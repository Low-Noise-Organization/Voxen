package io.lownoise.voxen.packaging;

import io.lownoise.voxen.plugins.api.VoxenException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class PackagingEngine {

    public Path createArchive(Path sourceDir, Path outputFile, ArchiveFormat format) {
        return switch (format) {
            case ZIP -> createZip(sourceDir, outputFile);
            case TAR_GZ -> createTarGz(sourceDir, outputFile);
            case TAR_BZ2 -> createTarBz2(sourceDir, outputFile);
        };
    }

    public Path createZip(Path sourceDir, Path outputFile) {
        Path result = outputFile.toString().endsWith(".zip") ? outputFile : outputFile.resolveSibling(outputFile.getFileName() + ".zip");
        try {
            Files.createDirectories(result.getParent());
            try (var zipOut = new java.util.zip.ZipOutputStream(Files.newOutputStream(result))) {
                Files.walkFileTree(sourceDir, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Path relative = sourceDir.relativize(file);
                        zipOut.putNextEntry(new java.util.zip.ZipEntry(relative.toString()));
                        Files.copy(file, zipOut);
                        zipOut.closeEntry();
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
            return result;
        } catch (IOException e) {
            throw new VoxenException(
                "Failed to create ZIP archive: " + e.getMessage(),
                "Check that the source directory exists and the output path is writable.",
                e
            );
        }
    }

    public Path createTarGz(Path sourceDir, Path outputFile) {
        Path result = outputFile.toString().endsWith(".tar.gz") ? outputFile : outputFile.resolveSibling(outputFile.getFileName() + ".tar.gz");
        try {
            Files.createDirectories(result.getParent());
            try (OutputStream fos = Files.newOutputStream(result);
                 OutputStream gzos = new java.util.zip.GZIPOutputStream(fos)) {
                writeTar(sourceDir, gzos);
            }
            return result;
        } catch (IOException e) {
            throw new VoxenException(
                "Failed to create tar.gz archive: " + e.getMessage(),
                "Check that the source directory exists and the output path is writable.",
                e
            );
        }
    }

    public Path createTarBz2(Path sourceDir, Path outputFile) {
        Path result = outputFile.toString().endsWith(".tar.bz2") ? outputFile : outputFile.resolveSibling(outputFile.getFileName() + ".tar.bz2");
        try {
            Files.createDirectories(result.getParent());
            try (OutputStream fos = Files.newOutputStream(result);
                 OutputStream bz2os = new org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream(fos)) {
                writeTar(sourceDir, bz2os);
            }
            return result;
        } catch (IOException e) {
            throw new VoxenException(
                "Failed to create tar.bz2 archive: " + e.getMessage(),
                "Check that the source directory exists and the output path is writable.",
                e
            );
        }
    }

    private void writeTar(Path sourceDir, OutputStream out) throws IOException {
        try (var tarOut = new org.apache.commons.compress.archivers.tar.TarArchiveOutputStream(out)) {
            tarOut.setLongFileMode(org.apache.commons.compress.archivers.tar.TarArchiveOutputStream.LONGFILE_POSIX);
            Files.walkFileTree(sourceDir, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path relative = sourceDir.relativize(file);
                    var entry = new org.apache.commons.compress.archivers.tar.TarArchiveEntry(file.toFile(), relative.toString());
                    entry.setSize(attrs.size());
                    tarOut.putArchiveEntry(entry);
                    Files.copy(file, tarOut);
                    tarOut.closeArchiveEntry();
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    public List<Path> collectArtifacts(Path directory, String... extensions) {
        List<Path> artifacts = new ArrayList<>();
        if (!Files.exists(directory)) {
            return artifacts;
        }
        try (var files = Files.walk(directory)) {
            files.filter(Files::isRegularFile)
                .filter(p -> {
                    for (String ext : extensions) {
                        if (p.toString().endsWith(ext)) return true;
                    }
                    return false;
                })
                .forEach(artifacts::add);
            return artifacts;
        } catch (IOException e) {
            return artifacts;
        }
    }
}
