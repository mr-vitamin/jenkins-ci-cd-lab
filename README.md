# Jenkins CI/CD Lab

A beginner-friendly Jenkins CI/CD demonstration for RapidAscent. The project
uses only Java's standard library: no Maven, Gradle, test framework, external
services, credentials, or deployment infrastructure are needed.

## Project structure

```text
.
├── Jenkinsfile
├── README.md
└── src
    ├── main/java/com/rapidascent/HelloWorld.java
    └── test/java/com/rapidascent/HelloWorldTest.java
```

The test is a small executable Java class that fails by throwing an
`AssertionError` when the expected result is not returned.

## Run locally

Install Git and JDK 21, then from the repository root run:

```sh
mkdir -p build/classes build/test-classes
javac --release 21 -d build/classes $(find src/main/java -name '*.java')
javac --release 21 -cp build/classes -d build/test-classes \
  $(find src/test/java -name '*.java')
java -cp build/classes:build/test-classes com.rapidascent.HelloWorldTest
jar --create --file build/jenkins-ci-cd-demo.jar \
  --main-class com.rapidascent.HelloWorld -C build/classes .
java -jar build/jenkins-ci-cd-demo.jar
```

The local build output is written to `build/`.

## Jenkins configuration

Create a Jenkins Pipeline job configured as follows:

1. Select **Pipeline script from SCM** as the Definition.
2. Select **Git** as the SCM.
3. Enter this repository's clone URL. For a public repository, no credentials
   are needed.
4. Set **Branch Specifier** to `*/main`.
5. Set **Script Path** to `Jenkinsfile`.
6. Save the job and select **Build Now**.

The Jenkins environment must have the Docker Pipeline plugin and access to a
Docker daemon. The pipeline runs in the Linux `eclipse-temurin:21-jdk` image;
the application itself has no dependencies beyond JDK 21.

## Pipeline stages

The root `Jenkinsfile` defines these stages and prints a message for each:

1. **Checkout** — explicitly checks out the source from the job's SCM.
2. **Build** — compiles the application and creates a runnable JAR.
3. **Test** — compiles and runs the standard-library unit test; a failed check
   exits with an error and fails the pipeline.
4. **Deploy** — simulates deployment by running the JAR, then archives the
   compiled artifact in Jenkins.

The pipeline also prints a success or failure notification in its `post`
section. Deployment is only simulated; nothing is sent to external
infrastructure.
