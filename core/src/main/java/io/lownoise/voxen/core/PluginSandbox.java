package io.lownoise.voxen.core;

import io.lownoise.voxen.plugins.api.VoxenException;

import java.io.FilePermission;
import java.nio.file.Path;
import java.security.*;
import java.util.PropertyPermission;

@SuppressWarnings("removal")

public class PluginSandbox {

    private static final String SANDBOX_POLICY = "voxen-sandbox.policy";
    private boolean enabled;

    public PluginSandbox() {
        this.enabled = false;
    }

    public PluginSandbox(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void applyIfEnabled(String pluginName, Path pluginSource) {
        if (!enabled) return;
        apply(pluginName, pluginSource);
    }

    public void apply(String pluginName, Path pluginSource) {
        Policy.setPolicy(new PluginPolicy(pluginName, pluginSource));
        System.setSecurityManager(new SecurityManager());
    }

    public void disable() {
        System.setSecurityManager(null);
    }

    private static class PluginPolicy extends Policy {

        private final String pluginName;
        private final Path pluginSource;
        private final Policy defaultPolicy;

        PluginPolicy(String pluginName, Path pluginSource) {
            this.pluginName = pluginName;
            this.pluginSource = pluginSource;
            this.defaultPolicy = Policy.getPolicy();
        }

        @Override
        public boolean implies(ProtectionDomain domain, Permission permission) {
            if (isPluginCode(domain)) {
                return pluginImplies(permission);
            }
            return defaultPolicy.implies(domain, permission);
        }

        private boolean isPluginCode(ProtectionDomain domain) {
            if (domain == null || domain.getCodeSource() == null) return false;
            var location = domain.getCodeSource().getLocation();
            return location != null && location.toString().contains(pluginName);
        }

        private boolean pluginImplies(Permission permission) {
            if (permission instanceof PropertyPermission pp && pp.getActions().equals("read")) {
                return true;
            }
            if (permission instanceof FilePermission fp) {
                String path = fp.getName();
                String sourcePath = pluginSource.toAbsolutePath().toString();
                if (path.startsWith(sourcePath) || path.startsWith(System.getProperty("java.io.tmpdir", "/tmp"))) {
                    return true;
                }
                if (path.equals("-") || path.equals("<<ALL FILES>>")) {
                    return false;
                }
            }
            if (permission instanceof RuntimePermission rp) {
                return switch (rp.getName()) {
                    case "getClassLoader", "accessClassInPackage.*", "getProtectionDomain",
                         "accessDeclaredMembers", "getStackTrace" -> true;
                    default -> false;
                };
            }
            if (permission instanceof java.net.NetPermission) {
                return false;
            }
            return !(permission instanceof AllPermission);
        }
    }
}
