dependencies {
  implementation(project(":event-common"))

  compileOnly(libs.shiruka)
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}
