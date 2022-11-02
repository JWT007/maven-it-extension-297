# maven-it-extension-297

Test Project for: 

## Ticket #297

https://github.com/khmarbaise/maven-it-extension/issues/297

### Description

#### Is your feature request related to a problem? Please describe.

The example below is maybe an edge case, but I still think there is potential in the requested feature. :)

We are using the maven-git-versioning-extension in our Git project at the root to set the POM version based on branch name.
(adding extension in ".mvn/extensions.xml" and associated ".mvn/maven-git-versioning-extension.xml".)

Unfortunately, this is also picked up within the integration tests by default.

So for example, if we want to test that the version of a modified POM is "1.2.3-SNAPSHOT" it is changed to "1.2.3-hotfix-SNAPSHOT".

I have workarounds now by defining the following on every single test-method.
```
  @MavenGoal("install")
  @MavenTest
  @SystemProperty(value = "versioning.disable", content="true") // disable maven-git-versioning-extension for IT tests
  public void test_001(final MavenExecutionResult result) { ... }
```
I tried defining this `@SystemProperty` annotation on the test class (and the `@Nested` classes); however, it does not propagate to the test methods.

#### Describe the solution you'd like

What would be great is to just be able to define the `@SystemProperty` at class-level and have it propagate to all methods (and methods in `@Nested` subclasses).

I am assuming that the same argument might be made for the `@MavenOption` annotation but that is not where I am having the problem. :)

#### Additional context
Sample error:
```
java.lang.AssertionError: [GAVP] 
Expecting:
  <["me.jwt007.maven.plugins.foobar.its",
    "foobar-bom-001",
    "1.0.1-hotfix-SNAPSHOT",
    "pom"]>
to contain exactly (and in same order):
  <["me.jwt007.maven.plugins.foobar.its", "foobar-bom-001", "1.2.3", "pom"]>
but some elements were not found:
  <["1.2.3"]>
and others were not expected:
  <["1.0.1-hotfix-SNAPSHOT"]>
```

## Integration Tests

**Requirements**
* JDK 11+
* Maven 3.8.1+

In the git project there is a top-level directory `.mvn` which contains configuration
to load the `maven-git-versioning-extension` and _always_ force
the version to a specific constant version:

````xml
<configuration ...>

  <!-- optional fallback configuration in case of no matching ref configuration-->
  <rev>
    <version>1.0.0-FOOBAR-SNAPSHOT</version>
  </rev>

</configuration>
````

When active in the build output you will see something like this:

````text
[18:24:10.294+0100] [INFO] --- me.qoomon:maven-git-versioning-extension:9.3.1 [core extension] ----
[18:24:12.678+0100] [INFO] matching ref: COMMIT - b88599834bd41770e2b08203e2a6b191542cc6b1
[18:24:12.679+0100] [INFO] ref configuration: COMMIT - pattern: null
[18:24:12.679+0100] [INFO]   version: 1.0.0-FOOBAR-SNAPSHOT
[18:24:12.694+0100] [INFO] 
[18:24:12.695+0100] [INFO] me.jwt.007.maven.plugins.ite:maven-ite-297
[18:24:12.698+0100] [INFO] set version to 1.0.0-FOOBAR-SNAPSHOT
[18:24:12.747+0100] [INFO] 
````

There are three _nearly_ identical integration tests that only differ on 
the placement of the `@SystemProperty`to disable the `maven-git-versioning-extension`.

Each performs a simple test on whether the `.git-versioned-pom.xml` 
was generated.  

If the system-property is passed successfully, the `maven-git-versioning-extension`
will be disabled and the file will not be generated.

````java
public void test_001(MavenExecutionResult result) {
  
  assertThat(result)
    .isSuccessful()
    .satisfies(r -> {
      assertThat(new File(r.getMavenProjectResult().getTargetProjectDirectory(), ".git-versioned-pom.xml"))
      .as("Versioned POM File")
      .doesNotExist();
     });
  
  }
````

