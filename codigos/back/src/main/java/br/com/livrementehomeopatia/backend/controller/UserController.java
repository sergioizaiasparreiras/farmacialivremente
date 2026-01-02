package br.com.livrementehomeopatia.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import br.com.livrementehomeopatia.backend.dto.ClientUpdateDTO;
import br.com.livrementehomeopatia.backend.dto.DoctorUpdateDTO;
import br.com.livrementehomeopatia.backend.dto.DoctorDTO;
import br.com.livrementehomeopatia.backend.dto.ClientDTO;
import br.com.livrementehomeopatia.backend.infra.security.LoggedUser;
import br.com.livrementehomeopatia.backend.model.Client;
import br.com.livrementehomeopatia.backend.model.Doctor;
import br.com.livrementehomeopatia.backend.model.User;
import br.com.livrementehomeopatia.backend.repository.ClientRepository;
import br.com.livrementehomeopatia.backend.repository.DoctorRepository;
import br.com.livrementehomeopatia.backend.repository.UserRepository;
import br.com.livrementehomeopatia.backend.services.ClientService;
import br.com.livrementehomeopatia.backend.services.DoctorService;
import br.com.livrementehomeopatia.backend.validation.OnFieldPresent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository repository;
    private final ClientRepository clienteRepository;
    private final DoctorRepository medicoRepository;
    private final ClientService clientService;
    private final DoctorService doctorService;

    @GetMapping
    public ResponseEntity<String> getUser() {
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/clientes")
    public ResponseEntity<List<Client>> getAllClientes() {
        return ResponseEntity.ok(clienteRepository.findAll());
    }

    @GetMapping("/medicos")
    public ResponseEntity<List<Doctor>> getAllMedicos() {
        return ResponseEntity.ok(medicoRepository.findAll());
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getAuthenticatedUserProfile(@AuthenticationPrincipal LoggedUser loggedUser) {
        if (loggedUser.getRole().equals("ROLE_CLIENTE")) {
            Client cliente = clienteRepository.findById(loggedUser.getId())
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
            return ResponseEntity.ok(new ClientDTO(cliente));
        }

        if (loggedUser.getRole().equals("ROLE_MEDICO")) {
            Doctor medico = medicoRepository.findById(loggedUser.getId())
                    .orElseThrow(() -> new RuntimeException("Médico não encontrado"));
            return ResponseEntity.ok(new DoctorDTO(medico));
        }

        return ResponseEntity.badRequest().body("Perfil não suportado.");
    }

    @PutMapping("/profile/client")
    public ResponseEntity<?> updateClientProfile(@AuthenticationPrincipal LoggedUser loggedUser,
                                                 @RequestBody @Validated(OnFieldPresent.class) ClientUpdateDTO dto) {
        clientService.updateClient(loggedUser.getId(), dto);
        return ResponseEntity.ok("Client profile updated successfully.");
    }

    @PutMapping("/profile/doctor")
    public ResponseEntity<?> updateDoctorProfile(@AuthenticationPrincipal LoggedUser loggedUser,
                                                 @RequestBody @Validated(OnFieldPresent.class) DoctorUpdateDTO dto) {
        doctorService.updateDoctor(loggedUser.getId(), dto);
        return ResponseEntity.ok("Doctor profile updated successfully.");
    }
}
