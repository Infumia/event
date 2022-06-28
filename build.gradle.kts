import com.diffplug.gradle.spotless.SpotlessPlugin
import com.diffplug.spotless.LineEnding

plugins {
  java
  `java-library`
  `maven-publish`
  signing
  id("com.diffplug.spotless") version "6.7.2"
  id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

val signRequired = !rootProject.property("dev").toString().toBoolean()

allprojects {
  group = "tr.com.infumia"
}

subprojects {
  apply<JavaPlugin>()
  apply<JavaLibraryPlugin>()
  apply<MavenPublishPlugin>()
  apply<SigningPlugin>()
  apply<SpotlessPlugin>()

  java {
    toolchain {
      languageVersion.set(JavaLanguageVersion.of(17))
    }
  }

  tasks {
    compileJava {
      options.encoding = Charsets.UTF_8.name()
    }

    javadoc {
      options.encoding = Charsets.UTF_8.name()
      (options as StandardJavadocDocletOptions).tags("todo")
    }

    val javadocJar by creating(Jar::class) {
      dependsOn("javadoc")
      archiveClassifier.set("javadoc")
      from(javadoc)
    }

    val sourcesJar by creating(Jar::class) {
      dependsOn("classes")
      archiveClassifier.set("sources")
      from(sourceSets["main"].allSource)
    }

    build {
      dependsOn(spotlessApply)
      dependsOn(jar)
      dependsOn(sourcesJar)
      dependsOn(javadocJar)
    }
  }

  repositories {
    mavenCentral()
  }

  dependencies {
    compileOnly(rootProject.libs.terminable)
    compileOnly(rootProject.libs.lombok)
    compileOnly(rootProject.libs.annotations)

    annotationProcessor(rootProject.libs.lombok)
    annotationProcessor(rootProject.libs.annotations)

    testAnnotationProcessor(rootProject.libs.lombok)
    testAnnotationProcessor(rootProject.libs.annotations)
  }

  spotless {
    lineEndings = LineEnding.UNIX
    isEnforceCheck = false

    java {
      importOrder()
      removeUnusedImports()
      endWithNewline()
      indentWithSpaces(2)
      trimTrailingWhitespace()
      prettier(
        mapOf(
          "prettier" to "2.7.1",
          "prettier-plugin-java" to "1.6.2"
        )
      ).config(
        mapOf(
          "parser" to "java",
          "tabWidth" to 2,
          "useTabs" to false
        )
      )
    }
  }

  publishing {
    publications {
      val publication = create<MavenPublication>("mavenJava") {
        groupId = project.group.toString()
        artifactId = "event"
        version = project.version.toString()

        from(components["java"])
        artifact(tasks["sourcesJar"])
        artifact(tasks["javadocJar"])
        pom {
          name.set("Event")
          description.set("A builder-like event library for Paper/Velocity.")
          url.set("https://infumia.com.tr/")
          licenses {
            license {
              name.set("MIT License")
              url.set("https://mit-license.org/license.txt")
            }
          }
          developers {
            developer {
              id.set("portlek")
              name.set("Hasan Demirtaş")
              email.set("utsukushihito@outlook.com")
            }
          }
          scm {
            connection.set("scm:git:git://github.com/infumia/terminable.git")
            developerConnection.set("scm:git:ssh://github.com/infumia/terminable.git")
            url.set("https://github.com/infumia/terminable")
          }
        }
      }

      signing {
        isRequired = signRequired
        if (isRequired) {
          useGpgCmd()
          sign(publication)
        }
      }
    }
  }
}

nexusPublishing {
  repositories {
    sonatype()
  }
}
