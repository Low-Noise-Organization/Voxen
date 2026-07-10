plugins {
    id("java-library")
    id("java-test-fixtures")
    id("jacoco")
    id("pmd")
}

allprojects {
    group = "io.lownoise.voxen"
    version = "0.1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "jacoco")
    apply(plugin = "pmd")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
        withSourcesJar()
        withJavadocJar()
    }

    jacoco {
        toolVersion = "0.8.12"
    }

    tasks.jacocoTestReport {
        dependsOn(tasks.test)
        reports {
            xml.required = true
            html.required = true
        }
    }

    tasks.jacocoTestCoverageVerification {
        dependsOn(tasks.jacocoTestReport)
        violationRules {
            rule {
                limit {
                    minimum = "0.30".toBigDecimal()
                }
            }
        }
    }

    pmd {
        toolVersion = "7.11.0"
        isIgnoreFailures = true
        rulesMinimumPriority = 5
        ruleSets = listOf()
        ruleSetConfig = resources.text.fromString(
            """
            <?xml version="1.0"?>
            <ruleset name="voxen-pmd"
                xmlns="http://pmd.sourceforge.net/ruleset/2.0.0"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:schemaLocation="http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd">
                <description>Voxen PMD rules</description>
                <rule ref="category/java/bestpractices.xml"/>
                <rule ref="category/java/errorprone.xml">
                    <exclude name="NullAssignment"/>
                </rule>
                <rule ref="category/java/codestyle.xml">
                    <exclude name="LongVariable"/>
                    <exclude name="ShortVariable"/>
                    <exclude name="ShortClassName"/>
                    <exclude name="OnlyOneReturn"/>
                    <exclude name="AtLeastOneConstructor"/>
                    <exclude name="LocalVariableCouldBeFinal"/>
                    <exclude name="MethodArgumentCouldBeFinal"/>
                    <exclude name="CommentDefaultAccessModifier"/>
                    <exclude name="UselessParentheses"/>
                    <exclude name="UnnecessaryImport"/>
                </rule>
                <rule ref="category/java/design.xml">
                    <exclude name="LawOfDemeter"/>
                    <exclude name="UseUtilityClass"/>
                    <exclude name="DataClass"/>
                    <exclude name="AvoidCatchingGenericException"/>
                    <exclude name="LoosePackageCoupling"/>
                    <exclude name="AvoidThrowingRawExceptionTypes"/>
                </rule>
            </ruleset>
            """.trimIndent()
        )
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        finalizedBy(tasks.jacocoTestReport)
    }

    tasks.check {
        dependsOn(tasks.jacocoTestCoverageVerification)
    }

    dependencies {
        testImplementation(platform("org.junit:junit-bom:5.11.0"))
        testImplementation("org.junit.jupiter:junit-jupiter")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
        testImplementation("org.assertj:assertj-core:3.26.3")
    }
}

tasks.register<JavaExec>("generateCompletions") {
    description = "Generate shell completion scripts for bash and zsh"
    group = "build"
    classpath = sourceSets.main.get().runtimeClasspath + project(":cli").sourceSets.main.get().runtimeClasspath
    mainClass = "io.lownoise.voxen.cli.CompletionGenerator"
    args = listOf("${layout.buildDirectory.get()}/completions")
    dependsOn(":cli:classes")
}

tasks.register("qualityCheck") {
    description = "Run all quality checks: tests, coverage, PMD"
    group = "verification"
    dependsOn(subprojects.map { "${it.path}:check" })
}
