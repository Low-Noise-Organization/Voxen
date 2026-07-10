package io.lownoise.voxen.core;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class TelemetryCollectorTest {

    @Test
    void collectorIsNotOptedInByDefault() {
        TelemetryCollector tc = new TelemetryCollector();
        assertThat(tc.isOptedIn()).isFalse();
    }

    @Test
    void collectorCanBeOptedIn() {
        TelemetryCollector tc = new TelemetryCollector(true);
        assertThat(tc.isOptedIn()).isTrue();
    }

    @Test
    void optOutDoesNotTrackOperations() {
        TelemetryCollector tc = new TelemetryCollector(false);
        tc.startOperation("1", "build", "proj");
        tc.finishOperation("1", true, null);
        assertThat(tc.totalOperations()).isZero();
    }

    @Test
    void optInTracksOperations() {
        TelemetryCollector tc = new TelemetryCollector(true);
        tc.startOperation("1", "build", "proj");
        tc.finishOperation("1", true, null);
        assertThat(tc.totalOperations()).isOne();
        assertThat(tc.failedOperations()).isZero();
    }

    @Test
    void optInTracksFailures() {
        TelemetryCollector tc = new TelemetryCollector(true);
        tc.startOperation("1", "build", "proj");
        tc.finishOperation("1", false, null);
        assertThat(tc.failedOperations()).isOne();
    }

    @Test
    void finishUnknownOperationDoesNotThrow() {
        TelemetryCollector tc = new TelemetryCollector(true);
        assertThatCode(() -> tc.finishOperation("nonexistent", true, null))
            .doesNotThrowAnyException();
    }

    @Test
    void multipleOperationsTracked() {
        TelemetryCollector tc = new TelemetryCollector(true);
        tc.startOperation("1", "build", "proj");
        tc.startOperation("2", "package", "proj");
        tc.finishOperation("1", true, null);
        tc.finishOperation("2", false, null);
        assertThat(tc.totalOperations()).isEqualTo(2);
        assertThat(tc.failedOperations()).isOne();
    }
}
