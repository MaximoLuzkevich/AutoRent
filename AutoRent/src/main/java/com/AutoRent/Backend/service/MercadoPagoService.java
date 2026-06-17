package com.AutoRent.Backend.service;

import com.AutoRent.Backend.exception.ParametroIncorrectoException;
import com.AutoRent.Backend.model.Pago;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class MercadoPagoService {

    private final String accessToken;
    private final RestClient restClient;

    public MercadoPagoService(@Value("${mercadopago.access-token:}") String accessToken) {
        this.accessToken = accessToken;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.mercadopago.com")
                .build();
    }

    public String crearPreferencia(Pago pago) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new ParametroIncorrectoException("Falta configurar MERCADOPAGO_ACCESS_TOKEN");
        }

        try {
            Map<String, Object> respuesta = restClient.post()
                    .uri("/checkout/preferences")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(crearRequestPreferencia(pago))
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {
                    });

            return obtenerLinkPago(respuesta);
        } catch (RestClientException e) {
            throw new ParametroIncorrectoException("No se pudo crear la preferencia de Mercado Pago");
        }
    }

    private Map<String, Object> crearRequestPreferencia(Pago pago) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("title", "Reserva AutoRent #" + pago.getReserva().getIdReserva());
        item.put("quantity", 1);
        item.put("currency_id", "ARS");
        item.put("unit_price", pago.getMonto());

        Map<String, Object> request = new LinkedHashMap<>();
        request.put("items", List.of(item));
        request.put("external_reference", String.valueOf(pago.getIdPago()));

        String emailCliente = pago.getReserva().getCliente().getEmail();
        if (emailCliente != null && !emailCliente.isBlank()) {
            request.put("payer", Map.of("email", emailCliente));
        }

        request.put("payment_methods", Map.of(
                "excluded_payment_types", new ArrayList<>(),
                "installments", 1
        ));

        return request;
    }

    private String obtenerLinkPago(Map<String, Object> respuesta) {
        if (respuesta == null) {
            throw new ParametroIncorrectoException("Mercado Pago no devolvio una respuesta valida");
        }

        Object link = accessToken.startsWith("TEST-")
                ? respuesta.get("sandbox_init_point")
                : respuesta.get("init_point");

        if (link == null || link.toString().isBlank()) {
            link = respuesta.get("init_point");
        }

        if (link == null || link.toString().isBlank()) {
            link = respuesta.get("sandbox_init_point");
        }

        if (link == null || link.toString().isBlank()) {
            throw new ParametroIncorrectoException("Mercado Pago no devolvio link de pago");
        }

        return link.toString();
    }
}
