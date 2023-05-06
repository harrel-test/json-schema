package dev.harrel.jsonschema;

import java.net.URI;
import java.util.Objects;
import java.util.UUID;

public final class Validator {
    private final JsonNodeFactory jsonNodeFactory;
    private final SchemaResolver schemaResolver;
    private final SchemaRegistry schemaRegistry;
    private final JsonParser jsonParser;

    Validator(EvaluatorFactory evaluatorFactory, JsonNodeFactory jsonNodeFactory, SchemaResolver schemaResolver, String defaultMetaSchemaUri) {
        this.jsonNodeFactory = Objects.requireNonNull(jsonNodeFactory);
        this.schemaResolver = Objects.requireNonNull(schemaResolver);
        this.schemaRegistry = new SchemaRegistry();
        MetaSchemaValidator metaSchemaValidator = new MetaSchemaValidator(this.schemaRegistry, this.schemaResolver);
        this.jsonParser = new JsonParser(defaultMetaSchemaUri, this.jsonNodeFactory, evaluatorFactory, this.schemaRegistry, metaSchemaValidator);
    }

    public URI registerSchema(String rawSchema) {
        return registerSchema(jsonNodeFactory.create(rawSchema));
    }

    public URI registerSchema(Object schemaProviderNode) {
        return registerSchema(jsonNodeFactory.wrap(schemaProviderNode));
    }

    public URI registerSchema(JsonNode schemaNode) {
        return jsonParser.parseRootSchema(URI.create(UUID.randomUUID().toString()), schemaNode);
    }

    public URI registerSchema(URI uri, String rawSchema) {
        return registerSchema(uri, jsonNodeFactory.create(rawSchema));
    }

    public URI registerSchema(URI uri, Object schemaProviderNode) {
        return registerSchema(uri, jsonNodeFactory.wrap(schemaProviderNode));
    }

    public URI registerSchema(URI uri, JsonNode schemaNode) {
        return jsonParser.parseRootSchema(uri, schemaNode);
    }

    public boolean validate(URI schemaUri, String rawInstance) {
        return validate(schemaUri, jsonNodeFactory.create(rawInstance));
    }

    public boolean validate(URI schemaUri, Object instanceProviderNode) {
        return validate(schemaUri, jsonNodeFactory.wrap(instanceProviderNode));
    }

    public boolean validate(URI schemaUri, JsonNode instanceNode) {
        Schema schema = getRootSchema(schemaUri.toString());
        EvaluationContext ctx = createNewEvaluationContext();
        return schema.validate(ctx, instanceNode);
    }

    private Schema getRootSchema(String uri) {
        Schema schema = schemaRegistry.get(uri);
        if (schema == null) {
            throw new IllegalStateException("Couldn't find schema with uri [%s]".formatted(uri));
        }
        return schema;
    }

    private EvaluationContext createNewEvaluationContext() {
        return new EvaluationContext(jsonParser, schemaRegistry, schemaResolver);
    }
}
