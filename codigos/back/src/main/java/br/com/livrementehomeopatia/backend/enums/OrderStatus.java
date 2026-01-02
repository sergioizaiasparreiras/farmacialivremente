package br.com.livrementehomeopatia.backend.enums;

/**
 * Enum que representa os status de um pedido, incluindo os específicos do Mercado Pago.
 */
public enum OrderStatus {

    // --- Status Genéricos ---
    EM_ANDAMENTO("Em Andamento"),               // Status inicial do pedido
    AGUARDANDO_PAGAMENTO("Aguardando Pagamento"),
    PAGO("Pago"),                               // Pagamento aprovado
    ENTREGUE("Entregue"),                       // Pedido entregue
    CANCELADO("Cancelado"),                     // Pedido cancelado manualmente
    
    // --- Status para Pedidos de Revenda ---
    EM_SEPARACAO("Em Separação"),
    PRONTO_PARA_ENVIO("Pronto para Envio"),
    A_CAMINHO("A Caminho"),
    FALHA_NA_ENTREGA("Entrega não realizada"),
    
    // --- Status para o Fluxo de Orçamento de Manipulados ---
    ORCAMENTO_PENDENTE("Orçamento Pendente"),
    EM_MANIPULACAO("Em Manipulação"),
    
    // --- Status específicos do Mercado Pago ---
    PAGAMENTO_EM_ANALISE("Pagamento em Análise"),
    PAGAMENTO_ESTORNADO("Pagamento Estornado"),
    PAGAMENTO_EXPIRADO("Pagamento Expirado"),
    PAGAMENTO_RECUSADO("Pagamento Recusado");

    private final String descricao;

    OrderStatus(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    /**
     * Converte o status do Mercado Pago para o status interno do sistema.
     * @param mpStatus Status retornado pelo Mercado Pago
     * @return Status interno correspondente
     */
    public static OrderStatus fromMercadoPagoStatus(String mpStatus) {
        if (mpStatus == null) return EM_ANDAMENTO;
        
        return switch (mpStatus.toLowerCase()) {
            case "pending", "authorized" -> AGUARDANDO_PAGAMENTO;
            case "approved" -> PAGO;
            case "in_process" -> PAGAMENTO_EM_ANALISE;
            case "cancelled" -> CANCELADO;
            case "refunded", "charged_back" -> PAGAMENTO_ESTORNADO;
            case "expired" -> PAGAMENTO_EXPIRADO;
            case "rejected" -> PAGAMENTO_RECUSADO;
            default -> EM_ANDAMENTO;
        };
    }
}