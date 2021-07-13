![BatchRunner](https://github.com/iamjosephmj/BatchRunner/blob/develop/media/batch.png)


# BatchRunner



<div align="center">
 
 <a href = "https://github.com/iamjosephmj/BatchRunner/actions/workflows/android.yml">
      <img src = "https://github.com/iamjosephmj/BatchRunner/actions/workflows/android.yml/badge.svg" />
 </a>
   
 <a href = "https://github.com/iamjosephmj/BatchRunner/network/">
    <img src = "https://img.shields.io/github/forks/iamjosephmj/BatchRunner" />
  </a>
 
  <a href = "https://github.com/iamjosephmj/BatchRunner">
    <img src = "https://img.shields.io/github/stars/iamjosephmj/BatchRunner" />
 </a>

 <a href = "https://github.com/iamjosephmj/BatchRunner/issues">
     <img src = "https://img.shields.io/github/issues/iamjosephmj/BatchRunner" />
 </a>  
 
 <a href = "https://github.com/iamjosephmj/BatchRunner/blob/master/LICENSE">
     <img src = "https://img.shields.io/github/license/iamjosephmj/BatchRunner" />
 </a> 

  <a href = "https://jitpack.io/#iamjosephmj/BatchRunner">
     <img src = "https://jitpack.io/v/iamjosephmj/BatchRunner.svg" />
  </a>
 
 <a href = "https://twitter.com/iamjosephmj">
     <img src = "https://img.shields.io/twitter/url?label=follow&style=social&url=https%3A%2F%2Ftwitter.com" />
  </a>

</div>


## What is BatchRunner?

<p>
BatchRunner is a Junit plugin that is composed on top of Coroutines to bring parallelism to the UnitTests.
With this plugin, you can leverage the maximum threads available in your machine (CI - Instance).
<br/>
BatchRunner also supports SuiteTests, in the sense that you will be able to run different test classes in the suite parallelly.
<br/>
</p>

## Gradle

Add the following to your project's root build.gradle file

```groovy
repositories {
    maven {
        url "https://jitpack.io"
    }
}
```

Add the following to your project's build.gradle file

```kotlin
dependencies {
    implementation("com.github.iamjosephmj:BatchRunner:1.0.0")
}
```
