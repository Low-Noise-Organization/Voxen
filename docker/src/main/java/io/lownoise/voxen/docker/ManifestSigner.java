package io.lownoise.voxen.docker;

import java.nio.file.Path;

public class ManifestSigner {

    public boolean signWithGpg(String imageTag, String keyId, String passphrase) {
        try {
            String[] cmd = {
                "docker", "trust", "sign",
                "--local", keyId,
                imageTag
            };
            ProcessBuilder pb = new ProcessBuilder(cmd)
                .inheritIO()
                .redirectErrorStream(true);
            Process p = pb.start();
            int exit = p.waitFor();
            return exit == 0;
        } catch (Exception e) {
            System.err.println("GPG signing failed: " + e.getMessage());
            return false;
        }
    }

    public boolean signWithCosign(String imageTag, String cosignKeyPath) {
        try {
            String[] cmd = {
                "cosign", "sign",
                "--key", cosignKeyPath,
                "--yes",
                imageTag
            };
            ProcessBuilder pb = new ProcessBuilder(cmd)
                .inheritIO()
                .redirectErrorStream(true);
            Process p = pb.start();
            int exit = p.waitFor();
            return exit == 0;
        } catch (Exception e) {
            System.err.println("Cosign signing failed: " + e.getMessage());
            return false;
        }
    }

    public boolean signWithCosignKeyless(String imageTag) {
        try {
            String[] cmd = {
                "cosign", "sign",
                "--yes",
                imageTag
            };
            ProcessBuilder pb = new ProcessBuilder(cmd)
                .inheritIO()
                .redirectErrorStream(true);
            Process p = pb.start();
            int exit = p.waitFor();
            return exit == 0;
        } catch (Exception e) {
            System.err.println("Cosign keyless signing failed: " + e.getMessage());
            return false;
        }
    }

    public boolean verify(String imageTag) {
        try {
            String[] cmd = {"cosign", "verify", imageTag};
            ProcessBuilder pb = new ProcessBuilder(cmd)
                .inheritIO()
                .redirectErrorStream(true);
            Process p = pb.start();
            int exit = p.waitFor();
            return exit == 0;
        } catch (Exception e) {
            System.err.println("Verification failed: " + e.getMessage());
            return false;
        }
    }
}
