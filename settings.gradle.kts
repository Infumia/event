rootProject.name = "event"

include0(
  mapOf(
    ":common" to "event-common",
    ":bukkit" to "event-bukkit",
    ":protocol-lib" to "event-protocol-lib",
    ":shiruka" to "event-shiruka",
    ":velocity" to "event-velocity",
  ),
)

fun include0(modules: Map<String, String?>) {
  modules.forEach { (module, projectName) ->
    include(module)
    if (projectName != null) {
      project(module).name = projectName
    }
  }
}
