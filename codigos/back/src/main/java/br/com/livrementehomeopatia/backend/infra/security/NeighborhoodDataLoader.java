package br.com.livrementehomeopatia.backend.infra.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import br.com.livrementehomeopatia.backend.model.Neighborhood;
import br.com.livrementehomeopatia.backend.repository.NeighborhoodRepository;
import br.com.livrementehomeopatia.backend.services.NeighborhoodService;
import java.util.Arrays;
import java.util.List;

/**
 * Componente responsável por carregar uma lista padrão de bairros na base de dados
 * durante a inicialização da aplicação.
 *
 * Implementa {@link CommandLineRunner} para garantir que a sua lógica seja executada
 * uma única vez após o contexto da aplicação Spring ter sido completamente carregado.
 * A lógica é idempotente, ou seja, só popula os dados se a tabela de bairros
 * estiver vazia, evitando duplicatas e lentidão em reinicializações futuras.
 */
@Component
public class NeighborhoodDataLoader implements CommandLineRunner {

    /**
     * Serviço de negócio para encapsular a lógica de salvar bairros.
     */
    private final NeighborhoodService neighborhoodService;

    /**
     * Repositório de dados para interagir diretamente com a tabela de bairros,
     * usado aqui especificamente para verificar se a tabela já contém dados.
     */
    private final NeighborhoodRepository neighborhoodRepository;

    /**
     * Construtor para injeção de dependências do serviço e do repositório de bairros.
     *
     * @param neighborhoodService O serviço para manipulação de bairros.
     * @param neighborhoodRepository O repositório para acesso direto aos dados de bairros.
     */
    public NeighborhoodDataLoader(NeighborhoodService neighborhoodService, NeighborhoodRepository neighborhoodRepository) {
        this.neighborhoodService = neighborhoodService;
        this.neighborhoodRepository = neighborhoodRepository;
    }

