package com.faisca.backend.usuario.service;

import com.faisca.backend.calendario.repository.GoogleEventRepository;
import com.faisca.backend.calendario.repository.GoogleTokenRepository;
import com.faisca.backend.projeto.entity.Projeto;
import com.faisca.backend.tarefa.dto.ColaboradorTarefaResponse;
import com.faisca.backend.tarefa.entity.ColaboradorTarefa;
import com.faisca.backend.tarefa.repository.ColaboradorTarefaRepository;
import com.faisca.backend.tarefa.repository.RegistroHorasRepository;
import com.faisca.backend.usuario.dto.*;
import com.faisca.backend.usuario.entity.Usuario;
import com.faisca.backend.usuario.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;
    private final ColaboradorTarefaRepository colaboradorTarefas;
    private final RegistroHorasRepository registroHoras;
    private final GoogleTokenRepository googleTokens;
    private final GoogleEventRepository googleEvents;

    public UsuarioService(UsuarioRepository repository,
                          PasswordEncoder encoder,
                          ColaboradorTarefaRepository colaboradorTarefas,
                          RegistroHorasRepository registroHoras,
                          GoogleTokenRepository googleTokens,
                          GoogleEventRepository googleEvents) {
        this.repository = repository;
        this.encoder = encoder;
        this.colaboradorTarefas = colaboradorTarefas;
        this.registroHoras = registroHoras;
        this.googleTokens = googleTokens;
        this.googleEvents = googleEvents;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return repository.findAll().stream().map(this::response).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscar(Integer id) {
        return response(obter(id));
    }

    @Transactional(readOnly = true)
    public List<ColaboradorTarefaResponse> listarTarefas(Integer usuarioId) {
        obter(usuarioId);
        return colaboradorTarefas.findByIdPkColaborador(usuarioId).stream()
                .map(ColaboradorTarefaResponse::new)
                .toList();
    }

    public UsuarioResponse criar(UsuarioCadastroRequest r) {
        String email = normalizarEmail(r.getEmail(), null);
        Usuario u = new Usuario();
        u.setNome(r.getNome().trim());
        u.setSobrenome(r.getSobrenome().trim());
        u.setEmail(email);
        u.setCargo(trimSafe(r.getCargo()));
        u.setSenha(encoder.encode(r.getSenha()));
        return response(repository.save(u));
    }

    public UsuarioResponse atualizar(Integer id, UsuarioPutRequest r) {
        Usuario u = obter(id);
        u.setEmail(normalizarEmail(r.getEmail(), id));
        u.setNome(r.getNome().trim());
        u.setSobrenome(r.getSobrenome().trim());
        u.setCargo(trimSafe(r.getCargo()));
        if (r.getSenha() != null && !r.getSenha().isBlank()) {
            u.setSenha(encoder.encode(r.getSenha()));
        }
        return response(repository.save(u));
    }

    public UsuarioResponse atualizarParcial(Integer id, UsuarioPatchRequest r) {
        Usuario u = obter(id);
        if (r.getEmail() != null) u.setEmail(normalizarEmail(r.getEmail(), id));
        if (r.getNome() != null) u.setNome(r.getNome().trim());
        if (r.getSobrenome() != null) u.setSobrenome(r.getSobrenome().trim());
        if (r.getCargo() != null) u.setCargo(trimSafe(r.getCargo()));
        if (r.getSenha() != null && !r.getSenha().isBlank()) {
            u.setSenha(encoder.encode(r.getSenha()));
        }
        return response(repository.save(u));
    }

    public void excluir(Integer id, Integer solicitante) {
        Usuario u = obter(id);
        if (id.equals(solicitante) || repository.count() <= 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é permitido excluir a própria conta ou a última conta");
        }
        if (registroHoras.existsByColaboradorId(id) || colaboradorTarefas.existsByIdPkColaborador(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Colaborador possui tarefas ou registros de horas vinculados");
        }
        googleEvents.deleteByFkUsuario(id);
        googleTokens.deleteByUsuarioId(id);
        repository.delete(u);
    }

    private Usuario obter(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }

    private String normalizarEmail(String email, Integer id) {
        String normalizado = email.trim().toLowerCase();
        if (id == null ? repository.existsByEmail(normalizado) : repository.existsByEmailAndIdNot(normalizado, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }
        return normalizado;
    }

    private String trimSafe(String valor) {
        return valor == null ? null : valor.trim();
    }

    private UsuarioResponse response(Usuario u) {
        List<ColaboradorTarefa> atribuicoes = u.getId() == null
                ? List.of()
                : colaboradorTarefas.findByIdPkColaborador(u.getId());
        Map<Integer, String> projetosUnicos = new LinkedHashMap<>();
        List<Integer> tarefasIds = new ArrayList<>();
        for (ColaboradorTarefa ct : atribuicoes) {
            if (ct.getTarefa() == null) continue;
            tarefasIds.add(ct.getTarefa().getId());
            Projeto projeto = ct.getTarefa().getProjeto();
            if (projeto != null && projeto.getId() != null && projeto.getCodigo() != null) {
                projetosUnicos.putIfAbsent(projeto.getId(), projeto.getCodigo());
            }
        }
        List<Integer> projetosIds = new ArrayList<>(projetosUnicos.keySet());
        List<String> projetosCodigos = new ArrayList<>(projetosUnicos.values());
        String projetosResumo = String.join(", ", projetosCodigos);
        return new UsuarioResponse(
                u.getId(),
                u.getNome(),
                u.getSobrenome(),
                u.getEmail(),
                u.getCargo() == null ? "" : u.getCargo(),
                projetosResumo,
                projetosIds,
                projetosCodigos,
                tarefasIds
        );
    }
}
