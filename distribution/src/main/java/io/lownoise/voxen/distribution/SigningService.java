package io.lownoise.voxen.distribution;

import io.lownoise.voxen.plugins.api.VoxenException;

import java.nio.file.Files;
import java.nio.file.Path;

public class SigningService {

    public static void sign(Path artifact, String keyId, String passphrase) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "gpg", "--batch", "--yes",
                "--local-user", keyId,
                "--passphrase", passphrase,
                "--detach-sign", "--armor",
                artifact.toAbsolutePath().toString()
            );
            pb.inheritIO().redirectOutput(ProcessBuilder.Redirect.DISCARD);
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new VoxenException(
                    "GPG signing failed with exit code " + exitCode,
                    "Check that GPG is installed and the key '" + keyId + "' is available."
                );
            }
        } catch (VoxenException e) {
            throw e;
        } catch (Exception e) {
            throw new VoxenException(
                "Failed to sign artifact: " + e.getMessage(),
                "Ensure GPG is installed and configured."
            );
        }
    }

    public static boolean isGpgAvailable() {
        try {
            Process pb = new ProcessBuilder("gpg", "--version")
                .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                .redirectError(ProcessBuilder.Redirect.DISCARD)
                .start();
            return pb.waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }
}
