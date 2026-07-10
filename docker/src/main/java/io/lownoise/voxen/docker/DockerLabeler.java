package io.lownoise.voxen.docker;

import io.lownoise.voxen.core.ProjectConfig;

import java.util.ArrayList;
import java.util.List;

public class DockerLabeler {

    public static List<String> generateLabels(BuildMetadata metadata, ProjectConfig config) {
        List<String> labels = new ArrayList<>();

        labels.add("org.opencontainers.image.created=" + quote(metadata.timestamp()));
        labels.add("org.opencontainers.image.version=" + quote(metadata.version()));
        labels.add("org.opencontainers.image.revision=" + quote(metadata.commit()));
        labels.add("org.opencontainers.image.source=" + quote(metadata.repository()));
        labels.add("org.opencontainers.image.title=" + quote(metadata.projectName()));
        labels.add("org.opencontainers.image.description=" + quote(metadata.description()));
        labels.add("org.opencontainers.image.licenses=" + quote(metadata.license()));
        labels.add("org.opencontainers.image.vendor=Voxen");

        labels.add("voxen.build.id=" + quote(metadata.buildId()));
        labels.add("voxen.build.timestamp=" + quote(metadata.timestamp()));
        labels.add("voxen.build.profile=" + quote(metadata.profile()));
        labels.add("voxen.build.runtime=" + quote(metadata.runtime()));
        labels.add("voxen.build.plugin=" + quote(metadata.pluginName()));
        labels.add("voxen.build.version=" + quote(metadata.voxenVersion()));

        if (metadata.checksumSha256() != null) {
            labels.add("voxen.checksum.sha256=" + quote(metadata.checksumSha256()));
        }
        if (metadata.checksumSha512() != null) {
            labels.add("voxen.checksum.sha512=" + quote(metadata.checksumSha512()));
        }
        if (metadata.checksumMd5() != null) {
            labels.add("voxen.checksum.md5=" + quote(metadata.checksumMd5()));
        }

        labels.add("voxen.sbom.filename=bom.json");
        labels.add("voxen.sbom.format=CycloneDX-1.6");
        labels.add("voxen.signed=" + (metadata.signed() ? "true" : "false"));
        if (metadata.signingKey() != null) {
            labels.add("voxen.signed.key=" + quote(metadata.signingKey()));
        }

        labels.add("voxen.generated=true");

        return labels;
    }

    private static String quote(String value) {
        if (value == null) return "\"\"";
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
