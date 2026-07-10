package io.lownoise.voxen.distribution;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lownoise.voxen.plugins.api.VoxenException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CredentialManager {

    private static final Path CREDENTIALS_FILE = Path.of(System.getProperty("user.home"), ".voxen", "credentials.json");
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Map<String, CredentialEntry> cache = new ConcurrentHashMap<>();

    public record CredentialEntry(String username, String password, String token, String host) {}

    public static CredentialEntry get(String repositoryId) {
        return cache.computeIfAbsent(repositoryId, id -> {
            try {
                loadCredentials();
                return cache.get(id);
            } catch (Exception e) {
                return null;
            }
        });
    }

    public static void store(String repositoryId, CredentialEntry entry) {
        cache.put(repositoryId, entry);
        saveCredentials();
    }

    public static CredentialEntry fromEnvironment(String prefix) {
        String username = System.getenv(prefix + "_USERNAME");
        String password = System.getenv(prefix + "_PASSWORD");
        String token = System.getenv(prefix + "_TOKEN");
        String host = System.getenv(prefix + "_HOST");
        if (token != null || (username != null && password != null)) {
            return new CredentialEntry(username, password, token, host);
        }
        return null;
    }

    private static void loadCredentials() {
        try {
            if (Files.exists(CREDENTIALS_FILE)) {
                String json = Files.readString(CREDENTIALS_FILE);
                @SuppressWarnings("unchecked")
                Map<String, Map<String, String>> data = MAPPER.readValue(json, Map.class);
                data.forEach((id, values) -> {
                    cache.put(id, new CredentialEntry(
                        values.get("username"),
                        values.get("password"),
                        values.get("token"),
                        values.get("host")
                    ));
                });
            }
        } catch (Exception e) {
            throw new VoxenException(
                "Failed to read credentials from " + CREDENTIALS_FILE,
                "Check the file format or delete it and reconfigure."
            );
        }
    }

    private static void saveCredentials() {
        try {
            Files.createDirectories(CREDENTIALS_FILE.getParent());
            var data = new java.util.LinkedHashMap<String, java.util.Map<String, String>>();
            cache.forEach((id, entry) -> {
                var values = new java.util.LinkedHashMap<String, String>();
                if (entry.username() != null) values.put("username", entry.username());
                if (entry.password() != null) values.put("password", entry.password());
                if (entry.token() != null) values.put("token", entry.token());
                if (entry.host() != null) values.put("host", entry.host());
                data.put(id, values);
            });
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(CREDENTIALS_FILE.toFile(), data);
        } catch (Exception e) {
            throw new VoxenException(
                "Failed to write credentials to " + CREDENTIALS_FILE,
                "Check that " + CREDENTIALS_FILE.getParent() + " is writable."
            );
        }
    }
}
