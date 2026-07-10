package io.lownoise.voxen.docker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class SlsaProvenanceTest {

    private final SlsaProvenance provenance = new SlsaProvenance();
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void generatesValidProvenance() {
        var metadata = BuildMetadata.builder()
            .projectName("my-app")
            .version("1.0.0")
            .commit("abc123")
            .repository("https://github.com/user/my-app")
            .checksumSha256("aabbccdd")
            .buildId("build-1")
            .profile("prod")
            .runtime("java")
            .nativeImage(false)
            .build();

        ObjectNode node = provenance.generate(metadata);

        assertThat(node.get("_type").asText()).contains("in-toto");
        assertThat(node.get("predicateType").asText()).contains("slsa.dev/provenance");
        assertThat(node.get("subject")).isNotEmpty();
        assertThat(node.get("subject").get(0).get("name").asText()).isEqualTo("my-app.tar.gz");
        assertThat(node.get("predicate").get("builder").get("id").asText()).contains("voxen.dev");
        assertThat(node.get("predicate").get("invocation").get("configSource").get("uri").asText())
            .isEqualTo("https://github.com/user/my-app");
        assertThat(node.get("predicate").get("metadata").get("buildInvocationId").asText())
            .isEqualTo("build-1");
    }

    @Test
    void containsBuildConfig() {
        var metadata = BuildMetadata.builder()
            .projectName("my-app")
            .version("2.0.0")
            .build();

        ObjectNode node = provenance.generate(metadata);

        assertThat(node.get("predicate").get("buildConfig").get("version").asText())
            .isEqualTo("2.0.0");
    }
}
