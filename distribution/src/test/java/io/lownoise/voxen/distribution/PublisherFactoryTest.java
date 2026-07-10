package io.lownoise.voxen.distribution;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PublisherFactoryTest {

    @Test
    void shouldReturnRegisteredPublishers() {
        assertThat(PublisherFactory.available())
            .contains("local", "maven-central", "artifactory", "nexus", "s3");
    }

    @Test
    void shouldGetPublisherByName() {
        Publisher local = PublisherFactory.get("local");
        assertThat(local).isInstanceOf(LocalFilePublisher.class);
        assertThat(local.name()).isEqualTo("local");
    }

    @Test
    void shouldGetMavenCentralPublisher() {
        Publisher pub = PublisherFactory.get("maven-central");
        assertThat(pub).isInstanceOf(MavenCentralPublisher.class);
    }

    @Test
    void shouldGetArtifactoryPublisher() {
        Publisher pub = PublisherFactory.get("artifactory");
        assertThat(pub).isInstanceOf(ArtifactoryPublisher.class);
    }

    @Test
    void shouldGetNexusPublisher() {
        Publisher pub = PublisherFactory.get("nexus");
        assertThat(pub).isInstanceOf(NexusPublisher.class);
    }

    @Test
    void shouldGetS3Publisher() {
        Publisher pub = PublisherFactory.get("s3");
        assertThat(pub).isInstanceOf(S3Publisher.class);
    }

    @Test
    void shouldThrowForUnknownPublisher() {
        assertThatThrownBy(() -> PublisherFactory.get("unknown"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("unknown");
    }

    @Test
    void shouldRegisterCustomPublisher() {
        Publisher custom = new LocalFilePublisher();
        PublisherFactory.register(custom);
        assertThat(PublisherFactory.available()).contains("local");
    }
}
