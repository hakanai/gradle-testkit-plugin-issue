package acme

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.prop
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.BuildTask
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class TestCommonPlugin {
    @TempDir
    lateinit var temp: File

    @Test
    fun test() {
        write("settings.gradle.kts", "rootProject.name = \"test-project\"")
        write(
            "build.gradle.kts",
            "plugins {",
            "    `lifecycle-base`",
            "    id(\"acme.common\")",
            "}",
            "",
            // The problem at hand: How do we remove the need to do this?
            // "var copyrightDate: String? by project.extra",
            "",
            "copyrightDate = \"2022\""
        )

        val buildResult = GradleRunner
            .create()
            .withPluginClasspath()
            .withProjectDir(temp)
            .withArguments("check")
            .build()

        val check = buildResult.task(":check")
        assertThat(check)
            .isNotNull()
            .prop("outcome") { t -> t.outcome }
            .isEqualTo(TaskOutcome.UP_TO_DATE)
    }

    private fun write(relativePath: String, vararg content: String) {
        temp.resolve(relativePath).writeText(content.joinToString("\n"))
    }
}
