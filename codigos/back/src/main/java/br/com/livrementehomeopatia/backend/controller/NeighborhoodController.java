package br.com.livrementehomeopatia.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.livrementehomeopatia.backend.infra.exception.NeighborhoodNotFoundException;
import br.com.livrementehomeopatia.backend.model.Neighborhood;
import br.com.livrementehomeopatia.backend.services.NeighborhoodService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/neighborhoods")
public class NeighborhoodController {

    @Autowired
    private NeighborhoodService neighborhoodService;

    /**
     * Cria um novo bairro.
     */
    @PostMapping
    public ResponseEntity<Neighborhood> createNeighborhood(@Valid @RequestBody Neighborhood neighborhood) {
        try {
            Neighborhood saved = neighborhoodService.save(neighborhood.getName(), neighborhood.getTax());
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }

    /**
     * Retorna todos os bairros cadastrados.
     */
    @GetMapping
    public ResponseEntity<List<Neighborhood>> getAllNeighborhoods() {
        return ResponseEntity.ok(neighborhoodService.findAll());
    }

    /**
     * Retorna um bairro pelo ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Neighborhood> getNeighborhoodById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(neighborhoodService.findById(id));
        } catch (NeighborhoodNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Atualiza um bairro existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Neighborhood> updateNeighborhood(
            @PathVariable Integer id,
            @Valid @RequestBody Neighborhood updatedNeighborhood) {
        try {
            Neighborhood updated = neighborhoodService.update(id, updatedNeighborhood.getName(),
                    updatedNeighborhood.getTax());
            return ResponseEntity.ok(updated);
        } catch (NeighborhoodNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }

    /**
     * Deleta um bairro pelo ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNeighborhood(@PathVariable Integer id) {
        try {
            neighborhoodService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (NeighborhoodNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}