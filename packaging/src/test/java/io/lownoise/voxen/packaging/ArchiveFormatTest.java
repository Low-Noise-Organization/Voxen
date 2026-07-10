package io.lownoise.voxen.packaging;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ArchiveFormatTest {

    @Test
    void shouldParseZip() {
        assertThat(ArchiveFormat.fromString("zip")).isEqualTo(ArchiveFormat.ZIP);
    }

    @Test
    void shouldParseTarGz() {
        assertThat(ArchiveFormat.fromString("tar.gz")).isEqualTo(ArchiveFormat.TAR_GZ);
        assertThat(ArchiveFormat.fromString("tgz")).isEqualTo(ArchiveFormat.TAR_GZ);
    }

    @Test
    void shouldParseTarBz2() {
        assertThat(ArchiveFormat.fromString("tar.bz2")).isEqualTo(ArchiveFormat.TAR_BZ2);
        assertThat(ArchiveFormat.fromString("tbz2")).isEqualTo(ArchiveFormat.TAR_BZ2);
    }

    @Test
    void shouldRejectUnknownFormat() {
        assertThatThrownBy(() -> ArchiveFormat.fromString("rar"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldProvideExtensions() {
        assertThat(ArchiveFormat.ZIP.extension()).isEqualTo(".zip");
        assertThat(ArchiveFormat.TAR_GZ.extension()).isEqualTo(".tar.gz");
        assertThat(ArchiveFormat.TAR_BZ2.extension()).isEqualTo(".tar.bz2");
    }
}
