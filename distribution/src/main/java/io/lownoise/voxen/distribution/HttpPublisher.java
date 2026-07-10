package io.lownoise.voxen.distribution;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

public abstract class HttpPublisher {

    protected final HttpClient client;

    protected HttpPublisher() {
        this.client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
    }

    protected PublishResult upload(String url, Path artifact, String token) {
        try {
            byte[] data = Files.readAllBytes(artifact);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/octet-stream")
                .header("Authorization", "Bearer " + token)
                .PUT(HttpRequest.BodyPublishers.ofByteArray(data))
                .timeout(Duration.ofMinutes(10))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return PublishResult.success(url, url, "Uploaded to " + url);
            } else {
                return PublishResult.failure("Upload failed: HTTP " + response.statusCode() + " - " + response.body());
            }
        } catch (Exception e) {
            return PublishResult.failure("Upload failed: " + e.getMessage());
        }
    }
}
