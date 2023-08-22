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
    // Bukkit
    final var bukkitEventManager = new BukkitEventManager();
    Plugins.init(this, bukkitEventManager);
    // Velocity
    final var velocityEventManager = new VelocityEventManager(this.proxyServer);
    Plugins.init(this, velocityEventManager);
    // Shiru ka
    final var shirukaEventManager = new ShirukaEventManager();
    Plugins.init(shirukaEventManager);
    // Protocol
      final var bukkitEventManager = new BukkitEventManager();
    Plugins.init(this, bukkitEventManager);
    Protocol.subscribe(ListenerPriority.NORMAL, PacketType.Play.Server.EXPLOSION)
      .filter(event -> true)
      .handler(event -> {

      })
      .bindWith(consumer);
  }
}
```
### Maven
```xml
<dependencies>
  <!-- Do NOT forget to relocate -->
  <dependency>
    <groupId>tr.com.infumia</groupId>
    <artifactId>event-common</artifactId>
    <version>VERSION</version>
  </dependency>
  <dependency>
    <groupId>tr.com.infumia</groupId>
    <artifactId>event-bukkit</artifactId>
    <version>VERSION</version>
  </dependency>
  <dependency>
    <groupId>tr.com.infumia</groupId>
    <artifactId>event-protocol-lib</artifactId>
    <version>VERSION</version>
  </dependency>
  <dependency>
    <groupId>tr.com.infumia</groupId>
    <artifactId>event-velocity</artifactId>
    <version>VERSION</version>
  </dependency>
  <dependency>
    <groupId>tr.com.infumia</groupId>
    <artifactId>event-shiruka</artifactId>
    <version>VERSION</version>
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
  implementation "tr.com.infumia:event-common:VERSION"
  implementation "tr.com.infumia:event-bukkit:VERSION"
  implementation "tr.com.infumia:event-protocol-lib:VERSION"
  implementation "tr.com.infumia:event-velocity:VERSION"
  implementation "tr.com.infumia:event-shiruka:VERSION"
}
```
