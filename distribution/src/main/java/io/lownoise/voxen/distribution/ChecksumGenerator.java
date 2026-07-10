package io.lownoise.voxen.distribution;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;

public class ChecksumGenerator {

    public record Checksums(String sha256, String sha512, String md5) {}

    public static Checksums generate(Path file) {
        try {
            String sha256 = digest(file, "SHA-256");
            String sha512 = digest(file, "SHA-512");
            String md5 = digest(file, "MD5");
            return new Checksums(sha256, sha512, md5);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate checksums for " + file, e);
        }
    }

    public static void writeChecksumFiles(Path artifact) {
        try {
            Checksums checksums = generate(artifact);
            writeDigest(artifact.resolveSibling(artifact.getFileName() + ".sha256"), checksums.sha256());
            writeDigest(artifact.resolveSibling(artifact.getFileName() + ".sha512"), checksums.sha512());
            writeDigest(artifact.resolveSibling(artifact.getFileName() + ".md5"), checksums.md5());
        } catch (Exception e) {
            throw new RuntimeException("Failed to write checksum files for " + artifact, e);
        }
    }

    private static String digest(Path file, String algorithm) throws Exception {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        try (InputStream is = Files.newInputStream(file);
             DigestInputStream dis = new DigestInputStream(is, md)) {
            byte[] buffer = new byte[8192];
            while (dis.read(buffer) != -1) {}
        }
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static void writeDigest(Path path, String digest) throws Exception {
        Files.writeString(path, digest + "  " + path.getFileName().toString().replaceAll("\\.(sha256|sha512|md5)$", "") + "\n");
    }
}
