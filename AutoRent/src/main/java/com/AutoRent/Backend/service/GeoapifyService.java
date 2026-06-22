package com.AutoRent.Backend.service;

import com.AutoRent.Backend.dto.lugar.LugarSugerenciaDto;
import com.AutoRent.Backend.exception.ParametroIncorrectoException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GeoapifyService {

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${geoapify.api-key:}")
    private String apiKey;

    @Value("${geoapify.autocomplete-url:https://api.geoapify.com/v1/geocode/autocomplete}")
    private String autocompleteUrl;

    public List<LugarSugerenciaDto> autocompletar(String texto) {
        if (texto == null || texto.trim().length() < 2) {
            return List.of();
        }

        String textoBuscado = texto.trim();
        String clave = apiKey == null ? "" : apiKey.trim();
        if (clave.isBlank()) {
            throw new ParametroIncorrectoException("Falta configurar GEOAPIFY_API_KEY");
        }

        URI uri;
        try {
            uri = UriComponentsBuilder.fromUriString(autocompleteUrl)
                    .queryParam("text", textoBuscado)
                    .queryParam("type", "city")
                    .queryParam("filter", "countrycode:ar")
                    .queryParam("format", "json")
                    .queryParam("limit", 6)
                    .queryParam("lang", "es")
                    .queryParam("apiKey", clave)
                    .build()
                    .encode()
                    .toUri();
        } catch (RuntimeException e) {
            throw new ParametroIncorrectoException("La configuracion de Geoapify no es valida");
        }

        try {
            String respuestaTexto = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(String.class);
            JsonNode respuesta = objectMapper.readTree(respuestaTexto);
            return convertirRespuesta(respuesta);
        } catch (RestClientException e) {
            throw new ParametroIncorrectoException("No se pudo consultar Geoapify");
        } catch (JsonProcessingException e) {
            throw new ParametroIncorrectoException("Geoapify no devolvio una respuesta JSON valida");
        } catch (RuntimeException e) {
            throw new ParametroIncorrectoException("No se pudo procesar la respuesta de Geoapify");
        }
    }

    private List<LugarSugerenciaDto> convertirRespuesta(JsonNode respuesta) {
        if (respuesta == null) {
            return List.of();
        }

        JsonNode resultados = respuesta.path("results");
        if (resultados.isArray()) {
            return convertirResultadosJson(resultados);
        }

        JsonNode features = respuesta.path("features");
        if (features.isArray()) {
            return convertirFeaturesGeoJson(features);
        }

        return List.of();
    }

    private List<LugarSugerenciaDto> convertirResultadosJson(JsonNode resultados) {
        List<LugarSugerenciaDto> lugares = new ArrayList<>();

        for (JsonNode item : resultados) {
            lugares.add(new LugarSugerenciaDto(
                    obtenerTexto(item, "city", "name"),
                    obtenerTexto(item, "state", "county"),
                    obtenerTexto(item, "country"),
                    obtenerTexto(item, "formatted")
            ));
        }

        return lugares;
    }

    private List<LugarSugerenciaDto> convertirFeaturesGeoJson(JsonNode features) {
        List<LugarSugerenciaDto> lugares = new ArrayList<>();

        for (JsonNode feature : features) {
            JsonNode props = feature.path("properties");
            lugares.add(new LugarSugerenciaDto(
                    obtenerTexto(props, "city", "name"),
                    obtenerTexto(props, "state", "county"),
                    obtenerTexto(props, "country"),
                    obtenerTexto(props, "formatted")
            ));
        }

        return lugares;
    }

    private String obtenerTexto(JsonNode node, String... campos) {
        for (String campo : campos) {
            String valor = node.path(campo).asText(null);
            if (valor != null && !valor.isBlank()) {
                return valor;
            }
        }

        return "";
    }
}
