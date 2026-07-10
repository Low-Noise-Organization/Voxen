package io.lownoise.voxen.cli;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class OutputFormatterTest {

    @Test
    void dockerizeJsonResult() {
        var result = new OutputFormatter.DockerizeJsonResult(
            "success", "my-app", "ghcr.io/user/app:1.0",
            "/path/to/Dockerfile", true);
        var node = result.node();
        assertThat(node.get("status").asText()).isEqualTo("success");
        assertThat(node.get("project").asText()).isEqualTo("my-app");
        assertThat(node.get("tag").asText()).isEqualTo("ghcr.io/user/app:1.0");
        assertThat(node.get("pushed").asBoolean()).isTrue();
    }

    @Test
    void publishJsonResultWithUrl() {
        var result = new OutputFormatter.PublishJsonResult(
            "success", "my-app", "ghcr.io/user", "ghcr.io/user/app:1.0");
        var node = result.node();
        assertThat(node.get("status").asText()).isEqualTo("success");
        assertThat(node.get("url").asText()).isEqualTo("ghcr.io/user/app:1.0");
    }

    @Test
    void publishJsonResultNullUrl() {
        var result = new OutputFormatter.PublishJsonResult(
            "failure", "my-app", "", null);
        var node = result.node();
        assertThat(node.get("status").asText()).isEqualTo("failure");
        assertThat(node.has("url")).isFalse();
    }

    @Test
    void messageResult() {
        var result = new OutputFormatter.MessageResult("ok", "all good");
        var node = result.node();
        assertThat(node.get("status").asText()).isEqualTo("ok");
        assertThat(node.get("message").asText()).isEqualTo("all good");
    }

    @Test
    void buildJsonResult() {
        var result = new OutputFormatter.BuildJsonResult(
            "success", "my-app", 1234,
            java.util.List.of("app.jar"), "/tmp/build");
        var node = result.node();
        assertThat(node.get("durationMs").asLong()).isEqualTo(1234);
        assertThat(node.get("artifacts").get(0).asText()).isEqualTo("app.jar");
    }

    @Test
    void outputFormatterJsonFlag() {
        assertThat(new OutputFormatter(true).isJson()).isTrue();
        assertThat(new OutputFormatter(false).isJson()).isFalse();
    }
}
