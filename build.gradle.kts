buildscript {
    repositories {
        google() // Make sure this is included
        mavenCentral() // Make sure this is included
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.2") // Use the latest stable version
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
