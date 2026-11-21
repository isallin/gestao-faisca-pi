package com.faisca.backend;

import com.faisca.backend.usuario.entity.Usuario;
import com.faisca.backend.usuario.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    CommandLineRunner init(UsuarioRepository repository, PasswordEncoder encoder) {
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
                return;
            }

            Usuario usuario = usuarioExistente.get();
            String senhaAtual = usuario.getSenha();

            if (senhaAtual != null && !senhaAtual.startsWith("$2a$")
                    && !senhaAtual.startsWith("$2b$")
                    && !senhaAtual.startsWith("$2y$")) {
                usuario.setSenha(encoder.encode(senhaAtual));
                repository.save(usuario);
            }
        };
    }
}
