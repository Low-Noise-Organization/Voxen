package io.lownoise.voxen.distribution;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class SbomGenerator {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String FORMAT = "CycloneDX";
    private static final String SPEC_VERSION = "1.6";

    public static void generateCycloneDx(Path artifact, String groupId, String artifactId, String version, Path outputDir) {
        try {
            Files.createDirectories(outputDir);

            ObjectNode bom = MAPPER.createObjectNode();
            bom.put("bomFormat", FORMAT);
            bom.put("specVersion", SPEC_VERSION);
            bom.put("serialNumber", "urn:uuid:" + java.util.UUID.randomUUID());
            bom.put("version", 1);

            ObjectNode metadata = MAPPER.createObjectNode();
            ObjectNode component = MAPPER.createObjectNode();
            component.put("type", "library");
            component.put("name", groupId + ":" + artifactId);
            component.put("version", version);
            component.put("purl", "pkg:maven/" + groupId.replace('.', '/') + "/" + artifactId + "@" + version);

            ObjectNode properties = MAPPER.createObjectNode();
            properties.put("timestamp", DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC).format(Instant.now()));
            component.set("properties", properties);

            metadata.set("component", component);
            bom.set("metadata", metadata);

            ArrayNode components = MAPPER.createArrayNode();
            ObjectNode artifactComponent = MAPPER.createObjectNode();
            artifactComponent.put("type", "file");
            artifactComponent.put("name", artifact.getFileName().toString());
            try {
                artifactComponent.put("size", Files.size(artifact));
            } catch (Exception ignored) {}
            components.add(artifactComponent);
            bom.set("components", components);

            Path bomFile = outputDir.resolve(artifactId + "-" + version + "-bom.json");
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(bomFile.toFile(), bom);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate SBOM: " + e.getMessage(), e);
        }
    }
}
