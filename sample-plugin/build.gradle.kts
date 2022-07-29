import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  `java-gradle-plugin`
  id("org.jetbrains.kotlin.jvm")
  id("com.vanniktech.maven.publish")
  id("org.jlleitschuh.gradle.ktlint")
}

gradlePlugin {
  plugins {
    register("samplePlugin") {
      id = "io.github.jackchen365.sample"
      implementationClass = "com.github.jackchen.plugin.sample.SamplePlugin"
    }
  }
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    jvmTarget = "11"
  }
}

configurations.compileOnly.configure { isCanBeResolved = true }
configurations.api.configure { isCanBeResolved = true }

tasks.withType<PluginUnderTestMetadata>().configureEach {
  pluginClasspath.from(configurations.compileOnly)
}

// Test tasks loads plugin from local maven repository
tasks.named("test").configure {
  dependsOn("publishToMavenLocal", project(":api").tasks.named("publishToMavenLocal"))
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()

  systemProperty("io.github.jackchen365.sample.version", version.toString())
}

/**
 * A special configuration that add internal project into this plugin
 */
val internalLibs: Configuration by configurations.creating
configurations.compileOnly.configure { extendsFrom(internalLibs) }
tasks.withType<org.gradle.jvm.tasks.Jar>().configureEach {
  duplicatesStrategy = DuplicatesStrategy.INCLUDE
  internalLibs.files.forEach { file ->
    if (file.isDirectory) {
      from(file)
    } else {
      from(zipTree(file))
    }
  }
}

dependencies {
  compileOnly(gradleApi())
  compileOnly(libs.kotlin.gradle.plugin)
  compileOnly(libs.android.gradle.plugin)
  compileOnly(libs.java.asm)
  compileOnly(libs.java.asm.util)
  compileOnly(libs.jdom2)
  compileOnly(libs.gson)
  compileOnly(libs.commons.io)
  compileOnly(libs.kotlin.stdlib)

  internalLibs(projects.api)

  testImplementation(gradleTestKit())
  testImplementation(libs.junit.jupiter)
  testImplementation(libs.kotlin.reflect)
  testImplementation(libs.commons.io)
  testImplementation(libs.gradle.test.toolkit)
}

ktlint {
  debug.set(true)
  additionalEditorconfigFile.set(file("$rootDir/.editorconfig"))
}
