package io.lownoise.voxen.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import io.lownoise.voxen.plugins.api.VoxenException;

import java.io.InputStream;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigValidator {

    private static final String SCHEMA_PATH = "/io/lownoise/voxen/core/voxen-schema.json";
    private static final JsonSchema schema = loadSchema();

    private static JsonSchema loadSchema() {
        try (InputStream is = ConfigValidator.class.getResourceAsStream(SCHEMA_PATH)) {
            if (is == null) {
                throw new RuntimeException("Schema file not found: " + SCHEMA_PATH);
            }
            var factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
            return factory.getSchema(is);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration schema", e);
        }
    }

    public static void validate(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(json);
            Set<ValidationMessage> errors = schema.validate(node);

            if (!errors.isEmpty()) {
                String details = errors.stream()
                    .map(ValidationMessage::getMessage)
                    .collect(Collectors.joining("\n  - ", "  - ", ""));
                throw new VoxenException(
                    "voxen.json contains " + errors.size() + " validation error(s):\n" + details,
                    "Fix the errors above and try again. See https://voxen.dev/docs/configuration for the full schema."
                );
            }
        } catch (VoxenException e) {
            throw e;
        } catch (Exception e) {
            throw new VoxenException(
                "Failed to validate voxen.json: " + e.getMessage(),
                "Check that the file contains valid JSON."
            );
        }
    }
}
