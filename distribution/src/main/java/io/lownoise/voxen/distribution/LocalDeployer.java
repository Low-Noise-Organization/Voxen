package io.lownoise.voxen.distribution;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class LocalDeployer implements Deployer {

    @Override
    public String name() {
        return "local";
    }

    @Override
    public DeployResult deploy(Path artifact, String target) {
        Path targetDir = Path.of(target != null ? target : "deploy");

        try {
            Files.createDirectories(targetDir);

            if (Files.isDirectory(artifact)) {
                copyDirectory(artifact, targetDir.resolve(artifact.getFileName()));
            } else {
                Files.copy(artifact, targetDir.resolve(artifact.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            }

            return DeployResult.success(
                targetDir.toString(),
                targetDir.toUri().toString(),
                "Deployed to " + targetDir);
        } catch (IOException e) {
            return DeployResult.failure("Failed to deploy: " + e.getMessage());
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
