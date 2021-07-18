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
BatchRunner is a Junit plugin that is composed on top of Mockito and Coroutines to bring parallelism to the UnitTests.
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
    // Build based on Java_1.8
    testImplementation("com.github.iamjosephmj:BatchRunner:1.0.2")
    
    // Build based on Java11
    testImplementation("com.github.iamjosephmj:BatchRunner:1.0.1")
}
```

## Integration

### For JUnit Test Classes

```kotlin

@RunWith(BatchRunner::class)
class BatchRunnerUnitTest1 {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
    // along with you tests
}

```

### For Mockito JUnit Test Classes

```kotlin

@RunWith(MockitoJUnitBatchRunner::class)
class MockitoBatchTest {

    @Mock
    private lateinit var context: Context

    @Before
    fun before() {
        `when`(context.isRestricted).thenReturn(true)
    }

    @Test
    fun mockTest1() {
        assert(context.isRestricted)
    }
}

```


### For TestSuites

```kotlin

@RunWith(BatchSuite::class)
@BatchSuite.SuiteClasses(
    BatchRunnerUnitTest1::class,
    BatchRunnerUnitTest2::class
)
class BatchSuiteRunner

```

### Gradle changes

```kotlin

android {
    subprojects {
        tasks.withType<Test> {
            maxParallelForks = Runtime.getRuntime().availableProcessors()
        }
    }
}

```

# Contribution, Issues or Future Ideas

If part of BatchRunner is not working correctly be sure to file a Github issue. In the issue provide as
many details as possible. This could include example code or the exact steps that you did so that
everyone can reproduce the issue. Sample projects are always the best way :). This makes it easy for
me or someone from the open-source community to start working!

If you have a feature idea submit an issue with a feature request or submit a pull request and we
will work with you to merge it in!

## Contribution guidelines

Contributions are more than welcome!
- You should make sure that all the test are working properly.
- You should raise a PR to `develop` branch
- Before you raise a PR please make sure your code had no issue from Android studio lint analyzer.

## Please Share & Star the repository to keep me motivated.
  <a href = "https://github.com/iamjosephmj/BatchRunner/stargazers">
     <img src = "https://img.shields.io/github/stars/iamjosephmj/BatchRunner" />
  </a>
  <a href = "https://twitter.com/iamjosephmj">
     <img src = "https://img.shields.io/twitter/url?label=follow&style=social&url=https%3A%2F%2Ftwitter.com" />
  </a>


