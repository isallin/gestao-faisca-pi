package com.faisca.backend.usuario.service;

import com.faisca.backend.usuario.dto.*;
import com.faisca.backend.usuario.entity.Usuario;
import com.faisca.backend.usuario.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@Transactional
public class UsuarioService {
    private final UsuarioRepository repository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    public UsuarioService(UsuarioRepository repository) { this.repository = repository; }
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() { return repository.findAll().stream().map(this::response).toList(); }
    @Transactional(readOnly = true)
    public UsuarioResponse buscar(Integer id) { return response(obter(id)); }
    public UsuarioResponse criar(UsuarioCadastroRequest r) {
        String email = normalizarEmail(r.getEmail(), null);
        Usuario u = new Usuario();
        u.setNome(r.getNome().trim()); u.setSobrenome(r.getSobrenome().trim());
        u.setEmail(email); u.setCargo(r.getCargo().trim()); u.setSenha(encoder.encode(r.getSenha()));
        return response(repository.save(u));
    }
    public UsuarioResponse atualizar(Integer id, UsuarioPutRequest r) {
        Usuario u = obter(id);
        u.setEmail(normalizarEmail(r.getEmail(), id));
        u.setNome(r.getNome().trim()); u.setSobrenome(r.getSobrenome().trim()); u.setCargo(r.getCargo().trim());
        // Alteração de senha é separada do gerenciamento de perfil.
        return response(repository.save(u));
    }
    public UsuarioResponse atualizarParcial(Integer id, UsuarioPatchRequest r) {
        Usuario u = obter(id);
        if (r.getEmail() != null) u.setEmail(normalizarEmail(r.getEmail(), id));
        if (r.getNome() != null) u.setNome(r.getNome().trim());
        if (r.getSobrenome() != null) u.setSobrenome(r.getSobrenome().trim());
        if (r.getCargo() != null) u.setCargo(r.getCargo().trim());
        return response(repository.save(u));
    }
    public void excluir(Integer id, Integer solicitante) {
        Usuario u = obter(id);
        if (id.equals(solicitante) || repository.count() <= 1)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é permitido excluir a própria conta ou a última conta");
        repository.delete(u);
    }
    private Usuario obter(Integer id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }
    private String normalizarEmail(String email, Integer id) {
        String normalizado = email.trim().toLowerCase();
        if (id == null ? repository.existsByEmail(normalizado) : repository.existsByEmailAndIdNot(normalizado, id))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        return normalizado;
    }
    private UsuarioResponse response(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getSobrenome(), u.getEmail(), u.getCargo());
    }
}
