package com.AutoRent.Backend.security;

import com.AutoRent.Backend.model.Rol;
import com.AutoRent.Backend.model.Usuario;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final byte[] secret;
    private final long expirationMillis;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-millis}") long expirationMillis
    ) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("jwt.secret debe tener al menos 32 caracteres");
        }

        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.expirationMillis = expirationMillis;
    }

    public String generarToken(Usuario usuario) {
        Instant ahora = Instant.now();
        Instant vencimiento = ahora.plusMillis(expirationMillis);

        Map<String, Object> header = Map.of(
                "alg", "HS256",
                "typ", "JWT"
        );

        List<String> roles = usuario.getRoles().stream()
                .map(Rol::getNombre)
                .map(Enum::name)
                .toList();

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", usuario.getEmail());
        payload.put("idUsuario", usuario.getIdUsuario());
        payload.put("nombre", usuario.getNombre());
        payload.put("roles", roles);
        payload.put("iat", ahora.getEpochSecond());
        payload.put("exp", vencimiento.getEpochSecond());

        String headerBase64 = base64Url(toJson(header));
        String payloadBase64 = base64Url(toJson(payload));
        String data = headerBase64 + "." + payloadBase64;

        return data + "." + base64Url(firmar(data));
    }

    public String obtenerEmail(String token) {
        return obtenerPayload(token).get("sub").toString();
    }

    public boolean tokenValido(String token, UserDetails userDetails) {
        String email = obtenerEmail(token);
        return email.equals(userDetails.getUsername()) && !estaExpirado(token) && firmaValida(token);
    }

    private boolean estaExpirado(String token) {
        Number exp = (Number) obtenerPayload(token).get("exp");
        return Date.from(Instant.ofEpochSecond(exp.longValue())).before(new Date());
    }

    private boolean firmaValida(String token) {
        String[] partes = token.split("\\.");
        if (partes.length != 3) {
            return false;
        }

        String data = partes[0] + "." + partes[1];
        String firmaEsperada = base64Url(firmar(data));
        return firmaEsperada.equals(partes[2]);
    }

    private Map<String, Object> obtenerPayload(String token) {
        try {
            String[] partes = token.split("\\.");
            if (partes.length != 3) {
                throw new IllegalArgumentException("Token JWT invalido");
            }

            byte[] payload = Base64.getUrlDecoder().decode(partes[1]);
            return objectMapper.readValue(payload, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new IllegalArgumentException("Token JWT invalido", e);
        }
    }

    private byte[] firmar(String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
            return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo firmar el token JWT", e);
        }
    }

    private String toJson(Map<String, Object> datos) {
        try {
            return objectMapper.writeValueAsString(datos);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo generar el token JWT", e);
        }
    }

    private String base64Url(String valor) {
        return base64Url(valor.getBytes(StandardCharsets.UTF_8));
    }

    private String base64Url(byte[] valor) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(valor);
    }
}
