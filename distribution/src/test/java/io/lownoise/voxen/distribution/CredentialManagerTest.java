package io.lownoise.voxen.distribution;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CredentialManagerTest {

    @Test
    void shouldReturnNullForUnknownRepository() {
        assertThat(CredentialManager.get("nonexistent")).isNull();
    }

    @Test
    void shouldStoreAndRetrieveCredentials() {
        CredentialManager.CredentialEntry entry = new CredentialManager.CredentialEntry("user", "pass", null, "host.com");
        CredentialManager.store("test-repo", entry);

        CredentialManager.CredentialEntry retrieved = CredentialManager.get("test-repo");
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.username()).isEqualTo("user");
        assertThat(retrieved.password()).isEqualTo("pass");
        assertThat(retrieved.host()).isEqualTo("host.com");
    }

    @Test
    void shouldOverwriteExistingCredentials() {
        CredentialManager.store("repo", new CredentialManager.CredentialEntry("old", "oldpass", null, "old.com"));
        CredentialManager.store("repo", new CredentialManager.CredentialEntry("new", "newpass", "token", "new.com"));

        CredentialManager.CredentialEntry entry = CredentialManager.get("repo");
        assertThat(entry.username()).isEqualTo("new");
        assertThat(entry.token()).isEqualTo("token");
    }

    @Test
    void shouldHandleTokenOnlyCredentials() {
        CredentialManager.CredentialEntry entry = new CredentialManager.CredentialEntry(null, null, "tok123", null);
        CredentialManager.store("token-repo", entry);

        CredentialManager.CredentialEntry retrieved = CredentialManager.get("token-repo");
        assertThat(retrieved.token()).isEqualTo("tok123");
        assertThat(retrieved.username()).isNull();
    }

    @Test
    void shouldLoadFromEnvironment() {
        CredentialManager.CredentialEntry entry = CredentialManager.fromEnvironment("NONEXISTENT_PREFIX");
        assertThat(entry).isNull();
    }
}
