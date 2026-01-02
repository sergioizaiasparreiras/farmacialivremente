package br.com.livrementehomeopatia.backend.services;

import br.com.livrementehomeopatia.backend.enums.ProductType;
import br.com.livrementehomeopatia.backend.dto.ProductDTO;
import br.com.livrementehomeopatia.backend.enums.Categories;
import br.com.livrementehomeopatia.backend.infra.exception.ExistingProductException;
import br.com.livrementehomeopatia.backend.infra.exception.ProductNotFoundException;
import br.com.livrementehomeopatia.backend.model.Product;
import br.com.livrementehomeopatia.backend.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Serviço responsável pelas operações relacionadas a produtos.
 */
@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Cria um novo produto utilizando dados separados e uma imagem multipart.
     *
     * @param name        nome do produto
     * @param description descrição do produto
     * @param price       preço do produto
     * @param available   disponibilidade do produto
     * @param type        tipo do produto
     * @param categories  categorias do produto
     * @param image       imagem do produto
     * @return produto criado
     * @throws IOException se ocorrer erro ao processar a imagem
     */
    @Transactional
    public Product create(String name, String description, Double price, boolean available,
            String type, List<String> categories, MultipartFile image) throws IOException {

        if (productAlreadyExists(name)) {
            throw new ExistingProductException("Produto com o nome '" + name + "' já existe.");
        }

        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setAvailable(available);

        product.setType(ProductType.valueOf(type));

        Set<Categories> categoriesEnum = categories.stream()
                .map(Categories::valueOf)
                .collect(Collectors.toSet());
        product.setCategories(categoriesEnum);

        product.setPhoto(image.getBytes());

        return repository.save(product);
    }

    /**
     * Atualiza um produto existente com base no ID fornecido e nos novos dados.
     * Campos nulos ou não informados serão ignorados.
     *
     * @param id          ID do produto a ser atualizado
     * @param name        novo nome do produto (opcional)
     * @param description nova descrição do produto (opcional)
     * @param price       novo preço do produto (opcional)
     * @param available   nova disponibilidade do produto (opcional)
     * @param type        novo tipo do produto (opcional, deve corresponder ao enum
     *                    ProductType)
     * @param categories  novas categorias do produto (opcional, deve corresponder
     *                    ao enum Categories)
     * @param image       nova imagem do produto em formato multipart (opcional)
     * @return produto atualizado e persistido no repositório
     * @throws IOException              se ocorrer erro ao processar a imagem
     * @throws EntityNotFoundException  se o produto não for encontrado
     * @throws ExistingProductException se já existir um produto com o mesmo nome
     */

    public Product update(Integer id, String name, String description, Double price, Boolean available,
            String type, List<String> categories, MultipartFile image) throws IOException {

        Product existingProduct = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com ID: " + id));

        applyUpdates(existingProduct, name, description, price, available, type, categories, image);

        return repository.save(existingProduct);
    }

    /**
     * Aplica atualizações ao produto existente, considerando apenas os campos não
     * nulos.
     *
     * @param product     produto existente a ser atualizado
     * @param name        novo nome do produto (opcional)
     * @param description nova descrição do produto (opcional)
     * @param price       novo preço do produto (opcional)
     * @param available   nova disponibilidade do produto (opcional)
     * @param type        novo tipo do produto (opcional)
     * @param categories  novas categorias do produto (opcional)
     * @param image       nova imagem do produto em formato multipart (opcional)
     * @throws IOException              se ocorrer erro ao processar a imagem
     * @throws ExistingProductException se o nome informado já existir em outro
     *                                  produto
     */
    private void applyUpdates(Product product, String name, String description, Double price, Boolean available,
            String type, List<String> categories, MultipartFile image) throws IOException {

        if (name != null && !name.isBlank() && !name.equals(product.getName())) {
            if (productAlreadyExists(name)) {
                throw new ExistingProductException("Produto com o nome '" + name + "' já existe.");
            }
            product.setName(name);
        }

        if (description != null && !description.isBlank()) {
            product.setDescription(description);
        }

        if (price != null) {
            product.setPrice(price);
        }

        if (available != null) {
            product.setAvailable(available);
        }

        if (type != null) {
            product.setType(ProductType.valueOf(type));
        }

        if (categories != null && !categories.isEmpty()) {
            Set<Categories> categoriesEnum = categories.stream()
                    .map(Categories::valueOf)
                    .collect(Collectors.toSet());
            product.setCategories(categoriesEnum);
        }

        if (image != null && !image.isEmpty()) {
            product.setPhoto(image.getBytes());
        }
    }

    /**
     * Verifica se um produto já existe com base no nome informado.
     *
     * @param name nome do produto
     * @return true se o produto já existir, false caso contrário
     */
    private boolean productAlreadyExists(String name) {
        return repository.findByName(name).isPresent();
    }

    /**
     * Deleta um produto pelo ID.
     *
     * @param id ID do produto a ser deletado
     */
    public void deleteById(Integer id) {
        Optional<Product> obj = repository.findById(id);

        if (obj.isEmpty()) {
            throw new ProductNotFoundException("Produto com ID " + id + " não encontrado.");
        }
        repository.deleteById(id);
    }

    /**
     * Deleta um produto pelo nome.
     *
     * @param name nome do produto a ser deletado
     */
    public void deleteByName(String name) {
        Optional<Product> obj = repository.findByName(name);

        if (obj.isEmpty()) {
            throw new ProductNotFoundException("Produto com nome '" + name + "' não encontrado.");
        }
        repository.delete(obj.get());
    }

    /**
     * Retorna todos os produtos cadastrados.
     *
     * @return lista de produtos
     */
    public List<Product> findAll() {
        return repository.findAll();
    }

    /**
     * Retorna um produto pelo ID.
     *
     * @param id ID do produto
     * @return produto encontrado
     */
    public Product findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Produto com ID " + id + " não encontrado."));
    }

   
    public ProductDTO updateProduct(Integer id,
            String nome,
            String descricao,
            String preco,
            boolean disponivel,
            String tipo,
            String categoriasJson,
            MultipartFile imagem) {

        Optional<Product> optionalProduct = repository.findById(id);
        if (!optionalProduct.isPresent()) {
            throw new RuntimeException("Produto com ID " + id + " não encontrado.");
        }

        Product product = optionalProduct.get();

        product.setName(nome);
        product.setDescription(descricao);
        product.setPrice(Double.parseDouble(preco));
        product.setAvailable(disponivel);

        if (tipo != null) {
            product.setType(ProductType.valueOf(tipo));
        }

        try {
            if (categoriasJson != null) {
                Set<Categories> categorias = objectMapper.readValue(categoriasJson,
                        new TypeReference<Set<Categories>>() {
                        });
                product.setCategories(categorias);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter categorias JSON: " + e.getMessage());
        }

        if (imagem != null && !imagem.isEmpty()) {
            try {
                product.setPhoto(imagem.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Erro ao processar imagem: " + e.getMessage());
            }
        }

        Product atualizado = repository.save(product);
        return new ProductDTO(atualizado);
    }
}
