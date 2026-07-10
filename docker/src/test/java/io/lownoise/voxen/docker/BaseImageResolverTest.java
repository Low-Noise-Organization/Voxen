package io.lownoise.voxen.docker;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class BaseImageResolverTest {

    private final BaseImageResolver resolver = new BaseImageResolver();

    @Test
    void javaResolvesToDistroless() {
        var img = resolver.resolve("java", false);
        assertThat(img.image()).contains("distroless");
        assertThat(img.type()).isEqualTo("java");
    }

    @Test
    void javaNativeResolvesToDistrolessCc() {
        var img = resolver.resolve("java", true);
        assertThat(img.image()).contains("distroless/cc");
        assertThat(img.type()).isEqualTo("base");
    }

    @Test
    void kotlinUsesJavaImage() {
        var img = resolver.resolve("kotlin", false);
        assertThat(img.type()).isEqualTo("java");
    }

    @Test
    void nodeResolvesToDistroless() {
        var img = resolver.resolve("node", false);
        assertThat(img.image()).contains("distroless");
        assertThat(img.type()).isEqualTo("node");
    }

    @Test
    void pythonResolvesToSlim() {
        var img = resolver.resolve("python", false);
        assertThat(img.image()).isEqualTo("python");
        assertThat(img.tag()).isEqualTo("3.11-slim");
    }

    @Test
    void goResolvesToDistrolessStatic() {
        var img = resolver.resolve("go", false);
        assertThat(img.image()).contains("distroless/static");
    }

    @Test
    void rustResolvesToDistrolessCc() {
        var img = resolver.resolve("rust", false);
        assertThat(img.image()).contains("distroless/cc");
    }

    @Test
    void dotnetResolvesToAspnet() {
        var img = resolver.resolve("dotnet", false);
        assertThat(img.image()).contains("microsoft.com/dotnet/aspnet");
        assertThat(img.tag()).isEqualTo("8.0");
    }

    @Test
    void unknownRuntimeDefaultsToDebian() {
        var img = resolver.resolve("unknown", false);
        assertThat(img.image()).isEqualTo("debian");
    }

    @Test
    void fullNameCombinesImageAndTag() {
        var img = resolver.resolve("java", false);
        assertThat(img.fullName()).isEqualTo(img.image() + ":" + img.tag());
    }
}
