package com.faisca.backend.fornecedor.service;

import com.faisca.backend.fornecedor.dto.*;
import com.faisca.backend.fornecedor.entity.*;
import com.faisca.backend.fornecedor.repository.*;
import com.faisca.backend.projeto.entity.Projeto;
import com.faisca.backend.projeto.repository.ProjetoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
@Transactional
public class FornecedorService {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}$");

    private final FornecedorEmpresaRepository empresas;
    private final FornecedorCategoriaRepository categorias;
    private final FornecedorContatoRepository contatos;
    private final FornecedorProjetoRepository fornecedorProjetos;
    private final ProjetoRepository projetos;

    public FornecedorService(FornecedorEmpresaRepository empresas,
                             FornecedorCategoriaRepository categorias,
                             FornecedorContatoRepository contatos,
                             FornecedorProjetoRepository fornecedorProjetos,
                             ProjetoRepository projetos) {
        this.empresas = empresas;
        this.categorias = categorias;
        this.contatos = contatos;
        this.fornecedorProjetos = fornecedorProjetos;
        this.projetos = projetos;
    }

    @Transactional(readOnly = true)
    public List<FornecedorResponse> listar() {
        return empresas.findAllByOrderByNomeAsc().stream().map(FornecedorResponse::new).toList();
    }

    @Transactional(readOnly = true)
    public FornecedorResponse buscar(Integer id) {
        return new FornecedorResponse(obterEmpresa(id));
    }

    public FornecedorResponse criar(FornecedorRequest request) {
        FornecedorEmpresa empresa = new FornecedorEmpresa();
        preencherBasico(empresa, request);
        empresa = empresas.saveAndFlush(empresa);
        aplicarRelacionamentos(empresa, request);
        return new FornecedorResponse(empresas.saveAndFlush(empresa));
    }

    public FornecedorResponse atualizar(Integer id, FornecedorRequest request) {
        FornecedorEmpresa empresa = obterEmpresa(id);
        preencherBasico(empresa, request);
        aplicarRelacionamentos(empresa, request);
        return new FornecedorResponse(empresas.saveAndFlush(empresa));
    }

    public void excluir(Integer id) {
        FornecedorEmpresa empresa = obterEmpresa(id);
        empresas.delete(empresa);
    }

    // -------- Categorias (fornecedor_categoria) --------

    @Transactional(readOnly = true)
    public List<FornecedorCategoriaResponse> listarCategorias() {
        return categorias.findAllByOrderByNomeAsc().stream().map(FornecedorCategoriaResponse::new).toList();
    }

    public FornecedorCategoriaResponse criarCategoria(FornecedorCategoriaRequest request) {
        String nome = request.getNome().trim();
        if (categorias.existsByNomeIgnoreCase(nome)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Categoria de fornecedor já cadastrada");
        }
        FornecedorCategoria categoria = new FornecedorCategoria();
        categoria.setNome(nome);
        return new FornecedorCategoriaResponse(categorias.save(categoria));
    }

    public FornecedorCategoriaResponse atualizarCategoria(Integer id, FornecedorCategoriaRequest request) {
        FornecedorCategoria categoria = categorias.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada"));
        String nome = request.getNome().trim();
        if (categorias.existsByNomeIgnoreCaseAndIdNot(nome, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Categoria de fornecedor já cadastrada");
        }
        categoria.setNome(nome);
        return new FornecedorCategoriaResponse(categorias.save(categoria));
    }

    public void excluirCategoria(Integer id) {
        FornecedorCategoria categoria = categorias.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada"));
        if (empresas.existsByCategoriasId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Categoria vinculada a fornecedores");
        }
        categorias.delete(categoria);
    }

    // -------- Contatos (fornecedor_contato) --------

    @Transactional(readOnly = true)
    public List<FornecedorContatoResponse> listarContatos(Integer fornecedorId) {
        obterEmpresa(fornecedorId);
        return contatos.findByFornecedorIdOrderByIdAsc(fornecedorId).stream()
                .map(FornecedorContatoResponse::new)
                .toList();
    }

    public FornecedorContatoResponse criarContato(Integer fornecedorId, FornecedorContatoRequest request) {
        FornecedorEmpresa empresa = obterEmpresa(fornecedorId);
        validarContato(request);
        FornecedorContato contato = new FornecedorContato();
        contato.setFornecedor(empresa);
        contato.setNome(blank(request.getNome()) ? empresa.getNome() : request.getNome().trim());
        contato.setEmail(trimToNull(request.getEmail()));
        contato.setTelefone(normalizarTelefone(request.getTelefone()));
        return new FornecedorContatoResponse(contatos.save(contato));
    }

    public FornecedorContatoResponse atualizarContato(Integer fornecedorId, Integer contatoId, FornecedorContatoRequest request) {
        obterEmpresa(fornecedorId);
        validarContato(request);
        FornecedorContato contato = contatos.findByIdAndFornecedorId(contatoId, fornecedorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contato não encontrado"));
        if (!blank(request.getNome())) contato.setNome(request.getNome().trim());
        contato.setEmail(trimToNull(request.getEmail()));
        contato.setTelefone(normalizarTelefone(request.getTelefone()));
        return new FornecedorContatoResponse(contatos.save(contato));
    }

    public void excluirContato(Integer fornecedorId, Integer contatoId) {
        obterEmpresa(fornecedorId);
        FornecedorContato contato = contatos.findByIdAndFornecedorId(contatoId, fornecedorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contato não encontrado"));
        contatos.delete(contato);
    }

    // -------- Projetos do fornecedor (fornecedor_projeto) --------

    @Transactional(readOnly = true)
    public List<FornecedorProjetoResponse> listarProjetos(Integer fornecedorId) {
        obterEmpresa(fornecedorId);
        return fornecedorProjetos.findByFornecedorId(fornecedorId).stream()
                .map(FornecedorProjetoResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FornecedorProjetoResponse> listarPorProjeto(Integer projetoId) {
        if (!projetos.existsById(projetoId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Projeto não encontrado");
        }
        return fornecedorProjetos.findByProjetoId(projetoId).stream()
                .map(FornecedorProjetoResponse::new)
                .toList();
    }

    public FornecedorProjetoResponse vincularProjeto(Integer fornecedorId, FornecedorProjetoRequest request) {
        FornecedorEmpresa empresa = obterEmpresa(fornecedorId);
        Projeto projeto = projetos.findById(request.getProjetoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projeto não encontrado"));
        validarDatasProjeto(request);
        FornecedorProjetoId id = new FornecedorProjetoId(fornecedorId, projeto.getId());
        FornecedorProjeto vinculo = fornecedorProjetos.findById(id).orElseGet(() -> {
            FornecedorProjeto fp = new FornecedorProjeto();
            fp.setFornecedor(empresa);
            fp.setProjeto(projeto);
            return fp;
        });
        vinculo.setServico(trimToNull(request.getServico()));
        vinculo.setValor(request.getValor());
        vinculo.setDataInicio(request.getDataInicio());
        vinculo.setDataFim(request.getDataFim());
        empresa.setJaTrabalhei(true);
        return new FornecedorProjetoResponse(fornecedorProjetos.save(vinculo));
    }

    public FornecedorProjetoResponse atualizarVinculoProjeto(Integer fornecedorId, Integer projetoId, FornecedorProjetoRequest request) {
        obterEmpresa(fornecedorId);
        validarDatasProjeto(request);
        FornecedorProjetoId id = new FornecedorProjetoId(fornecedorId, projetoId);
        FornecedorProjeto vinculo = fornecedorProjetos.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vínculo fornecedor-projeto não encontrado"));
        vinculo.setServico(trimToNull(request.getServico()));
        vinculo.setValor(request.getValor());
        vinculo.setDataInicio(request.getDataInicio());
        vinculo.setDataFim(request.getDataFim());
        return new FornecedorProjetoResponse(fornecedorProjetos.save(vinculo));
    }

    public void desvincularProjeto(Integer fornecedorId, Integer projetoId) {
        FornecedorEmpresa empresa = obterEmpresa(fornecedorId);
        FornecedorProjetoId id = new FornecedorProjetoId(fornecedorId, projetoId);
        FornecedorProjeto vinculo = fornecedorProjetos.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vínculo fornecedor-projeto não encontrado"));
        fornecedorProjetos.delete(vinculo);
        empresa.getProjetos().removeIf(fp -> fp.getProjeto().getId().equals(projetoId));
    }

    // -------- Auxiliares --------

    private FornecedorEmpresa obterEmpresa(Integer id) {
        return empresas.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor não encontrado"));
    }

    private void preencherBasico(FornecedorEmpresa empresa, FornecedorRequest r) {
        String nome = firstNonBlank(r.getNome(), r.getName());
        if (blank(nome)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome do fornecedor é obrigatório");
        }
        String categoriaLegada = firstNonBlank(r.getCategoria(), r.getCategory());
        boolean temCategorias = !blank(categoriaLegada)
                || (r.getCategoriasIds() != null && !r.getCategoriasIds().isEmpty())
                || (r.getCategorias() != null && r.getCategorias().stream().anyMatch(c -> !blank(c)));
        if (!temCategorias) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe ao menos uma categoria para o fornecedor");
        }
        String cidade = firstNonBlank(r.getCidade(), r.getCity());
        if (blank(cidade)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cidade é obrigatória");
        }
        validarContatoPrincipal(r);

        empresa.setNome(nome.trim());
        empresa.setCidade(cidade.trim());
        empresa.setOrigem(trimToNull(r.getOrigem()));
        String uf = trimToNull(r.getEstado());
        empresa.setEstado(uf == null ? null : uf.toUpperCase());
        empresa.setObservacao(trimToNull(firstNonBlank(r.getObservacao(), r.getNote())));
        if (r.getJaTrabalhei() != null) {
            empresa.setJaTrabalhei(r.getJaTrabalhei());
        }
    }

    private void aplicarRelacionamentos(FornecedorEmpresa empresa, FornecedorRequest r) {
        // 1) Categorias N:N (fornecedor_categ_assoc)
        Set<FornecedorCategoria> selecionadas = new LinkedHashSet<>();
        if (r.getCategoriasIds() != null) {
            for (Integer catId : r.getCategoriasIds()) {
                if (catId == null) continue;
                FornecedorCategoria cat = categorias.findById(catId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada: " + catId));
                selecionadas.add(cat);
            }
        }
        List<String> nomesCategorias = new ArrayList<>();
        if (r.getCategorias() != null) nomesCategorias.addAll(r.getCategorias());
        String catLegada = firstNonBlank(r.getCategoria(), r.getCategory());
        if (!blank(catLegada)) nomesCategorias.add(catLegada);
        for (String nomeCat : nomesCategorias) {
            if (blank(nomeCat)) continue;
            String limpo = nomeCat.trim();
            FornecedorCategoria cat = categorias.findByNomeIgnoreCase(limpo)
                    .orElseGet(() -> {
                        FornecedorCategoria nova = new FornecedorCategoria();
                        nova.setNome(limpo);
                        return categorias.save(nova);
                    });
            selecionadas.add(cat);
        }
        empresa.getCategorias().clear();
        empresa.getCategorias().addAll(selecionadas);

        // 2) Contatos 1:N (fornecedor_contato)
        if (r.getContatos() != null) {
            empresa.getContatos().clear();
            for (FornecedorContatoRequest c : r.getContatos()) {
                if (blank(c.getNome()) && blank(c.getEmail()) && blank(c.getTelefone())) continue;
                FornecedorContato contato = new FornecedorContato();
                contato.setNome(blank(c.getNome()) ? empresa.getNome() : c.getNome().trim());
                contato.setEmail(trimToNull(c.getEmail()));
                contato.setTelefone(normalizarTelefone(c.getTelefone()));
                empresa.addContato(contato);
            }
        } else if (r.getPhone() != null || r.getEmail() != null || r.getPhone2() != null || r.getEmail2() != null) {
            empresa.getContatos().clear();
            if (!blank(r.getPhone()) || !blank(r.getEmail())) {
                FornecedorContato principal = new FornecedorContato();
                principal.setNome("Contato Principal");
                principal.setTelefone(normalizarTelefone(r.getPhone()));
                principal.setEmail(trimToNull(r.getEmail()));
                empresa.addContato(principal);
            }
            if (!blank(r.getPhone2()) || !blank(r.getEmail2())) {
                FornecedorContato secundario = new FornecedorContato();
                secundario.setNome("Contato Secundário");
                secundario.setTelefone(normalizarTelefone(r.getPhone2()));
                secundario.setEmail(trimToNull(r.getEmail2()));
                empresa.addContato(secundario);
            }
        }

        // 3) Projetos N:N (fornecedor_projeto)
        if (r.getProjetosVinculados() != null) {
            empresa.getProjetos().clear();
            empresas.flush();
            for (FornecedorProjetoRequest fpReq : r.getProjetosVinculados()) {
                validarDatasProjeto(fpReq);
                Projeto projeto = projetos.findById(fpReq.getProjetoId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projeto não encontrado: " + fpReq.getProjetoId()));
                FornecedorProjeto fp = new FornecedorProjeto();
                fp.setFornecedor(empresa);
                fp.setProjeto(projeto);
                fp.setServico(trimToNull(fpReq.getServico()));
                fp.setValor(fpReq.getValor());
                fp.setDataInicio(fpReq.getDataInicio());
                fp.setDataFim(fpReq.getDataFim());
                empresa.addProjeto(fp);
            }
        } else if (r.getProjects() != null) {
            empresa.getProjetos().clear();
            empresas.flush();
            String[] codigos = r.getProjects().split("[,;]");
            Set<Integer> projetosAdicionados = new LinkedHashSet<>();
            for (String raw : codigos) {
                if (blank(raw)) continue;
                projetos.findByCodigoIgnoreCase(raw.trim()).ifPresent(projeto -> {
                    if (projetosAdicionados.add(projeto.getId())) {
                        FornecedorProjeto fp = new FornecedorProjeto();
                        fp.setFornecedor(empresa);
                        fp.setProjeto(projeto);
                        empresa.addProjeto(fp);
                    }
                });
            }
        }

        if (r.getJaTrabalhei() == null) {
            boolean temProjetosTexto = !blank(r.getProjects());
            empresa.setJaTrabalhei(!empresa.getProjetos().isEmpty() || temProjetosTexto);
        }
    }

    private void validarContatoPrincipal(FornecedorRequest r) {
        String telefonePrincipal;
        String emailPrincipal;
        if (r.getContatos() != null) {
            FornecedorContatoRequest principal = r.getContatos().isEmpty() ? null : r.getContatos().get(0);
            telefonePrincipal = principal != null ? principal.getTelefone() : null;
            emailPrincipal = principal != null ? principal.getEmail() : null;
        } else {
            telefonePrincipal = r.getPhone();
            emailPrincipal = r.getEmail();
        }
        if (blank(telefonePrincipal)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Telefone 1 é obrigatório");
        }
        if (!PHONE_PATTERN.matcher(telefonePrincipal.trim()).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Telefone 1 inválido");
        }
        if (blank(emailPrincipal)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail 1 é obrigatório");
        }
    }

    private void validarContato(FornecedorContatoRequest r) {
        if (blank(r.getNome()) && blank(r.getEmail()) && blank(r.getTelefone())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe ao menos nome, e-mail ou telefone do contato");
        }
    }

    private void validarDatasProjeto(FornecedorProjetoRequest r) {
        if (r.getDataInicio() != null && r.getDataFim() != null && r.getDataFim().isBefore(r.getDataInicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data de fim anterior à data de início");
        }
    }

    private String normalizarTelefone(String telefone) {
        if (blank(telefone)) return null;
        String trimmed = telefone.trim();
        if (trimmed.length() <= 15) return trimmed;
        String digits = trimmed.replaceAll("\\D", "");
        return digits.length() > 15 ? digits.substring(0, 15) : digits;
    }

    private String firstNonBlank(String a, String b) {
        return !blank(a) ? a : (!blank(b) ? b : null);
    }

    private String trimToNull(String s) {
        return blank(s) ? null : s.trim();
    }

    private boolean blank(String s) {
        return s == null || s.isBlank();
    }
}
