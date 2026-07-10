package io.lownoise.voxen.docker;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ManifestSignerTest {

    private final ManifestSigner signer = new ManifestSigner();

    @Test
    void signWithGpgFailsGracefullyWhenNoDockerTrust() {
        boolean result = signer.signWithGpg("nonexistent:latest", "testkey", "passphrase");
        assertThat(result).isFalse();
    }

    @Test
    void signWithCosignFailsGracefullyWhenNoCosign() {
        boolean result = signer.signWithCosign("nonexistent:latest", "/tmp/nonexistent.key");
        assertThat(result).isFalse();
    }

    @Test
    void signWithCosignKeylessFailsGracefullyWhenNoCosign() {
        boolean result = signer.signWithCosignKeyless("nonexistent:latest");
        assertThat(result).isFalse();
    }

    @Test
    void verifyFailsGracefullyWhenNoCosign() {
        boolean result = signer.verify("nonexistent:latest");
        assertThat(result).isFalse();
    }
}
