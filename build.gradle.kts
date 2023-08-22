plugins {
  java
  `java-library`
  `maven-publish`
  signing
  alias(libs.plugins.spotless)
  alias(libs.plugins.nexus)
}

val signRequired = !rootProject.property("dev").toString().toBoolean()

allprojects { group = "tr.com.infumia" }

subprojects {
  apply<JavaPlugin>()
  apply<JavaLibraryPlugin>()
  apply<MavenPublishPlugin>()
  apply<SigningPlugin>()

  java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  tasks {
    compileJava {
      options.encoding = Charsets.UTF_8.name()
    }

    jar {
      archiveClassifier.set(null as String?)
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
      dependsOn(jar)
      dependsOn(sourcesJar)
      dependsOn(javadocJar)
    }
  }

  repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    mavenLocal()
  }

  dependencies {
    compileOnly(rootProject.libs.lombok)
    compileOnly(rootProject.libs.annotations)

    annotationProcessor(rootProject.libs.lombok)

    testAnnotationProcessor(rootProject.libs.lombok)
  }

  publishing {
    publications {
      val publication = create<MavenPublication>("mavenJava") {
        groupId = project.group.toString()
        artifactId = project.name
        version = project.version.toString()

        from(components["java"])
        artifact(tasks["sourcesJar"])
        artifact(tasks["javadocJar"])
        pom {
          name.set("Event")
          description.set("A builder-like event library for Bukkit/ProtocolLib/Velocity/Shiruka.")
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
            connection.set("scm:git:git://github.com/infumia/event.git")
            developerConnection.set("scm:git:ssh://github.com/infumia/event.git")
            url.set("https://github.com/infumia/event")
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

nexusPublishing.repositories.sonatype()

repositories.mavenCentral()

spotless {
  lineEndings = com.diffplug.spotless.LineEnding.UNIX

  val prettierConfig =
    mapOf(
      "prettier" to "2.8.8",
      "prettier-plugin-java" to "2.3.0",
    )

  format("encoding") {
    target("*.*")
    encoding("UTF-8")
    endWithNewline()
    trimTrailingWhitespace()
  }

  kotlinGradle {
    target("**/*.gradle.kts")
    indentWithSpaces(2)
    endWithNewline()
    trimTrailingWhitespace()
    ktlint()
  }

  yaml {
    target(
      ".github/**/*.yml",
      ".github/**/*.yaml",
    )
    endWithNewline()
    trimTrailingWhitespace()
    val jackson = jackson()
    jackson.yamlFeature("LITERAL_BLOCK_STYLE", true)
    jackson.yamlFeature("SPLIT_LINES", false)
  }

  java {
    target("**/src/**/java/**/*.java")
    importOrder()
    removeUnusedImports()
    indentWithSpaces(2)
    endWithNewline()
    trimTrailingWhitespace()
    prettier(prettierConfig)
      .config(
        mapOf("parser" to "java", "tabWidth" to 2, "useTabs" to false, "printWidth" to 100),
      )
  }
}
