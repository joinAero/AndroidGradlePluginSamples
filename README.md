# Android Gradle Plugin Samples

[![Download](https://api.bintray.com/packages/eevee/maven/android-archive-plugin/images/download.svg)](https://bintray.com/eevee/maven/android-archive-plugin/_latestVersion)

## Android Archive Plugin

Gradle plugin that archive the Android sources and javadoc.

```gradle
buildscript {
  repositories {
    jcenter()
  }
  dependencies {
    classpath 'cc.eevee.gradle:android-archive-plugin:1.0.0'
  }
}

apply plugin: 'android-archive'
```

It will provide these Gradle tasks: `androidSourcesJar` `androidJavadoc` `androidJavadocJar`.

## Articles

*zh-Hans*

* [如何实现 Android 项目的 Gradle 插件](/docs/android_gradle.md)

## License

The project is [Apache License, Version 2.0](/LICENSE) licensed.
