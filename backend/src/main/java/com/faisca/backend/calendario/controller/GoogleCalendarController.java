package com.faisca.backend.calendario.controller;

import com.faisca.backend.calendario.service.CalendarSyncService;
import com.faisca.backend.calendario.service.GoogleAuthService;
import com.faisca.backend.usuario.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

@RestController
@RequestMapping("/api/calendar")
@Tag(name = "Google Calendar", description = "Conexão da conta Google e sincronização automática de tarefas, projetos e registros de hora")
public class GoogleCalendarController {

    private final GoogleAuthService auth;
    private final CalendarSyncService sync;
    private final JwtUtil jwtUtil;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public GoogleCalendarController(GoogleAuthService auth, CalendarSyncService sync, JwtUtil jwtUtil) {
        this.auth = auth;
        this.sync = sync;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/status")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Status da conexão", description = "Informa se o usuário já conectou o Google Calendar.")
    @ApiResponse(responseCode = "200", description = "Status da conexão")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    public ResponseEntity<Map<String, Object>> status(@RequestAttribute("usuarioId") Integer usuarioId) {
        return ResponseEntity.ok(Map.of("connected", auth.isConectado(usuarioId)));
    }

    @GetMapping("/connect")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Iniciar conexão", description = "Retorna a URL do Google para o usuário autorizar o acesso ao calendário.")
    @ApiResponse(responseCode = "200", description = "URL de autorização")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    public ResponseEntity<Map<String, String>> connect(@RequestAttribute("usuarioId") Integer usuarioId)
            throws IOException, GeneralSecurityException {
        return ResponseEntity.ok(Map.of("url", auth.montarUrlAutorizacao(usuarioId)));
    }

    @GetMapping("/callback")
    @Operation(summary = "Callback do OAuth", description = "Recebido direto do Google após o usuário autorizar; não exige token.")
    @ApiResponse(responseCode = "302", description = "Redireciona de volta ao frontend")
    public void callback(@RequestParam("code") String code,
                         @RequestParam("state") String state,
                         HttpServletResponse response) throws IOException {
        String destino;
        try {
            Integer usuarioId = jwtUtil.validarStateGoogle(state);
            auth.conectar(usuarioId, code);
            sync.sincronizarPendentes(usuarioId);
            destino = frontendUrl + "/?google=connected";
        } catch (Exception e) {
            destino = frontendUrl + "/?google=error";
        }
        response.sendRedirect(destino);
    }

    @DeleteMapping("/conexao")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Desconectar", description = "Remove o vínculo com o Google Calendar. Os eventos já criados no Google permanecem.")
    @ApiResponse(responseCode = "204", description = "Desconectado")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    public ResponseEntity<Void> desconectar(@RequestAttribute("usuarioId") Integer usuarioId) {
        auth.desconectar(usuarioId);
        return ResponseEntity.noContent().build();
    }
}
