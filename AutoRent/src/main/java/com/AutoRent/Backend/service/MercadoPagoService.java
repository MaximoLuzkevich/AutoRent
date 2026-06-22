package com.AutoRent.Backend.service;

import com.AutoRent.Backend.exception.ParametroIncorrectoException;
import com.AutoRent.Backend.model.Pago;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePayerRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MercadoPagoService {

    private final String accessToken;
    private final String appBaseUrl;

    public MercadoPagoService(
            @Value("${mercadopago.access-token:}") String accessToken,
            @Value("${app.base-url:http://localhost:8080}") String appBaseUrl
    ) {
        this.accessToken = accessToken;
        this.appBaseUrl = appBaseUrl;
    }

    public String crearPreferencia(Pago pago) {
        validarConfiguracion();
        MercadoPagoConfig.setAccessToken(accessToken);

        try {
            PreferenceItemRequest item = PreferenceItemRequest.builder()
                    .title("Reserva AutoRent #" + pago.getReserva().getIdReserva())
                    .description(pago.getReserva().getAuto().getMarca()
                            + " " + pago.getReserva().getAuto().getModelo())
                    .quantity(1)
                    .currencyId("ARS")
                    .unitPrice(pago.getMonto())
                    .build();

            PreferenceRequest request = PreferenceRequest.builder()
                    .items(List.of(item))
                    .payer(PreferencePayerRequest.builder()
                            .email(pago.getReserva().getCliente().getEmail())
                            .build())
                    .externalReference(String.valueOf(pago.getIdPago()))
                    .notificationUrl(url("/api/pagos/mercadopago/webhook"))
                    .backUrls(PreferenceBackUrlsRequest.builder()
                            .success(url("/pago-resultado.html?estado=aprobado"))
                            .pending(url("/pago-resultado.html?estado=pendiente"))
                            .failure(url("/pago-resultado.html?estado=rechazado"))
                            .build())
                    .build();

            Preference preference = new PreferenceClient().create(request);
            return obtenerLinkPago(preference);
        } catch (MPException | MPApiException e) {
            throw new ParametroIncorrectoException("No se pudo crear la preferencia de Mercado Pago");
        }
    }

    public Payment obtenerPago(Long idPagoMercadoPago) {
        validarConfiguracion();
        MercadoPagoConfig.setAccessToken(accessToken);

        try {
            return new PaymentClient().get(idPagoMercadoPago);
        } catch (MPException | MPApiException e) {
            throw new ParametroIncorrectoException("No se pudo consultar el pago en Mercado Pago");
        }
    }

    private String obtenerLinkPago(Preference preference) {
        if (preference == null) {
            throw new ParametroIncorrectoException("Mercado Pago no devolvio una respuesta valida");
        }

        String link = accessToken.startsWith("TEST-")
                ? preference.getSandboxInitPoint()
                : preference.getInitPoint();

        if (link == null || link.isBlank()) {
            link = preference.getInitPoint();
        }

        if (link == null || link.isBlank()) {
            link = preference.getSandboxInitPoint();
        }

        if (link == null || link.isBlank()) {
            throw new ParametroIncorrectoException("Mercado Pago no devolvio link de pago");
        }

        return link;
    }

    private void validarConfiguracion() {
        if (accessToken == null || accessToken.isBlank()) {
            throw new ParametroIncorrectoException("Falta configurar MERCADOPAGO_ACCESS_TOKEN");
        }
    }

    private String url(String path) {
        String base = appBaseUrl.endsWith("/")
                ? appBaseUrl.substring(0, appBaseUrl.length() - 1)
                : appBaseUrl;
        return base + path;
    }
}
