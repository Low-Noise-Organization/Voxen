package io.lownoise.voxen.docker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

public class SlsaProvenance {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public ObjectNode generate(BuildMetadata metadata) {
        ObjectNode provenance = MAPPER.createObjectNode();

        provenance.put("_type", "https://in-toto.io/Statement/v1");
        provenance.put("predicateType", "https://slsa.dev/provenance/v1");

        ObjectNode subject = MAPPER.createObjectNode();
        subject.put("name", metadata.projectName() + ".tar.gz");
        subject.put("digest", MAPPER.createObjectNode()
            .put("sha256", metadata.checksumSha256() != null ? metadata.checksumSha256() : ""));
        ArrayNode subjects = MAPPER.createArrayNode();
        subjects.add(subject);
        provenance.set("subject", subjects);

        ObjectNode predicate = MAPPER.createObjectNode();

        ObjectNode builder = MAPPER.createObjectNode();
        builder.put("id", "https://voxen.dev/builders/java/" + metadata.voxenVersion());
        predicate.set("builder", builder);

        ObjectNode buildMetadata2 = MAPPER.createObjectNode();
        ObjectNode invocation = MAPPER.createObjectNode();

        ObjectNode configSource = MAPPER.createObjectNode();
        configSource.put("uri", metadata.repository());
        configSource.put("digest", MAPPER.createObjectNode()
            .put("sha1", metadata.commit()));
        configSource.put("entryPoint", "voxen dockerize --tag " + metadata.buildId());
        invocation.set("configSource", configSource);

        ObjectNode parameters = MAPPER.createObjectNode();
        parameters.put("profile", metadata.profile());
        parameters.put("runtime", metadata.runtime());
        parameters.put("nativeImage", metadata.nativeImage());
        invocation.set("parameters", parameters);

        ObjectNode environment = MAPPER.createObjectNode();
        environment.put("voxenVersion", metadata.voxenVersion());
        environment.put("buildTimestamp", metadata.timestamp());
        invocation.set("environment", environment);

        predicate.set("invocation", invocation);

        ObjectNode buildConfig = MAPPER.createObjectNode();
        buildConfig.put("version", metadata.version());
        predicate.set("buildConfig", buildConfig);

        ObjectNode metadata3 = MAPPER.createObjectNode();
        metadata3.put("buildInvocationId", metadata.buildId());
        metadata3.put("buildStartedOn", metadata.timestamp());
        metadata3.put("buildFinishedOn", Instant.now().toString());
        metadata3.put("completeness", MAPPER.createObjectNode()
            .put("parameters", true).put("environment", false).put("materials", false));
        predicate.set("metadata", metadata3);

        ArrayNode materials = MAPPER.createArrayNode();
        ObjectNode material = MAPPER.createObjectNode();
        material.put("uri", metadata.repository());
        material.put("digest", MAPPER.createObjectNode()
            .put("sha1", metadata.commit()));
        materials.add(material);
        predicate.set("materials", materials);

        provenance.set("predicate", predicate);

        return provenance;
    }

    public void write(Path outputPath, BuildMetadata metadata) throws Exception {
        ObjectNode provenance = generate(metadata);
        Files.createDirectories(outputPath.getParent());
        MAPPER.writerWithDefaultPrettyPrinter().writeValue(outputPath.toFile(), provenance);
    }
}
