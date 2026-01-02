package br.com.livrementehomeopatia.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import br.com.livrementehomeopatia.backend.model.Inputs;

import br.com.livrementehomeopatia.backend.services.InputsService;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/inputs")
public class InputsController {

    @Autowired
    private InputsService inputsService;

/**
 * Endpoint para importar uma planilha de entradas.
 *
 * @param file Arquivo Excel a ser importado.
 * @return ResponseEntity com mensagem de sucesso ou erro.
 * @throws IOException Se ocorrer um erro ao ler o arquivo.
 * @throws RuntimeException Se ocorrer um erro ao processar a importação.
 */

 @PostMapping("/importar")
 public ResponseEntity<String> importarPlanilha(@RequestParam("file") MultipartFile file) {
     try {
         // Validações do arquivo
         if (file.isEmpty()) {
             return ResponseEntity
                     .status(HttpStatus.BAD_REQUEST)
                     .body("Arquivo vazio ou não foi enviado");
         }
         
         String fileName = file.getOriginalFilename();
         if (fileName == null) {
             return ResponseEntity
                     .status(HttpStatus.BAD_REQUEST)
                     .body("Nome do arquivo inválido");
         }
         
         // Verificar extensão do arquivo
         String fileExtension = fileName.toLowerCase();
         if (!fileExtension.endsWith(".xlsx") && !fileExtension.endsWith(".xls")) {
             return ResponseEntity
                     .status(HttpStatus.BAD_REQUEST)
                     .body("Tipo de arquivo inválido. Apenas arquivos Excel (.xlsx ou .xls) são aceitos. Arquivo enviado: " + fileName);
         }
         
         // Verificar tipo MIME
         String contentType = file.getContentType();
         if (contentType != null && 
             !contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") &&
             !contentType.equals("application/vnd.ms-excel")) {
             System.out.println("Aviso: Tipo MIME inesperado: " + contentType + " para arquivo: " + fileName);
         }
         
         System.out.println("Iniciando processamento do arquivo: " + fileName + " (tamanho: " + file.getSize() + " bytes)");
         
         inputsService.importarPlanilha(file.getInputStream());
         return ResponseEntity.ok("Importação realizada com sucesso!");
     } catch (IOException e) {
         System.err.println("Erro de I/O ao ler arquivo:");
         e.printStackTrace();
         return ResponseEntity
                 .status(HttpStatus.BAD_REQUEST)
                 .body("Erro ao ler o arquivo: " + e.getMessage());
     } catch (RuntimeException e) {
         System.err.println("Erro de runtime ao processar arquivo:");
         e.printStackTrace();
         return ResponseEntity
                 .status(HttpStatus.INTERNAL_SERVER_ERROR)
                 .body("Erro ao importar planilha: " + e.getMessage());
     } catch (Exception e) {
         System.err.println("Erro inesperado:");
         e.printStackTrace();
         return ResponseEntity
                 .status(HttpStatus.INTERNAL_SERVER_ERROR)
                 .body("Erro inesperado: " + e.getMessage());
     }
 }


    /**
     * Busca todos os insumos.
     * 
     * @return Uma resposta HTTP com status 200 (OK) e um corpo que cont m a lista de insumos.
     */
    @GetMapping
    public ResponseEntity<List<Inputs>> findAll() {
        return ResponseEntity.ok(inputsService.findAll());
    }

    /**
     * Busca um insumo pelo ID.
     * 
     * @param id O ID do insumo a ser buscado.
     * @return Uma resposta HTTP com status 200 (OK) se o insumo for encontrado, ou 
     *         uma resposta HTTP com status 404 (Not Found) se o insumo n o for encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Inputs> findById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(inputsService.findById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Atualiza o status de disponibilidade de um insumo.
     * 
     * @param id O ID do insumo a ser atualizado.
     * @param available O novo status de disponibilidade do insumo.
     * @return Uma resposta HTTP com status 200 (OK) se o insumo for atualizado com sucesso, ou 
     *         uma resposta HTTP com status 404 (Not Found) se o insumo n o for encontrado.
     */
    @PutMapping("/{id}/available")
    public ResponseEntity<Inputs> updateAvailable(@PathVariable Integer id, @RequestParam boolean available) {
        try {
            return ResponseEntity.ok(inputsService.updateAvailable(id, available));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Deleta um insumo pelo ID.
     * 
     * @param id O ID do insumo a ser deletado.
     * @return Uma resposta HTTP com status 204 (No Content) se o insumo for deletado com sucesso, ou 
     *         uma resposta HTTP com status 404 (Not Found) se o insumo n o for encontrado.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            inputsService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}