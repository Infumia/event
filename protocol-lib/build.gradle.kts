dependencies {
  implementation(project(":event-common"))
  implementation(project(":event-bukkit"))

  compileOnly(libs.protocollib)
  compileOnly(libs.bukkit)
}
