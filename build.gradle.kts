import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.spotless.LineEnding

plugins {
  java
  `java-library`
  `maven-publish`
  signing
  alias(libs.plugins.spotless)
  alias(libs.plugins.nexus)
}

val signRequired = !rootProject.property("dev").toString().toBoolean()
val spotlessApply = rootProject.property("spotless.apply").toString().toBoolean()

repositories {
  mavenCentral()
}

if (spotlessApply) {
  configure<SpotlessExtension> {
    lineEndings = LineEnding.UNIX

    format("encoding") {
      target("*.*")
      encoding("UTF-8")
    }

    java {
      target("**/src/**/java/**/*.java")
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
}

allprojects {
  group = "tr.com.infumia"

  extra["qualifiedProjectName"] = if (parent == null) {
    "Event"
  } else {
    val parentName = parent!!.extra["qualifiedProjectName"].toString()
    var current = name[0].toUpperCase() + name.substring(1)
    var index: Int? = 0
    while (index != null) {
      index = current.indexOf('-')
      if (index == -1) {
        break
      }
      current = current.substring(0, index) + current[index + 1].toUpperCase() + current.substring(index + 2)
    }
    parentName + current
  }
}

subprojects {
  apply<JavaPlugin>()
  apply<JavaLibraryPlugin>()
  apply<MavenPublishPlugin>()
  apply<SigningPlugin>()

  val qualifiedProjectName = project.extra["qualifiedProjectName"].toString()

  java {
    toolchain {
      languageVersion.set(JavaLanguageVersion.of(17))
    }
  }

  tasks {
    compileJava {
      options.encoding = Charsets.UTF_8.name()
    }

    jar {
      archiveClassifier.set(null as String?)
      archiveBaseName.set(qualifiedProjectName)
      archiveVersion.set(project.version.toString())
    }

    javadoc {
      options.encoding = Charsets.UTF_8.name()
      (options as StandardJavadocDocletOptions).tags("todo")
    }

    val javadocJar by creating(Jar::class) {
      dependsOn("javadoc")
      archiveClassifier.set("javadoc")
      archiveBaseName.set(qualifiedProjectName)
      archiveVersion.set(project.version.toString())
      from(javadoc)
    }

    val sourcesJar by creating(Jar::class) {
      dependsOn("classes")
      archiveClassifier.set("sources")
      archiveBaseName.set(qualifiedProjectName)
      archiveVersion.set(project.version.toString())
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
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    mavenLocal()
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

  publishing {
    publications {
      val publication = create<MavenPublication>("mavenJava") {
        groupId = project.group.toString()
        artifactId = qualifiedProjectName
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