Run the tests with:

`mvn clean verify`

### HelloMojo1IT

In this test, the `@SystemProperty` is on the top-level class.

````java
@DisplayName("HelloMojo:")
@MavenJupiterExtension
@MavenRepository
@SystemProperty(value = "versioning.disable", content="true") // disable maven-git-versioning-extension for IT tests
public class HelloMojo1IT {

  @Nested
  @DisplayName("Nested:")
  class NestedClass {

    @DisplayName("... test version")
    @MavenGoal("compile")
    @MavenTest
    public void test_001(MavenExecutionResult result) {...}
````

#### Test Results

Fails:
````text
[17:33:06.167+0100] [ERROR] me.jwt007.maven.plugins.ite.issue297.its.HelloMojo1IT$NestedClass.test_001(MavenExecutionResult)  Time elapsed: 40.656 s  <<< FAILURE!
java.lang.AssertionError: 
[Versioned POM File] 
Expecting file:
  <C:\JWT\PRJ\PWC\REPO\maven-it-extension-297\target\maven-it\me\jwt007\maven\plugins\ite\issue297\its\HelloMojo1IT\NestedClass\test_001\project\.git-versioned-pom.xml>
not to exist
	at me.jwt007.maven.plugins.ite.issue297.its.HelloMojo1IT$NestedClass.lambda$test_001$0(HelloMojo1IT.java:37)
	at me.jwt007.maven.plugins.ite.issue297.its.HelloMojo1IT$NestedClass.test_001(HelloMojo1IT.java:34)
````

**mvn-arguments.log**
````text
C:\JWT\SOFTWARE\apache-maven-3.8.6\bin\mvn.cmd
-Dmaven.repo.local=C:\JWT\PRJ\PWC\REPO\maven-it-extension-297\target\maven-it\me\jwt007\maven\plugins\ite\issue297\its\HelloMojo1IT\.m2\repository
--batch-mode
-V
--errors

````

### HelloMojo2IT

In this test, the `@SystemProperty` is on the nested class.

````java
@DisplayName("HelloMojo:")
@MavenJupiterExtension
@MavenRepository
public class HelloMojo2IT {

  @Nested
  @DisplayName("Nested:")
  @SystemProperty(value = "versioning.disable", content="true") // disable maven-git-versioning-extension for IT tests
  class NestedClass {

    @DisplayName("... test version")
    @MavenGoal("compile")
    @MavenTest
    public void test_001(MavenExecutionResult result) {...}
````

#### Test Results

Succeeds.

**mvn-arguments.log**
````text
C:\JWT\SOFTWARE\apache-maven-3.8.6\bin\mvn.cmd
-Dmaven.repo.local=C:\JWT\PRJ\PWC\REPO\maven-it-extension-297\target\maven-it\me\jwt007\maven\plugins\ite\issue297\its\HelloMojo2IT\.m2\repository
-Dversioning.disable=true
--batch-mode
-V
--errors
compile
````

***HelloMojo3IT***

In this test, the `@SystemProperty` is on the nested test-method.

````java
@DisplayName("HelloMojo:")
@MavenJupiterExtension
@MavenRepository
public class HelloMojo3IT {

  @Nested
  @DisplayName("Nested:")
  class NestedClass {

    @DisplayName("... test version")
    @MavenGoal("compile")
    @MavenTest
    @SystemProperty(value = "versioning.disable", content="true") // disable maven-git-versioning-extension for IT tests
    public void test_001(MavenExecutionResult result) {...}
````

#### Test Results

Succeeds.

**mvn-arguments.log**
````text
C:\JWT\SOFTWARE\apache-maven-3.8.6\bin\mvn.cmd
-Dmaven.repo.local=C:\JWT\PRJ\PWC\REPO\maven-it-extension-297\target\maven-it\me\jwt007\maven\plugins\ite\issue297\its\HelloMojo3IT\.m2\repository
-Dversioning.disable=true
--batch-mode
-V
--errors
compile
````
