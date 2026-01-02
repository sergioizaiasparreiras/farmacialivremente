package br.com.livrementehomeopatia.backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.livrementehomeopatia.backend.infra.exception.NeighborhoodNotFoundException;

import br.com.livrementehomeopatia.backend.model.Neighborhood;

import br.com.livrementehomeopatia.backend.repository.NeighborhoodRepository;

@Service
public class NeighborhoodService {

    @Autowired
    private NeighborhoodRepository neighborhoodRepository;

    /**
     * Salva um novo bairro.
     *
     * @param name nome do bairro
     * @param tax  taxa do bairro
     * @return o bairro salvo
     * @throws IllegalArgumentException se o bairro ja estiver cadastrado
     */
    public Neighborhood save(String name, Double tax) {

        if (neighborhoodRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Bairro ja cadastrado");
        }

        Neighborhood novoBairro = new Neighborhood();
        novoBairro.setId(null);
        novoBairro.setName(name);
        novoBairro.setTax(tax);

        return neighborhoodRepository.save(novoBairro);
    }

    /**
     * Retorna todos os bairros cadastrados.
     *
     * @return lista de bairros
     */
    public List<Neighborhood> findAll() {
        return neighborhoodRepository.findAll();
    }

    /**
     * Retorna um bairro pelo ID.
     *
     * @param id ID do bairro
     * @return o bairro encontrado
     * @throws NeighborhoodNotFoundException se o bairro não for encontrado
     */

    public Neighborhood findById(Integer id) {
        Optional<Neighborhood> obj = neighborhoodRepository.findById(id);
        return obj.orElseThrow(() -> new NeighborhoodNotFoundException("Bairro com ID " + id + " não encontrado."));
    }

    /**
     * Atualiza os dados de um bairro existente.
     *
     * @param id   ID do bairro a ser atualizado
     * @param name novo nome do bairro
     * @param tax  nova taxa do bairro
     * @return bairro atualizado
     * @throws NeighborhoodNotFoundException se o bairro não for encontrado
     * @throws IllegalArgumentException      se o novo nome já estiver em uso por
     *                                       outro bairro
     */
    public Neighborhood update(Integer id, String name, Double tax) {
        Neighborhood existing = neighborhoodRepository.findById(id)
                .orElseThrow(() -> new NeighborhoodNotFoundException("Bairro com ID " + id + " não encontrado."));

        Optional<Neighborhood> neighborhoodByName = neighborhoodRepository.findByName(name);

        if (neighborhoodByName.isPresent() && !neighborhoodByName.get().getId().equals(id)) {
            throw new IllegalArgumentException("Já existe um bairro com o nome '" + name + "'");
        }

        existing.setName(name);
        existing.setTax(tax);

        return neighborhoodRepository.save(existing);
    }

    /**
     * Deleta um bairro pelo ID.
     *
     * @param id ID do bairro a ser deletado
     * @throws NeighborhoodNotFoundException se o bairro não for encontrado
     */
    public void deleteById(Integer id) {
        Optional<Neighborhood> obj = neighborhoodRepository.findById(id);

        if (obj.isEmpty()) {
            throw new NeighborhoodNotFoundException("Bairro com ID " + id + " não encontrado.");
        }
        neighborhoodRepository.deleteById(id);
    }
}
