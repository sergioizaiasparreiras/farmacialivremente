package br.com.livrementehomeopatia.backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.livrementehomeopatia.backend.dto.ClientUpdateDTO;
import br.com.livrementehomeopatia.backend.dto.ClientDTO;
import br.com.livrementehomeopatia.backend.model.Cart;
import br.com.livrementehomeopatia.backend.model.Client;
import br.com.livrementehomeopatia.backend.repository.CartRepository;
import br.com.livrementehomeopatia.backend.repository.ClientRepository;
import br.com.livrementehomeopatia.backend.validation.PartialUpdateValidator;
import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável pelas operações relacionadas a clientes.
 */
@RequiredArgsConstructor
@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final UserUpdateHelper userUpdateHelper;
    private final PasswordEncoder passwordEncoder;
    private final PartialUpdateValidator validator;
    private final CartRepository cartRepository;

    /**
     * Retorna todos os clientes cadastrados.
     *
     * @return lista de clientes
     */
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    /**
     * Cria um novo cliente com os dados informados.
     *
     * @param objDTO objeto contendo os dados do cliente
     * @return cliente criado
     */
    public Client create(ClientDTO objDTO) {
        Optional<Client> existing = clientRepository.findByEmail(objDTO.getEmail());
        if (existing.isPresent()) {
            throw new DataIntegrityViolationException("E-mail já cadastrado no sistema!");
        }

        objDTO.setId(null);
        Client newClient = new Client(objDTO);
        newClient.setPassword(passwordEncoder.encode(objDTO.getPassword()));

        newClient = clientRepository.save(newClient);

        Cart cart = new Cart();
        cart.setUser(newClient); 
        cartRepository.save(cart); 

        newClient.setCart(cart); 
        return clientRepository.save(newClient);
    }

    /**
     * Atualiza dados de um cliente existente com base no DTO informado.
     *
     * @param id  ID do cliente a ser atualizado
     * @param dto dados a serem atualizados
     */
    public void updateClient(Integer id, ClientUpdateDTO dto) {
        validator.validatePartial(dto);

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        userUpdateHelper.applyUserUpdates(client, dto);
        clientRepository.save(client);
    }
}
