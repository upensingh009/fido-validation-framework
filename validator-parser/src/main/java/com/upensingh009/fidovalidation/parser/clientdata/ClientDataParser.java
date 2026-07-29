package com.upensingh009.fidovalidation.parser.clientdata;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

/**
 * Parses clientDataJSON into a ClientData record.
 */
public class ClientDataParser {
    private final ObjectMapper mapper = new ObjectMapper();

    public ClientData parse(byte[] clientDataJsonBytes) throws Exception {
        String json = new String(clientDataJsonBytes, StandardCharsets.UTF_8);
        Map<String, Object> map = mapper.readValue(json, Map.class);

        String type = (String) map.getOrDefault("type", "");
        String challengeB64 = (String) map.getOrDefault("challenge", "");
        String origin = (String) map.getOrDefault("origin", "");

        // Some clients base64url-encode the challenge; normalize by decoding if possible
        String challenge = challengeB64;
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(challengeB64);
            challenge = Base64.getUrlEncoder().withoutPadding().encodeToString(decoded);
        } catch (IllegalArgumentException ignored) {
            // keep original
        }

        return new ClientData(type, challenge, origin, json);
    }
}
