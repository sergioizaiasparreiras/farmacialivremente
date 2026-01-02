package br.com.livrementehomeopatia.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import br.com.livrementehomeopatia.backend.dto.ClientDTO;
import br.com.livrementehomeopatia.backend.dto.ForgotPasswordDTO;
import br.com.livrementehomeopatia.backend.dto.LoginRequestDTO;
import br.com.livrementehomeopatia.backend.dto.RegisterResponseDTO;
import br.com.livrementehomeopatia.backend.dto.DoctorDTO;
import br.com.livrementehomeopatia.backend.dto.ResetPasswordDTO;
import br.com.livrementehomeopatia.backend.dto.ResponseDTO;
import br.com.livrementehomeopatia.backend.infra.security.TokenService;
import br.com.livrementehomeopatia.backend.model.Client;
import br.com.livrementehomeopatia.backend.model.Doctor;
import br.com.livrementehomeopatia.backend.model.User;
import br.com.livrementehomeopatia.backend.repository.UserRepository;
import br.com.livrementehomeopatia.backend.services.ClientService;
import br.com.livrementehomeopatia.backend.services.DoctorService;
import br.com.livrementehomeopatia.backend.services.PasswordResetService;
import br.com.livrementehomeopatia.backend.validation.CrmNationalValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador responsável pelas operações de autenticação,
 * incluindo login, registro de usuários e recuperação de senha.
 * Disponibiliza endpoints para autenticação de clientes e médicos.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository repository;
    private final ClientService clientService;
    private final DoctorService doctorService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final PasswordResetService passwordResetService;
    private final CrmNationalValidator crmNationalValidator;

    /**
     * Realiza o login do usuário com base nas credenciais fornecidas.
     *
     * @param body Objeto contendo o e-mail e a senha do usuário.
     * @return {@code ResponseEntity<ResponseDTO>} contendo o nome do usuário e o
     *         token gerado, ou {@code badRequest()} se as credenciais forem inválidas.
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody LoginRequestDTO body) {
        User user = this.repository.findByEmail(body.email())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (passwordEncoder.matches(body.password(), user.getPassword())) {
            String token = this.tokenService.generationToken(user);

            return ResponseEntity.ok(new ResponseDTO(
                token,
                user.getFullName(),
                user.getEmail(),
                user.getRole().name()    
            ));
        }

        return ResponseEntity.badRequest().build();
    }

    /**
     * Registra um novo cliente com base nas informações fornecidas.
     *
     * @param body Objeto contendo os dados do cliente.
     * @return {@code ResponseEntity<ResponseDTO>} com nome e token.
     */
    @PostMapping("/register/client")
    public ResponseEntity<?> registerClient(@RequestBody ClientDTO body) {
        Client newCliente = clientService.create(body);
        String token = tokenService.generationToken(newCliente);
        return ResponseEntity.ok(new RegisterResponseDTO(newCliente.getFullName(), token));
       
    }

    /**
     * Registra um novo médico com base nas informações fornecidas.
     *
     * @param body Objeto contendo os dados do médico.
     * @return {@code ResponseEntity<ResponseDTO>} com nome e token.
     */
    @PostMapping("/register/doctor")
    public ResponseEntity<?> registerDoctor(@RequestBody @Valid DoctorDTO body) {
    Doctor newMedico = doctorService.create(body);
    String token = tokenService.generationToken(newMedico);
    return ResponseEntity.ok(new RegisterResponseDTO(newMedico.getFullName(), token));
}

    /**
     * Inicia o processo de recuperação de senha para o e-mail fornecido.
     *
     * @param dto Objeto contendo o e-mail do usuário que deseja recuperar a senha.
     * @return {@code ResponseEntity<?>} com mensagem de confirmação.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordDTO dto) {
        passwordResetService.requestPasswordRecovery(dto.getEmail());   
        return ResponseEntity.ok("Email Validated");
    }

    /**
     * Realiza a alteração da senha do usuário utilizando o token de recuperação.
     *
     * @param dto Objeto contendo o token de recuperação e a nova senha.
     * @return {@code ResponseEntity<?>} com mensagem de confirmação.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDTO dto) {
        passwordResetService.passwordReset(dto.getToken(), dto.getNewPassword());
        return ResponseEntity.ok("Password reset successfully");
    }

@GetMapping("/check-crm")
    public ResponseEntity<Boolean> checkCrm(@RequestParam String crm) {
        boolean isValid = crmNationalValidator.isCrmValid(crm);
        return ResponseEntity.ok(isValid);
    }
}