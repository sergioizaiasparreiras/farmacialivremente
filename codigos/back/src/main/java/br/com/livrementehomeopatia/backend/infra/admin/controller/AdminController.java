package br.com.livrementehomeopatia.backend.infra.admin.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import br.com.livrementehomeopatia.backend.dto.UserDTO;
import br.com.livrementehomeopatia.backend.infra.admin.service.AdminService;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // Listar usuários não-admins com filtro
    @GetMapping("/users")
    public ResponseEntity<Page<UserDTO>> listRegularUsers(
            @RequestParam(required = false) String filter,
            @PageableDefault(size = 10, sort = "fullName") Pageable pageable) {
        
        return ResponseEntity.ok(adminService.listRegularUsers(filter, pageable));
    }

    // Buscar usuário por ID
    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Integer id) {  // Alterado de Long para Integer
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    // Atualizar role do usuário
    @PatchMapping("/users/{id}/role")
    public ResponseEntity<Void> updateUserRole(
            @PathVariable Integer id,  // Alterado de Long para Integer
            @RequestParam String newRole) {
        
        adminService.updateUserRole(id, newRole);
        return ResponseEntity.noContent().build();
    }

    // Ativar/desativar usuário
    @PatchMapping("/users/{id}/status")
    public ResponseEntity<Void> setUserActiveStatus(
            @PathVariable Integer id,  // Alterado de Long para Integer
            @RequestParam boolean active) {
        
        adminService.setUserActiveStatus(id, active);
        return ResponseEntity.noContent().build();
    }
}