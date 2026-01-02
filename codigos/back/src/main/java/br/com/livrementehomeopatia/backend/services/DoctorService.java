package br.com.livrementehomeopatia.backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.livrementehomeopatia.backend.dto.DoctorUpdateDTO;
import br.com.livrementehomeopatia.backend.dto.DoctorDTO;
import br.com.livrementehomeopatia.backend.model.Cart;
import br.com.livrementehomeopatia.backend.model.Doctor;
import br.com.livrementehomeopatia.backend.repository.CartRepository;
import br.com.livrementehomeopatia.backend.repository.DoctorRepository;
import br.com.livrementehomeopatia.backend.validation.PartialUpdateValidator;
import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável pelas operações relacionadas a médicos no sistema.
 * Inclui funcionalidades de criação, listagem e atualização de dados de
 * médicos.
 */
@RequiredArgsConstructor
@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserUpdateHelper userUpdateHelper;
    private final PasswordEncoder passwordEncoder;
    private final PartialUpdateValidator validator;
    private final CartRepository cartRepository;

    /**
     * Retorna a lista de todos os médicos cadastrados no sistema.
     *
     * @return uma lista de objetos {@link Doctor}
     */
    public List<Doctor> findAll() {
        return doctorRepository.findAll();
    }

    /**
     * Cria um novo médico a partir de um DTO, após validar se o e-mail já está
     * cadastrado.
     * A senha e o CRM são criptografados antes do armazenamento.
     *
     * @param objDTO objeto {@link DoctorDTO} contendo os dados do médico a ser
     *               criado
     * @return o objeto {@link Doctor} recém-criado e persistido
     * @throws DataIntegrityViolationException se o e-mail já estiver cadastrado
     */
    public Doctor create(DoctorDTO objDTO) {
        objDTO.setId(null);

        Optional<Doctor> existingDoctor = doctorRepository.findByEmail(objDTO.getEmail());
        if (existingDoctor.isPresent()) {
            throw new DataIntegrityViolationException("E-mail já cadastrado no sistema!");
        }

        Doctor newObj = new Doctor(objDTO);
        newObj.setPassword(passwordEncoder.encode(objDTO.getPassword()));
        newObj.setCrm(passwordEncoder.encode(objDTO.getCrm()));
        newObj.setFullName(objDTO.getFullName());

        newObj = doctorRepository.save(newObj);

        Cart cart = new Cart();
        cart.setUser(newObj); 
        cartRepository.save(cart); 

        newObj.setCart(cart); 
        return doctorRepository.save(newObj);
    }

    /**
     * Atualiza os dados de um médico existente com base nas informações fornecidas
     * em um DTO.
     *
     * @param id  o ID do médico a ser atualizado
     * @param dto objeto {@link DoctorUpdateDTO} contendo os novos dados do médico
     * @throws RuntimeException se o médico com o ID fornecido não for encontrado
     */
    public void updateDoctor(Integer id, DoctorUpdateDTO dto) {
        validator.validatePartial(dto);
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        userUpdateHelper.applyUserUpdates(doctor, dto);
        doctorRepository.save(doctor);
    }
    /**
     * Verifica se um médico com o CRM fornecido já existe no sistema.
     *
     * @param crm o CRM do médico a ser verificado
     * @return true se o CRM já estiver cadastrado, false caso contrário
     */

public boolean existsByCrm(String crm) {
    return doctorRepository.existsByCrm(crm);
}
}