    /**
     * Ponto de entrada executado pelo Spring Boot na inicialização.
     *
     * Este método primeiro verifica se já existem bairros no banco de dados.
     * Se o banco estiver vazio, ele prossegue para popular a tabela com uma
     * lista predefinida de bairros e suas respectivas taxas de entrega.
     *
     * @param args Argumentos de linha de comando (não utilizados neste runner).
     * @throws Exception se ocorrer um erro durante a execução.
     */
    @Override
    public void run(String... args) throws Exception {
        
        // ### CORREÇÃO DEFINITIVA ADICIONADA AQUI ###
        // Primeiro, verifica se a tabela de bairros já tem algum registro.
        if (neighborhoodRepository.count() > 0) {
            // Se a contagem for maior que zero, significa que os dados já foram carregados.
            // A função então para imediatamente, garantindo uma inicialização rápida.
            System.out.println("Bairros já cadastrados no banco de dados. Carga inicial ignorada.");
            return;
        }

        // Este bloco de código só será executado se o 'if' acima for falso,
        // ou seja, na primeira vez que a aplicação subir com um banco de dados limpo.
        System.out.println("Banco de dados de bairros vazio. Iniciando carga inicial...");
        
        List<Neighborhood> defaultNeighborhoods = Arrays.asList(
             new Neighborhood(null, "Adelaide", 20.00),
            new Neighborhood(null, "Aeroporto (Pampulha)", 19.00),
            new Neighborhood(null, "Agua Branca", 20.00),
            new Neighborhood(null, "Alphaville", 58.00),
            new Neighborhood(null, "Alípio de Melo", 18.00),
            new Neighborhood(null, "Alto Caiçara", 16.00),
            new Neighborhood(null, "Alto Santa Lúcia", 15.00),
            new Neighborhood(null, "Alto Vera Cruz", 15.00),
            new Neighborhood(null, "Altos dos Pinheiros", 19.00),
            new Neighborhood(null, "Álvaro Camargo", 31.00),
            new Neighborhood(null, "Alvorada (Contagem)", 25.00),
            new Neighborhood(null, "Alvorada BH", 16.00),
            new Neighborhood(null, "Amazonas (Perto Praça Cemig)", 22.00),
            new Neighborhood(null, "Ana Lúcia", 17.00),
            new Neighborhood(null, "Anchieta", 11.00),
            new Neighborhood(null, "Aparecida", 17.00),
            new Neighborhood(null, "Bairro da Graça", 15.00),
            new Neighborhood(null, "Bairro da Urca", 22.00),
            new Neighborhood(null, "Bairro das Indústrias", 24.00),
            new Neighborhood(null, "Bandeirantes Contagem", 26.00),
            new Neighborhood(null, "Barreiro de Baixo", 25.00),
            new Neighborhood(null, "Barreiro de Cima", 25.00),
            new Neighborhood(null, "Barro Preto", 13.00),
            new Neighborhood(null, "Barroca", 13.00),
            new Neighborhood(null, "Belvedere", 17.00),
            new Neighborhood(null, "Betânia", 18.00),
            new Neighborhood(null, "Betim (Centro)", 60.00),
            new Neighborhood(null, "Boa Vista", 16.00),
            new Neighborhood(null, "Bomfim", 14.00),
            new Neighborhood(null, "Buritis", 18.00),
            new Neighborhood(null, "Cabana", 18.00),
            new Neighborhood(null, "Cachoeirinha", 16.00),
            new Neighborhood(null, "Calçara", 16.00),
            new Neighborhood(null, "Calafate", 14.00),
            new Neighborhood(null, "Califórnia", 22.00),
            new Neighborhood(null, "Camargo", 23.00),
            new Neighborhood(null, "Campo Alegre", 23.00),
            new Neighborhood(null, "Canaã", 28.00),
            new Neighborhood(null, "Capitão Eduardo", 35.00),
            new Neighborhood(null, "Candelária (Venda Nova)", 27.00),
            new Neighborhood(null, "Carlos Prates", 14.00),
            new Neighborhood(null, "Carmo Sion", 11.00),
            new Neighborhood(null, "Carrefour (Contagem)", 28.00),
            new Neighborhood(null, "Castelo", 20.00),
            new Neighborhood(null, "Ceasa", 26.00),
            new Neighborhood(null, "Cenáculo (Venda Nova)", 25.00),
            new Neighborhood(null, "Centro BH", 14.00),
            new Neighborhood(null, "Céu Azul", 28.00),
            new Neighborhood(null, "Cidade Industrial", 23.00),
            new Neighborhood(null, "Cidade Jardim", 12.00),
            new Neighborhood(null, "Cidade Nova", 17.00),
            new Neighborhood(null, "Colégio Batista", 13.00),
            new Neighborhood(null, "Concórdia", 14.00),
            new Neighborhood(null, "Condomínio Bosque do Jambreiro", 35.00),
            new Neighborhood(null, "Cond. Nova Lima", 30.00),
            new Neighborhood(null, "Confins", 60.00),
            new Neighborhood(null, "Conj. Agua Branca", 23.00),
            new Neighborhood(null, "Conj. Cristina", 29.00),
            new Neighborhood(null, "Conj. Teixeira Dias", 24.00),
            new Neighborhood(null, "Contagem (Centro)", 29.00),
            new Neighborhood(null, "Copacabana", 24.00),
            new Neighborhood(null, "Coqueiros", 25.00),
            new Neighborhood(null, "Coração de Jesus", 13.00),
            new Neighborhood(null, "Coração Eucarístico", 16.00),
            new Neighborhood(null, "Cruzeiro", 11.00),
            new Neighborhood(null, "Dom Bosco", 17.00),
            new Neighborhood(null, "Dom Cabral", 17.00),
            new Neighborhood(null, "Dona Clara", 20.00),
            new Neighborhood(null, "Durval de Barros", 30.00),
            new Neighborhood(null, "Eldorado", 25.00),
            new Neighborhood(null, "Engenho Nogueira", 23.00),
            new Neighborhood(null, "Enseada das Garças", 25.00),
            new Neighborhood(null, "Ermelinda", 17.00),
            new Neighborhood(null, "Esplanada", 15.00),
            new Neighborhood(null, "Estoril", 17.00),
            new Neighborhood(null, "Estrela Dalva", 17.00),
            new Neighborhood(null, "Eymard", 21.00),
            new Neighborhood(null, "Fernão Dias", 17.00),
            new Neighborhood(null, "Flávio Marques Lisboa", 24.00),
            new Neighborhood(null, "Floramar", 23.00),
            new Neighborhood(null, "Floresta", 12.00),
            new Neighborhood(null, "Fortaleza", 43.00),
            new Neighborhood(null, "Frei Eustáquio", 18.00),
            new Neighborhood(null, "Frei Leopoldo", 27.00),
            new Neighborhood(null, "Funcionários", 11.00),
            new Neighborhood(null, "Gameleira", 17.00),
            new Neighborhood(null, "General Carneiro", 30.00),
            new Neighborhood(null, "Glória", 20.00),
            new Neighborhood(null, "Goiânia", 21.00),
            new Neighborhood(null, "Grajaú", 15.00),
            new Neighborhood(null, "Guarani", 22.00),
            new Neighborhood(null, "Gutierrez", 14.00),
            new Neighborhood(null, "Havaí", 17.00),
            new Neighborhood(null, "Heliópolis", 22.00),
            new Neighborhood(null, "Hotel Ouro Minas", 20.00),
            new Neighborhood(null, "Horto", 15.00),
            new Neighborhood(null, "Humaitá", 23.00),
            new Neighborhood(null, "Independência", 27.00),
            new Neighborhood(null, "Instituto Agronômico", 15.00),
            new Neighborhood(null, "Ipanema", 18.00),
            new Neighborhood(null, "Ipiranga", 17.00),
            new Neighborhood(null, "Itaipu", 28.00),
            new Neighborhood(null, "Itapoã", 25.00),
            new Neighborhood(null, "Nazaré", 26.00),
            new Neighborhood(null, "Nossa Sraza Glória (Contagem)", 28.00),
            new Neighborhood(null, "Novo São Lucas", 10.00),
            new Neighborhood(null, "Nova Cachoeirinha", 17.00),
            new Neighborhood(null, "Nova Cintra", 17.00),
            new Neighborhood(null, "Nova Floresta", 16.00),
            new Neighborhood(null, "Nova Gameleira", 17.00),
            new Neighborhood(null, "Nova Glória", 21.00),
            new Neighborhood(null, "Nova Granada", 15.00),
            new Neighborhood(null, "Nova Lima", 38.00),
            new Neighborhood(null, "Nova Pampulha", 30.00),
            new Neighborhood(null, "Nova Suíça", 16.00),
            new Neighborhood(null, "Nova Vista", 16.00),
            new Neighborhood(null, "Novo Aarão Reis", 20.00),
            new Neighborhood(null, "Novo Cloris", 26.00),
            new Neighborhood(null, "Novo Planalto", 21.00),
            new Neighborhood(null, "Novo Progresso", 22.00),
            new Neighborhood(null, "Novo Riacho", 26.00),
            new Neighborhood(null, "Olaria", 25.00),
            new Neighborhood(null, "Ouro Minas", 24.00),
            new Neighborhood(null, "Ouro Velho (Mansões)", 35.00),
            new Neighborhood(null, "Olhos D'Água", 19.00),
            new Neighborhood(null, "Ouro Preto", 20.00),
            new Neighborhood(null, "Padre Eustáquio", 17.00),
            new Neighborhood(null, "Palmares", 17.00),
            new Neighborhood(null, "Palmeiras", 17.00),
            new Neighborhood(null, "Palmital (Santa Luzia)", 32.00),
            new Neighborhood(null, "Paquetá", 22.00),
            new Neighborhood(null, "Paraíso", 13.00),
            new Neighborhood(null, "Parque do Engenho", 40.00),
            new Neighborhood(null, "Parque São Pedro", 35.00),
            new Neighborhood(null, "Parque Turista", 35.00),
            new Neighborhood(null, "Patrocínio", 22.00),
            new Neighborhood(null, "Paulo II", 26.00),
            new Neighborhood(null, "Petrolândia (Betim)", 37.00),
            new Neighborhood(null, "Pilar", 20.00),
            new Neighborhood(null, "Pindorama", 22.00),
            new Neighborhood(null, "Pirajá", 19.00),
            new Neighborhood(null, "Piratininga", 26.00),
            new Neighborhood(null, "Planalto", 27.00),
            new Neighborhood(null, "Pompéia", 14.00),
            new Neighborhood(null, "Prado", 14.00),
            new Neighborhood(null, "Primeiro de Maio", 19.00),
            new Neighborhood(null, "Progresso", 21.00),
            new Neighborhood(null, "Providência", 19.00),
            new Neighborhood(null, "Regina", 30.00),
            new Neighborhood(null, "Renascença", 16.00),
            new Neighborhood(null, "Ressaca (Contagem)", 30.00),
            new Neighborhood(null, "Retiro das Pedras", 39.00),
            new Neighborhood(null, "Ribeiro de Abreu", 21.00),
            new Neighborhood(null, "Rio Branco", 26.00),
            new Neighborhood(null, "Itatiaia (Perto do Zoológico)", 25.00),
            new Neighborhood(null, "Jaqueline", 27.00),
            new Neighborhood(null, "Jaraguá", 23.00),
            new Neighborhood(null, "Jardim Alvorada (BH)", 21.00),
            new Neighborhood(null, "Jardim Alvorada (Contagem)", 24.00),
            new Neighborhood(null, "Jardim América", 17.00),
            new Neighborhood(null, "Jardim Atlântico", 25.00),
            new Neighborhood(null, "Jardim Belmonte", 27.00),
            new Neighborhood(null, "Jardim Canadá", 31.00),
            new Neighborhood(null, "Jardim Comerciários", 26.00),
            new Neighborhood(null, "Jardim Europa", 26.00),
            new Neighborhood(null, "Jardim Filadélfia", 27.00),
            new Neighborhood(null, "Jardim Industrial", 26.00),
            new Neighborhood(null, "Jardim Laguna (Contagem)", 27.00),
            new Neighborhood(null, "Jardim Leblon", 26.00),
            new Neighborhood(null, "Jardim Montanhes (BH)", 17.00),
            new Neighborhood(null, "Jardim Vitória", 27.00),
            new Neighborhood(null, "Jardinópolis", 20.00),
            new Neighborhood(null, "Jatobá", 27.00),
            new Neighborhood(null, "João Pinheiro", 18.00),
            new Neighborhood(null, "Juliana", 27.00),
            new Neighborhood(null, "Justinópolis (Ribeirão das Neves)", 38.00),
            new Neighborhood(null, "Kennedy", 28.00),
            new Neighborhood(null, "Lagoa Ceu Azul", 27.00),
            new Neighborhood(null, "Lagoa dos Ingleses", 69.00),
            new Neighborhood(null, "Lagoa Santa", 60.00),
            new Neighborhood(null, "Lagoinha (Centro)", 15.00),
            new Neighborhood(null, "Lagoinha (Venda Nova)", 32.00),
            new Neighborhood(null, "Letícia", 27.00),
            new Neighborhood(null, "Liberdade (Jaraguá)", 21.00),
            new Neighborhood(null, "Lindeia", 34.00),
            new Neighborhood(null, "Lourdes", 12.00),
            new Neighborhood(null, "Luxemburgo", 14.00),
            new Neighborhood(null, "Maldonado", 25.00),
            new Neighborhood(null, "Mangabeiras", 12.00),
            new Neighborhood(null, "Mangueiras", 27.00),
            new Neighborhood(null, "Mantiqueira", 27.00),
            new Neighborhood(null, "Maria Goreth", 22.00),
            new Neighborhood(null, "Maria Helena", 27.00),
            new Neighborhood(null, "Mariilândia", 27.00),
            new Neighborhood(null, "Milionários", 25.00),
            new Neighborhood(null, "Minas Brasil (Padre Eustáquio)", 18.00),
            new Neighborhood(null, "Minas Caixa", 25.00),
            new Neighborhood(null, "Minaslândia (PTB Betim)", 50.00),
            new Neighborhood(null, "Mineirão", 27.00),
            new Neighborhood(null, "Monsenhor Messias", 16.00),
            new Neighborhood(null, "Morro do Chapéu", 50.00),
            new Neighborhood(null, "Nacional (Contagem)", 27.00),
            new Neighborhood(null, "Sabará", 42.00),
            new Neighborhood(null, "Sagrada Família", 17.00),
            new Neighborhood(null, "Salgado Filho", 16.00),
            new Neighborhood(null, "Santa Amélia", 23.00),
            new Neighborhood(null, "Santa Branca", 28.00),
            new Neighborhood(null, "Santa Cruz", 18.00),
            new Neighborhood(null, "Santa Efigênia", 11.00),
            new Neighborhood(null, "Santa Fé (Neves)", 55.00),
            new Neighborhood(null, "Santa Inês", 16.00),
            new Neighborhood(null, "Santa Lúcia", 15.00),
            new Neighborhood(null, "Santa Luzia (Centro)", 48.00),
            new Neighborhood(null, "Santa Maria (Itambé)", 25.00),
            new Neighborhood(null, "Santa Maria (Luxemburgo)", 15.00),
            new Neighborhood(null, "Santa Mônica", 23.00),
            new Neighborhood(null, "Santa Rosa", 23.00),
            new Neighborhood(null, "Santa Tereza", 12.00),
            new Neighborhood(null, "Santa Terezinha", 22.00),
            new Neighborhood(null, "Santo Agostinho", 13.00),
            new Neighborhood(null, "Santo Andre", 17.00),
            new Neighborhood(null, "Santo Antônio", 11.00),
            new Neighborhood(null, "São Benedito", 28.00),
            new Neighborhood(null, "São Bento", 16.00),
            new Neighborhood(null, "São Bernardo", 23.00),
            new Neighborhood(null, "São Cristóvão", 16.00),
            new Neighborhood(null, "São Francisco", 20.00),
            new Neighborhood(null, "São Gabriel", 23.00),
            new Neighborhood(null, "São Geraldo", 16.00),
            new Neighborhood(null, "São João Batista", 25.00),
            new Neighborhood(null, "São José (Pampulha)", 20.00),
            new Neighborhood(null, "São José Lapa", 50.00),
            new Neighborhood(null, "São Lucas", 10.00),
            new Neighborhood(null, "São Luiz (Pampulha)", 20.00),
            new Neighborhood(null, "São Marcos", 20.00),
            new Neighborhood(null, "São Paulo", 20.00),
            new Neighborhood(null, "São Pedro", 12.00),
            new Neighborhood(null, "São Tomas", 22.00),
            new Neighborhood(null, "Saudade", 12.00),
            new Neighborhood(null, "Savassi", 11.00),
            new Neighborhood(null, "Serra", 10.00),
            new Neighborhood(null, "Serrano", 23.00),
            new Neighborhood(null, "Serra Verde", 26.00),
            new Neighborhood(null, "Silveira", 18.00),
            new Neighborhood(null, "Sion", 12.00),
            new Neighborhood(null, "Solar", 30.00),
            new Neighborhood(null, "Taquaril", 17.00),
            new Neighborhood(null, "Teixeira Dias", 26.00),
            new Neighborhood(null, "Tirol", 27.00),
            new Neighborhood(null, "Trevo", 27.00),
            new Neighborhood(null, "Tony", 25.00),
            new Neighborhood(null, "Tupi", 21.00),
            new Neighborhood(null, "Túnel Ibirité", 40.00),
            new Neighborhood(null, "Urca", 23.00),
            new Neighborhood(null, "Uruçuia", 33.00),
            new Neighborhood(null, "Vale Jatobá", 28.00),
            new Neighborhood(null, "Vale do Sereno", 22.00),
            new Neighborhood(null, "Venda Nova", 25.00),
            new Neighborhood(null, "Vera Cruz", 16.00),
            new Neighborhood(null, "Vila Campestre", 30.00),
            new Neighborhood(null, "Vila Castela", 23.00),
            new Neighborhood(null, "Vila da Serra", 19.00),
            new Neighborhood(null, "Vila Del Rey", 21.00),
            new Neighborhood(null, "Vila Ipê", 24.00),
            new Neighborhood(null, "Vila Oeste", 18.00),
            new Neighborhood(null, "Vila Paris (BH)", 13.00),
            new Neighborhood(null, "Vila Paris (Contagem)", 30.00),
            new Neighborhood(null, "Vila Pinho", 32.00),
            new Neighborhood(null, "Vila do Minério", 31.00),
            new Neighborhood(null, "Village Terrasse", 23.00),
            new Neighborhood(null, "Vista Alegre", 18.00),
            new Neighborhood(null, "Vista Sol", 32.00),
            new Neighborhood(null, "Xangrilá", 30.00),
            new Neighborhood(null, "Zoológico", 27.00)
        );

        for (Neighborhood neighborhood : defaultNeighborhoods) {
            try {
                neighborhoodService.save(neighborhood.getName(), neighborhood.getTax());
            } catch (IllegalArgumentException e) {
                System.out.println("Bairro já cadastrado (verificação de segurança): " + neighborhood.getName());
            }
        }
        System.out.println("Carga inicial de bairros concluída com sucesso.");
    }
}