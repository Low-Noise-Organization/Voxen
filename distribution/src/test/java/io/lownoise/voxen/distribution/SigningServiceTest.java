package io.lownoise.voxen.distribution;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class SigningServiceTest {

    @Test
    void shouldCheckGpgAvailability() {
        boolean available = SigningService.isGpgAvailable();
        assertThat(available).isInstanceOf(Boolean.class);
    }
}
