package io.lownoise.voxen.distribution;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class LocalDeployerTest {

    @Test
    void shouldDeployFile(@TempDir Path tempDir) throws Exception {
        Path artifact = tempDir.resolve("app.jar");
        Files.writeString(artifact, "fake jar");
        Path target = tempDir.resolve("deploy");

        LocalDeployer deployer = new LocalDeployer();
        DeployResult result = deployer.deploy(artifact, target.toString());

        assertThat(result.success()).isTrue();
        assertThat(result.target()).contains("deploy");
        assertThat(Files.exists(target.resolve("app.jar"))).isTrue();
    }

    @Test
    void shouldDeployWithDefaultTarget(@TempDir Path tempDir) throws Exception {
        Path artifact = tempDir.resolve("app.jar");
        Files.writeString(artifact, "fake jar");

        LocalDeployer deployer = new LocalDeployer();
        DeployResult result = deployer.deploy(artifact, null);

        assertThat(result.success()).isTrue();
    }
}
