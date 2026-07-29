package com.upensingh009.fidovalidation.parser.clientdata;

/**
 * Immutable client data record parsed from clientDataJSON.
 */
public record ClientData(String type, String challenge, String origin, String rawJson) {
}
