package dev.harrel.jsonschema;

public interface JsonNodeFactory {
    JsonNode wrap(Object node);
    JsonNode create(String rawJson);
}