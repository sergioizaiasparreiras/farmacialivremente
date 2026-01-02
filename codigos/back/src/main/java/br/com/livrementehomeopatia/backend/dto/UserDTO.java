package br.com.livrementehomeopatia.backend.dto;

import br.com.livrementehomeopatia.backend.enums.Role;
import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Integer id;
    private String fullName;
    private String email;
    private Role role;
    private LocalDate createdDate;
    private boolean active;
}