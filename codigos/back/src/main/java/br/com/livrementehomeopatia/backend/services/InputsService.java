package br.com.livrementehomeopatia.backend.services;

import org.apache.poi.ss.usermodel.*;

import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.livrementehomeopatia.backend.model.Inputs;
import br.com.livrementehomeopatia.backend.repository.InputsRepository;

@Service
public class InputsService {

    @Autowired
    private InputsRepository inputsRepository;

    /**
     * Importa uma planilha Excel e adiciona os insumos 
     * que não estão cadastrados no banco de dados.
     * 
     * @param excelStream InputStream da planilha Excel
     * @throws RuntimeException Caso ocorra um erro ao importar a planilha
     */
    public void importarPlanilha(InputStream excelStream) {
        try {
            System.out.println("Iniciando importação da planilha...");
            
            Workbook workbook = WorkbookFactory.create(excelStream);
            System.out.println("Workbook criado com sucesso");
            
            Sheet sheet = workbook.getSheetAt(0);
            System.out.println("Planilha carregada - Total de linhas: " + sheet.getLastRowNum());
            
            List<Inputs> inputsList = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Cell nameCell = row.getCell(2); // Coluna "Descrição"
                    if (nameCell != null) {
                        String name;
                        
                        // Verificar o tipo da célula para evitar erros
                        switch (nameCell.getCellType()) {
                            case STRING:
                                name = nameCell.getStringCellValue().trim().replaceAll("\\s+", " ");
                                break;
                            case NUMERIC:
                                name = String.valueOf(nameCell.getNumericCellValue()).trim();
                                break;
                            default:
                                System.out.println("Tipo de célula não suportado na linha " + (i + 1) + ": " + nameCell.getCellType());
                                continue;
                        }

                        if (!name.isEmpty() && !inputsRepository.existsByNameIgnoreCase(name)) {
                            Inputs input = new Inputs(null, name);
                            inputsList.add(input);
                            System.out.println("Adicionado insumo: " + name);
                        } else if (name.isEmpty()) {
                            System.out.println("Nome vazio na linha " + (i + 1));
                        } else {
                            System.out.println("Insumo já existe: " + name);
                        }
                    } else {
                        System.out.println("Célula vazia na linha " + (i + 1) + ", coluna 2");
                    }
                } else {
                    System.out.println("Linha vazia: " + (i + 1));
                }
            }

            System.out.println("Total de novos insumos a serem salvos: " + inputsList.size());
            inputsRepository.saveAll(inputsList);
            System.out.println("Importação concluída com sucesso!");
            
            workbook.close();
            
        } catch (Exception e) {
            System.err.println("Erro detalhado ao importar planilha:");
            e.printStackTrace();
            throw new RuntimeException("Erro ao importar planilha: " + e.getMessage() + " (" + e.getClass().getSimpleName() + ")", e);
        }
    }
    /**
     * Retorna todos os insumos cadastrados.
     * 
     * @return lista de insumos
     */
    public List<Inputs> findAll() {
        return inputsRepository.findAll();
    }

    /**
     * Retorna um insumo pelo ID.
     * 
     * @param id O ID do insumo a ser buscado.
     * @return O insumo encontrado.
     * @throws NoSuchElementException Caso o insumo n o seja encontrado.
     */
    
    public Inputs findById(Integer id) {
        return inputsRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Input não encontrado com ID: " + id));
    }

    /**
     * Atualiza o status de disponibilidade de um insumo.
     * 
     * @param id        O ID do insumo a ser atualizado.
     * @param available O novo status de disponibilidade do insumo.
     * @return O insumo atualizado.
     * @throws NoSuchElementException Caso o insumo n o seja encontrado.
     */
    public Inputs updateAvailable(Integer id, boolean available) {
        Inputs input = findById(id);
        input.setAvailable(available);
        return inputsRepository.save(input);
    }

    /**
     * Deleta um insumo pelo ID.
     * 
     * @param id O ID do insumo a ser deletado.
     * @throws NoSuchElementException Caso o insumo n o seja encontrado.
     */
    public void delete(Integer id) {
        if (!inputsRepository.existsById(id)) {
            throw new NoSuchElementException("Input não encontrado com ID: " + id);
        }
        inputsRepository.deleteById(id);
    }
}
