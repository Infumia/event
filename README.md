# event
[![idea](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![Update Snapshot](https://github.com/Infumia/event/actions/workflows/snapshot.yml/badge.svg)](https://github.com/Infumia/event/actions/workflows/snapshot.yml)
![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/tr.com.infumia/EventCommon?label=maven-central&server=https%3A%2F%2Foss.sonatype.org%2F)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/tr.com.infumia/EventCommon?label=maven-central&server=https%3A%2F%2Foss.sonatype.org)
## How to Use (Developers)
### Initiate the Library
```java
final class Plugin {

  void onLoad() {
    // Paper
    final var paperEventManager = new PaperEventManager();
    Plugins.init(this, paperEventManager);
    // Velocity
    final var velocityEventManager = new VelocityEventManager(this.proxyServer);
    Plugins.init(this, velocityEventManager);
  }
}
```
### Maven
```xml
<dependencies>
  <!-- Do NOT forget to relocate -->
  <dependency>
    <groupId>tr.com.infumia</groupId>
    <artifactId>terminable</artifactId>
    <version>VERSION</version>
    <scope>provided</scope>
  </dependency>
  <dependency>
    <groupId>tr.com.infumia</groupId>
    <artifactId>EventPaper</artifactId>
    <version>VERSION</version>
    <scope>provided</scope>
  </dependency>
  <dependency>
    <groupId>tr.com.infumia</groupId>
    <artifactId>EventVelocity</artifactId>
    <version>VERSION</version>
    <scope>provided</scope>
  </dependency>
</dependencies>
```
### Gradle
```groovy
plugins {
  id "java"
}

dependencies {
  // Do NOT forget to relocate.
  implementation "tr.com.infumia:terminable:VERSION"
  implementation "tr.com.infumia:EventPaper:VERSION"
  implementation "tr.com.infumia:EventVelocity:VERSION"
}
```
