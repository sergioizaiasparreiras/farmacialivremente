package br.com.livrementehomeopatia.backend.enums;

/**
 * Enum que representa os diferentes papéis (perfis) de usuário no sistema.
 * Cada papel possui um código numérico e uma descrição que pode ser utilizada para controle de acesso.
 */
public enum Role {

    /**
     * Papel para usuários com perfil de médico.
     */
    MEDICO(0, "ROLE_MEDICO"),

    /**
     * Papel para usuários com privilégios administrativos.
     */
    ADMIN(1, "ROLE_ADMIN"),

    /**
     * Papel para usuários com perfil de cliente.
     */
    CLIENTE(2, "ROLE_CLIENTE");

    private Integer cod;
    private String descricao;

    /**
     * Construtor interno da enumeração.
     *
     * @param cod código numérico do papel
     * @param descricao descrição textual do papel
     */
    private Role(Integer cod, String descricao) {
        this.cod = cod;
        this.descricao = descricao;
    }

    /**
     * Retorna o código numérico do papel.
     *
     * @return código do papel
     */
    public Integer getCod() {
        return cod;
    }

    /**
     * Retorna a descrição textual do papel (ex: "ROLE_MEDICO").
     *
     * @return descrição do papel
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Converte um código numérico em um valor da enum {@link Role}.
     *
     * @param cod código a ser convertido
     * @return valor correspondente da enum {@link Role}
     * @throws IllegalArgumentException se o código não for válido
     */
    public static Role toEnum(Integer cod) {
        if (cod == null) {
            return null;
        }
        for (Role x : Role.values()) {
            if (cod.equals(x.getCod())) {
                return x;
            }
        }
        throw new IllegalArgumentException("Id inválido: " + cod);
    }
}
