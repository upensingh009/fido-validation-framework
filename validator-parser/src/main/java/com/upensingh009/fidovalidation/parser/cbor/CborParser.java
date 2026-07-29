package com.upensingh009.fidovalidation.parser.cbor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Minimal CBOR parser wrapper using Jackson CBOR for decoding CBOR to Java maps.
 * Provides convenience methods to decode a CBOR byte[] into a Map.
 */
public class CborParser {
    private final ObjectMapper mapper;

    public CborParser() {
        this.mapper = new ObjectMapper(new CBORFactory());
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> parse(byte[] cbor) throws IOException {
        return mapper.readValue(cbor, Map.class);
    }
}
