dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
    jcenter() // Warning: this repository is going to shut down soon
    maven("https://jitpack.io")
  }
}

rootProject.name = "AndroidSampleLibrary"
include(":app", ":api", ":core", ":library", ":plugin")
