package br.com.livrementehomeopatia.backend.validation;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONArray;
import org.json.JSONObject;

@Service
public class CrmNationalValidator {

    private static final String API_KEY = "8941438480";

    public boolean isCrmValid(String crm) {
        // Aceita apenas 4 a 6 dígitos numéricos
        if (crm == null || !crm.matches("\\d{4,6}")) return false;
        String numero = crm;
        String uf = ""; 

        String url = String.format(
            "https://www.consultacrm.com.br/api/index.php?tipo=crm&uf=%s&q=%s&chave=%s&destino=json",
            uf, numero, API_KEY
        );

        RestTemplate restTemplate = new RestTemplate();
        try {
            String response = restTemplate.getForObject(url, String.class);
            if (response == null) return false;

            JSONObject json = new JSONObject(response);
            JSONArray items = json.optJSONArray("item");
            // Se houver pelo menos um item, o CRM existe
            return items != null && items.length() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}