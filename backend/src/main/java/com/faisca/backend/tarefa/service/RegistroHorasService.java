package com.faisca.backend.tarefa.service;

import com.faisca.backend.calendario.service.CalendarSyncService;
import com.faisca.backend.calendario.service.EventoCalendario;
import com.faisca.backend.calendario.service.SyncCalendarioEvent;
import com.faisca.backend.tarefa.dto.RegistroHorasRequest;
import com.faisca.backend.tarefa.dto.RegistroHorasResponse;
import com.faisca.backend.tarefa.entity.ColaboradorTarefa;
import com.faisca.backend.tarefa.entity.ColaboradorTarefaId;
import com.faisca.backend.tarefa.entity.RegistroHoras;
import com.faisca.backend.tarefa.entity.Tarefa;
import com.faisca.backend.tarefa.repository.ColaboradorTarefaRepository;
import com.faisca.backend.tarefa.repository.RegistroHorasRepository;
import com.faisca.backend.tarefa.repository.TarefaRepository;
import com.faisca.backend.usuario.entity.Usuario;
import com.faisca.backend.usuario.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class RegistroHorasService {

    private final RegistroHorasRepository registros;
    private final TarefaRepository tarefas;
    private final UsuarioRepository usuarios;
    private final ColaboradorTarefaRepository colaboradorTarefas;
    private final CalendarSyncService calendario;

    public RegistroHorasService(RegistroHorasRepository registros,
                                TarefaRepository tarefas,
                                UsuarioRepository usuarios,
                                ColaboradorTarefaRepository colaboradorTarefas,
                                CalendarSyncService calendario) {
        this.registros = registros;
        this.tarefas = tarefas;
        this.usuarios = usuarios;
        this.colaboradorTarefas = colaboradorTarefas;
        this.calendario = calendario;
    }

    @Transactional(readOnly = true)
    public List<RegistroHorasResponse> listar(Integer tarefaId, Integer colaboradorId) {
        List<RegistroHoras> lista;
        if (tarefaId != null && colaboradorId != null) {
            lista = registros.findByTarefaIdAndColaboradorId(tarefaId, colaboradorId);
        } else if (tarefaId != null) {
            lista = registros.findByTarefaId(tarefaId);
        } else if (colaboradorId != null) {
            lista = registros.findByColaboradorId(colaboradorId);
        } else {
            lista = registros.findAll();
        }
        return lista.stream().map(RegistroHorasResponse::new).toList();
    }

    @Transactional(readOnly = true)
    public RegistroHorasResponse buscar(Integer id) {
        return new RegistroHorasResponse(obter(id));
    }

    public RegistroHorasResponse criar(RegistroHorasRequest request, Integer usuarioId) {
        return salvar(new RegistroHoras(), request, usuarioId);
    }

    public RegistroHorasResponse atualizar(Integer id, RegistroHorasRequest request, Integer usuarioId) {
        return salvar(obter(id), request, usuarioId);
    }

    public void excluir(Integer id) {
        registros.delete(obter(id));
        calendario.publicarExclusao(SyncCalendarioEvent.TipoEntidade.REGISTRO_HORAS, id);
    }

    private RegistroHoras obter(Integer id) {
        return registros.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro não encontrado"));
    }

    private RegistroHorasResponse salvar(RegistroHoras registro, RegistroHorasRequest request, Integer usuarioId) {
        boolean novo = registro.getId() == null;
        if (!request.fim().isAfter(request.inicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fim deve ser posterior ao início");
        }
        Tarefa tarefa = tarefas.findById(request.tarefaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarefa não encontrada"));

        Integer colaboradorId = request.colaboradorId();
        if (colaboradorId == null) {
            if (registro.getColaborador() != null) {
                colaboradorId = registro.getColaborador().getId();
            } else if (usuarioId != null) {
                colaboradorId = usuarioId;
            }
        }
        Usuario colaborador = null;
        if (colaboradorId != null) {
            colaborador = usuarios.findById(colaboradorId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Colaborador não encontrado"));
            garantirVinculoColaboradorTarefa(colaborador, tarefa);
        }

        registro.setTarefa(tarefa);
        registro.setColaborador(colaborador);
        registro.setHoraInicio(request.inicio());
        registro.setHoraFim(request.fim());
        registro.setDescricao(request.descricao() == null ? null : request.descricao().trim());
        registro.setUltimaAtualizacao(LocalDateTime.now());
        if (novo && usuarioId != null) {
            registro.setCriadoPor(usuarioId);
        }
        RegistroHoras salvo = registros.save(registro);
        if (novo) {
            calendario.publicarCriacao(SyncCalendarioEvent.TipoEntidade.REGISTRO_HORAS, salvo.getId(), usuarioId, EventoCalendario.deRegistro(salvo));
        } else {
            calendario.publicarAtualizacao(SyncCalendarioEvent.TipoEntidade.REGISTRO_HORAS, salvo.getId(), EventoCalendario.deRegistro(salvo));
        }
        return new RegistroHorasResponse(salvo);
    }

    private void garantirVinculoColaboradorTarefa(Usuario colaborador, Tarefa tarefa) {
        ColaboradorTarefaId id = new ColaboradorTarefaId(colaborador.getId(), tarefa.getId());
        if (!colaboradorTarefas.existsById(id)) {
            ColaboradorTarefa ct = new ColaboradorTarefa();
            ct.setColaborador(colaborador);
            ct.setTarefa(tarefa);
            ct.setDataAtribuicao(LocalDateTime.now());
            colaboradorTarefas.saveAndFlush(ct);
        }
    }
}
