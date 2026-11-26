package com.faisca.backend;

import com.faisca.backend.fornecedor.entity.FornecedorCategoria;
import com.faisca.backend.fornecedor.repository.FornecedorCategoriaRepository;
import com.faisca.backend.tarefa.entity.Prioridade;
import com.faisca.backend.tarefa.repository.PrioridadeRepository;
import com.faisca.backend.usuario.entity.Usuario;
import com.faisca.backend.usuario.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner init(UsuarioRepository repository,
                           PrioridadeRepository prioridades,
                           FornecedorCategoriaRepository fornecedorCategorias,
                           PasswordEncoder encoder) {
        return args -> {
            Optional<Usuario> usuarioExistente = repository.findByEmail("teste@faisca.com");

            if (usuarioExistente.isEmpty()) {
                Usuario u = new Usuario();
                u.setNome("Teste");
                u.setSobrenome("Silva");
                u.setEmail("teste@faisca.com");
                u.setCargo("Administrador");
                u.setSenha(encoder.encode("123456"));
                repository.save(u);
            } else {
                Usuario usuario = usuarioExistente.get();
                String senhaAtual = usuario.getSenha();

                if (senhaAtual != null && !senhaAtual.startsWith("$2a$")
                        && !senhaAtual.startsWith("$2b$")
                        && !senhaAtual.startsWith("$2y$")) {
                    usuario.setSenha(encoder.encode(senhaAtual));
                    repository.save(usuario);
                }
            }

            if (prioridades.count() == 0) {
                for (String nome : List.of("Baixa", "Moderada", "Média", "Alta", "Crítica")) {
                    Prioridade p = new Prioridade();
                    p.setNome(nome);
                    prioridades.save(p);
                }
            }

            if (fornecedorCategorias.count() == 0) {
                for (String nome : List.of("Marcenaria", "Iluminação", "Marmoraria", "Paisagismo", "Construção", "Instalações", "Materiais", "Outros")) {
                    FornecedorCategoria cat = new FornecedorCategoria();
                    cat.setNome(nome);
                    fornecedorCategorias.save(cat);
                }
            }
        };
    }
}
