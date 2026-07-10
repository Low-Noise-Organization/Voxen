package io.lownoise.voxen.docker;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ImageLinterTest {

    private final ImageLinter linter = new ImageLinter();

    @Test
    void lintResultRecord() {
        var result = new ImageLinter.LintResult(
            java.util.List.of("err1"), java.util.List.of("warn1"), false);
        assertThat(result.errors()).containsExactly("err1");
        assertThat(result.warnings()).containsExactly("warn1");
        assertThat(result.passed()).isFalse();
    }

    @Test
    void lintResultPassesWhenNoErrors() {
        var result = new ImageLinter.LintResult(
            java.util.List.of(), java.util.List.of(), true);
        assertThat(result.passed()).isTrue();
    }

    @Test
    void lintHandlesMissingImageGracefully() {
        var result = linter.lint("nonexistent-image-for-testing");
        assertThat(result.errors()).isNotEmpty();
        assertThat(result.passed()).isFalse();
    }

    @Test
    void lintReturnsResultWithNoErrorsForValidTag() {
        var result = linter.lint("alpine:latest");
        assertThat(result).isNotNull();
    }
}
