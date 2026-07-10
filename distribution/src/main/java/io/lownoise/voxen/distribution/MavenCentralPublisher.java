package io.lownoise.voxen.distribution;

import java.nio.file.Path;

public class MavenCentralPublisher extends HttpPublisher implements Publisher {

    @Override
    public String name() {
        return "maven-central";
    }

    @Override
    public PublishResult publish(Path artifact, String repository, String groupId, String artifactId, String version) {
        CredentialManager.CredentialEntry creds = CredentialManager.get("maven-central");
        if (creds == null) {
            creds = CredentialManager.fromEnvironment("MAVEN_CENTRAL");
        }
        if (creds == null) {
            return PublishResult.failure(
                "No credentials found for Maven Central.\n" +
                "Set MAVEN_CENTRAL_TOKEN environment variable or configure ~/.voxen/credentials.json");
        }

        String baseUrl = creds.host() != null ? creds.host() : "https://central.sonatype.com";
        String url = baseUrl + "/api/v1/publisher/upload?groupId=" + groupId
            + "&artifactId=" + artifactId + "&version=" + version;

        PublishResult result = upload(url, artifact, creds.token());
        if (result.success()) {
            ChecksumGenerator.writeChecksumFiles(artifact);
        }
        return result;
    }
}
