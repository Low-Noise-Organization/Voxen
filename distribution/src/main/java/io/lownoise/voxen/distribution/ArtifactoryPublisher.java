package io.lownoise.voxen.distribution;

import java.nio.file.Path;

public class ArtifactoryPublisher extends HttpPublisher implements Publisher {

    @Override
    public String name() {
        return "artifactory";
    }

    @Override
    public PublishResult publish(Path artifact, String repository, String groupId, String artifactId, String version) {
        CredentialManager.CredentialEntry creds = CredentialManager.get("artifactory");
        if (creds == null) {
            creds = CredentialManager.fromEnvironment("ARTIFACTORY");
        }
        if (creds == null) {
            return PublishResult.failure(
                "No credentials found for Artifactory.\n" +
                "Set ARTIFACTORY_HOST/TOKEN environment variables or configure ~/.voxen/credentials.json");
        }

        String repo = repository != null ? repository : "libs-release-local";
        String baseUrl = creds.host();
        String url = baseUrl + "/" + repo + "/"
            + groupId.replace('.', '/') + "/"
            + artifactId + "/" + version + "/" + artifact.getFileName();

        PublishResult result = upload(url, artifact, creds.token());
        if (result.success()) {
            ChecksumGenerator.writeChecksumFiles(artifact);
        }
        return result;
    }
}
