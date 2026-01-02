package br.com.livrementehomeopatia.backend.controller;

import br.com.livrementehomeopatia.backend.dto.ProductDTO;
import br.com.livrementehomeopatia.backend.infra.exception.ExistingProductException;
import br.com.livrementehomeopatia.backend.model.Product;
import br.com.livrementehomeopatia.backend.services.ProductService;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Controlador responsável pelo gerenciamento de produtos.
 */
@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * Cria um novo produto com imagem.
     *
     * @param name        nome do produto
     * @param description descrição do produto
     * @param price       preço do produto
     * @param available   disponibilidade do produto
     * @param type        tipo do produto
     * @param categories  categorias do produto
     * @param image       imagem do produto
     * @return produto criado ou mensagem de erro
     */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> create(
            @RequestParam("nome") String name,
            @RequestParam("descricao") String description,
            @RequestParam("preco") Double price,
            @RequestParam("disponivel") boolean available,
            @RequestParam("tipo") String type,
            @RequestParam("categorias") List<String> categories,
            @RequestParam("imagem") MultipartFile image) {
        try {
            Product newProduct = productService.create(name, description, price, available, type, categories, image);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ProductDTO(newProduct));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar a imagem.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao cadastrar produto.");
        }
    }

    /**
     * Atualiza um produto existente com as informações fornecidas.
     *
     * @param id          ID do produto a ser atualizado
     * @param name        novo nome do produto (opcional)
     * @param description nova descrição do produto (opcional)
     * @param price       novo preço do produto (opcional)
     * @param available   nova disponibilidade do produto (opcional)
     * @param type        novo tipo do produto (opcional)
     * @param categories  novas categorias do produto (opcional)
     * @param image       nova imagem do produto (opcional)
     * @return produto atualizado ou mensagem de erro
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
            @PathVariable Integer id,
            @RequestParam(value = "nome", required = false) String name,
            @RequestParam(value = "descricao", required = false) String description,
            @RequestParam(value = "preco", required = false) Double price,
            @RequestParam(value = "disponivel", required = false) Boolean available,
            @RequestParam(value = "tipo", required = false) String type,
            @RequestParam(value = "categorias", required = false) List<String> categories,
            @RequestParam(value = "imagem", required = false) MultipartFile image) {

        try {
            Product updatedProduct = productService.update(id, name, description, price, available, type, categories,
                    image);
            return ResponseEntity.ok(new ProductDTO(updatedProduct));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ExistingProductException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar a imagem.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao atualizar produto.");
        }
    }

    /**
     * Lista todos os produtos.
     *
     * @return lista de produtos
     */
    @GetMapping
    public ResponseEntity<List<Product>> findAll() {
        List<Product> products = productService.findAll();
        return ResponseEntity.ok(products);
    }

    /**
     * Busca um produto pelo ID.
     *
     * @param id ID do produto
     * @return produto encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable Integer id) {
        Product product = productService.findById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * Deleta um produto pelo ID.
     *
     * @param id ID do produto a ser deletado
     * @return status HTTP 204 (No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deleta um produto pelo nome.
     *
     * @param name nome do produto a ser deletado
     * @return status HTTP 204 (No Content)
     */
    @DeleteMapping("/name/{name}")
    public ResponseEntity<Void> deleteByName(@PathVariable String name) {
        productService.deleteByName(name);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retorna a imagem de um produto pelo ID.
     *
     * @param id ID do produto
     * @return imagem do produto em formato de bytes
     */
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Integer id) {
        Product product = productService.findById(id);
        byte[] image = product.getPhoto();

        if (image == null || image.length == 0) {
            return ResponseEntity.notFound().build();
        }

        String contentType = detectMimeType(image);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(image);
    }

    /**
     * Detecta o tipo MIME de uma imagem baseado nos seus bytes.
     *
     * @param image bytes da imagem
     * @return tipo MIME da imagem
     */
    private String detectMimeType(byte[] image) {
        if (image.length >= 12) {
            if (image[0] == (byte) 0xFF && image[1] == (byte) 0xD8) {
                return MimeTypeUtils.IMAGE_JPEG_VALUE;
            }
            if (image[0] == (byte) 0x89 && image[1] == (byte) 0x50 &&
                    image[2] == (byte) 0x4E && image[3] == (byte) 0x47) {
                return MimeTypeUtils.IMAGE_PNG_VALUE;
            }
            if (image[8] == 'W' && image[9] == 'E' && image[10] == 'B' && image[11] == 'P') {
                return "image/webp";
            }
        }
        return MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE;
    }
}
