package io.lownoise.voxen.distribution;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class LocalFilePublisher implements Publisher {

    @Override
    public String name() {
        return "local";
    }

    @Override
    public PublishResult publish(Path artifact, String repository, String groupId, String artifactId, String version) {
        Path outputDir = Path.of(repository != null ? repository : "dist")
            .resolve(groupId.replace('.', '/'))
            .resolve(artifactId)
            .resolve(version);

        try {
            Files.createDirectories(outputDir);

            if (Files.isDirectory(artifact)) {
                copyDirectory(artifact, outputDir.resolve(artifact.getFileName()));
            } else {
                Files.copy(artifact, outputDir.resolve(artifact.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            }

            return PublishResult.success(
                outputDir.toString(),
                outputDir.toUri().toString(),
                "Published to " + outputDir);
        } catch (IOException e) {
            return PublishResult.failure("Failed to publish: " + e.getMessage());
        }
    }

    private void copyDirectory(Path source, Path target) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Files.createDirectories(target.resolve(source.relativize(dir)));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, target.resolve(source.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
