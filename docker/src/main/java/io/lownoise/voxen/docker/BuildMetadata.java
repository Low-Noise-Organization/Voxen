package io.lownoise.voxen.docker;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public record BuildMetadata(
    String buildId,
    String timestamp,
    String voxenVersion,
    String projectName,
    String version,
    String runtime,
    String pluginName,
    String profile,
    String commit,
    String repository,
    String checksumSha256,
    String checksumSha512,
    String checksumMd5,
    boolean signed,
    String signingKey,
    String artifactName,
    long artifactSize,
    boolean nativeImage,
    String buildImage,
    String buildCommand,
    boolean needsBuildStage,
    boolean createUser,
    String port,
    String description,
    String license,
    String ascFile,
    boolean hasSbom
) {

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.of("UTC"));

    public static BuildMetadata.Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String buildId = UUID.randomUUID().toString();
        private String timestamp = FORMATTER.format(Instant.now());
        private String voxenVersion = "0.1.0-SNAPSHOT";
        private String projectName = "app";
        private String version = "latest";
        private String runtime = "java";
        private String pluginName = "unknown";
        private String profile = "dev";
        private String commit = "";
        private String repository = "";
        private String checksumSha256;
        private String checksumSha512;
        private String checksumMd5;
        private boolean signed;
        private String signingKey;
        private String artifactName;
        private long artifactSize;
        private boolean nativeImage;
        private String buildImage;
        private String buildCommand;
        private boolean needsBuildStage;
        private boolean createUser = true;
        private String port;
        private String description = "";
        private String license = "Apache-2.0";
        private String ascFile;
        private boolean hasSbom;

        public Builder buildId(String v) { this.buildId = v; return this; }
        public Builder timestamp(String v) { this.timestamp = v; return this; }
        public Builder voxenVersion(String v) { this.voxenVersion = v; return this; }
        public Builder projectName(String v) { this.projectName = v; return this; }
        public Builder version(String v) { this.version = v; return this; }
        public Builder runtime(String v) { this.runtime = v; return this; }
        public Builder pluginName(String v) { this.pluginName = v; return this; }
        public Builder profile(String v) { this.profile = v; return this; }
        public Builder commit(String v) { this.commit = v; return this; }
        public Builder repository(String v) { this.repository = v; return this; }
        public Builder checksumSha256(String v) { this.checksumSha256 = v; return this; }
        public Builder checksumSha512(String v) { this.checksumSha512 = v; return this; }
        public Builder checksumMd5(String v) { this.checksumMd5 = v; return this; }
        public Builder signed(boolean v) { this.signed = v; return this; }
        public Builder signingKey(String v) { this.signingKey = v; return this; }
        public Builder artifactName(String v) { this.artifactName = v; return this; }
        public Builder artifactSize(long v) { this.artifactSize = v; return this; }
        public Builder nativeImage(boolean v) { this.nativeImage = v; return this; }
        public Builder buildImage(String v) { this.buildImage = v; return this; }
        public Builder buildCommand(String v) { this.buildCommand = v; return this; }
        public Builder needsBuildStage(boolean v) { this.needsBuildStage = v; return this; }
        public Builder createUser(boolean v) { this.createUser = v; return this; }
        public Builder port(String v) { this.port = v; return this; }
        public Builder description(String v) { this.description = v; return this; }
        public Builder license(String v) { this.license = v; return this; }
        public Builder ascFile(String v) { this.ascFile = v; return this; }
        public Builder hasSbom(boolean v) { this.hasSbom = v; return this; }

        public BuildMetadata build() {
            return new BuildMetadata(buildId, timestamp, voxenVersion, projectName,
                version, runtime, pluginName, profile, commit, repository,
                checksumSha256, checksumSha512, checksumMd5, signed, signingKey,
                artifactName, artifactSize, nativeImage, buildImage, buildCommand,
                needsBuildStage, createUser, port, description, license, ascFile, hasSbom);
        }
    }
}
