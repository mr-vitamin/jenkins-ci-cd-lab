# Jenkins CI/CD Lab

A beginner-friendly Jenkins lab that builds, tests, and packages a small Java
application. It uses Java's standard tools, so the project does not need Maven,
Gradle, or extra application dependencies. You will run Jenkins locally in
Docker Desktop; the pipeline itself runs on Jenkins' built-in executor.

## What you need

- A Windows computer with permission to install software.
- Docker Desktop for Windows.
- A GitHub account is useful for exploring the source, but this public
  repository can be checked out without credentials.
- An internet connection for downloading Docker Desktop, the Jenkins image,
  and this repository.

## 1. Install and verify Docker Desktop

1. Download Docker Desktop for Windows from
   [docker.com/products/docker-desktop](https://www.docker.com/products/docker-desktop/).
2. Run the installer. If it asks, use the recommended WSL 2 backend and allow
   the installer to enable the required Windows features.
3. Restart Windows if the installer requests it, then open Docker Desktop.
   Wait until it reports that Docker is running.
4. Open PowerShell or Windows Terminal and verify that the Docker command-line
   tools can reach the Docker engine:

   ```powershell
   docker --version
   docker info
   ```

   The first command prints a version. The second prints information about the
   running Docker engine. If it reports that it cannot connect, start Docker
   Desktop and wait for it to finish starting before continuing.

## 2. Start Jenkins

In PowerShell or Windows Terminal, run this command:

```powershell
docker run -d --name jenkins -p 127.0.0.1:8081:8080 -v jenkins_home:/var/jenkins_home jenkins/jenkins:lts-jdk21
```

Docker downloads the Jenkins image if it is not already on your computer, then
starts Jenkins in the background. The named `jenkins_home` volume stores
Jenkins configuration and job data separately from the container. The
`127.0.0.1` binding makes this Jenkins instance available only from your own
computer, and port `8081` avoids using Jenkins' usual web port on the Windows
host.

Wait a minute or two for the first startup, then open
[http://localhost:8081](http://localhost:8081) in your browser. If the page is
not ready, wait a little longer and refresh.

## 3. Complete the first-time setup wizard

1. On the **Unlock Jenkins** page, retrieve the one-time administrator
   password from the container:

   ```powershell
   docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
   ```

   Copy the password printed in the terminal into the browser. Do not share or
   publish it. If that command says the container is not running, see
   [Troubleshooting](#troubleshooting).
2. Select **Install suggested plugins**. Let the installation finish; it may
   take several minutes. This installs the common plugins needed to get
   started, including pipeline and source-control support.
3. On **Create First Admin User**, enter a username, password, name, and email
   address for your own local Jenkins account. Keep your password private.
4. On **Instance Configuration**, keep the suggested Jenkins URL unless you
   have a reason to change it. The web address for this lab is
   `http://localhost:8081`.
5. Select **Save and Finish**, then **Start using Jenkins**. Sign in with the
   administrator account you just created.

The initial administrator password is only for unlocking the setup wizard;
after setup, sign in with the account you created.

## 4. Create a Pipeline job for this repository

1. From the Jenkins dashboard, select **New Item**.
2. Enter a job name, such as `jenkins-ci-cd-lab`.
3. Select **Pipeline**, then select **OK**.
4. Find the **Pipeline** section on the job configuration page.
5. For **Definition**, select **Pipeline script from SCM**.
6. For **SCM**, select **Git**.
7. Enter this repository URL in **Repository URL**:

   ```text
   https://github.com/mr-vitamin/jenkins-ci-cd-lab.git
   ```

   Leave **Credentials** set to none for this public repository.
8. Set **Branch Specifier** to `*/main`.
9. Set **Script Path** to `Jenkinsfile`.
10. Select **Save**. On the job page, select **Build Now**.

Jenkins will fetch the repository and read the pipeline instructions from the
root-level `Jenkinsfile`. If your copy of the repository uses a different
default branch, use that branch name instead of `main`.

## 5. Understand the pipeline stages

The `Jenkinsfile` describes a simple CI/CD workflow. CI (continuous
integration) checks that changes can be built and tested; CD (continuous
delivery/deployment) prepares and, in a real project, delivers the result. This
lab's deployment step is only a simulation.

1. **Checkout** — retrieves the selected branch from Git so the build uses the
   latest source code.
2. **Build** — compiles the Java application with JDK 21 and packages the
   classes into `build/jenkins-ci-cd-demo.jar`.
3. **Test** — compiles and runs the project's small Java test program. A failed
   check exits with an error, causing the pipeline to fail rather than report a
   successful build.
4. **Deploy** — runs the JAR to demonstrate that it starts, then archives the
   JAR in Jenkins. This does **not** deploy to a server or any external
   infrastructure.

The pipeline uses `agent any`, so it runs on an available Jenkins executor.
With the setup in this guide, that is the Jenkins container's built-in
executor, which uses the JDK 21 in the selected Jenkins image. You do not need
Docker-in-Docker, a mounted Docker socket, a separate build agent, or a Docker
Pipeline plugin.

## 6. Check the build results

Open the job and select the build number to see its results:

- A successful run shows all four stages as successful and finishes with
  **SUCCESS**. If a stage fails, later stages do not run; open that stage or the
  build's **Console Output** to find the error.
- The console output includes messages such as `Checking out repository source
  code...`, `Compiling the Java application and packaging the artifact...`,
  `Compiling and running automated unit tests...`, and `Simulating deployment
  and archiving the compiled artifact...`.
- The test prints
  `PASS: greeting returns the expected message.` and running the JAR prints
  `Hello from the Jenkins CI/CD demo!`.
- On success, Jenkins prints
  `SUCCESS: Jenkins CI/CD demonstration completed successfully.` The
  `FAILURE` message instead points you to the earlier stage output.
- The job's build page has **Build Artifacts** with
  `build/jenkins-ci-cd-demo.jar`. Select it to download the runnable JAR.
  Jenkins also records a fingerprint for this archived artifact.

## Stop and restart Jenkins safely

To stop Jenkins without removing its data, run:

```powershell
docker stop jenkins
```

To start the same container again later, run:

```powershell
docker start jenkins
```

Wait for startup to finish, then reopen [http://localhost:8081](http://localhost:8081).
Your jobs and settings remain in the `jenkins_home` Docker volume. Do not
remove that volume if you want to keep Jenkins data.

## Troubleshooting

### Docker commands cannot connect

Make sure Docker Desktop is open and says Docker is running. Then retry
`docker info`. If Docker Desktop reports a WSL 2 or virtualization problem,
follow the instructions it displays and restart Docker Desktop.

### Port 8081 is already in use

The container may fail to start if another program is already using local port
8081. Check Docker's error output and close the program using that port. If you
choose a different available host port, change the left-hand port in the
`-p` option and use that port in the browser; leave the container port `8080`
unchanged.

### Jenkins page does not load or Jenkins is still starting

Check that the container is running:

```powershell
docker ps
```

For startup details, inspect its logs:

```powershell
docker logs jenkins
```

Wait for startup messages to settle, then refresh `http://localhost:8081`.
Confirm the container was started with the port mapping shown above.

### The initial password command fails

The container must be running. Check it with `docker ps`, then retry:

```powershell
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

If Jenkins has already completed its setup wizard, sign in using the
administrator account you created instead; the initial password is not your
ongoing login password.

### Jenkins cannot find Git, a plugin, or the Pipeline job type

Allow the suggested plugins to finish installing, then refresh Jenkins. The
Git plugin provides Git SCM support and the Pipeline plugin provides Pipeline
jobs. If a required plugin was not installed, open **Manage Jenkins** >
**Plugins**, install it, and restart Jenkins when prompted.

### SCM checkout fails

Check that **Repository URL** is
`https://github.com/mr-vitamin/jenkins-ci-cd-lab.git`, **Branch Specifier** is
`*/main`, and the repository is reachable. No credentials are needed for this
public repository. For a private repository, configure credentials securely
in Jenkins rather than putting a password or token in the repository URL.

### Build reports a missing Java command or Java version problem

Confirm that the job is using the official `jenkins/jenkins:lts-jdk21` image
and that the Jenkins container has finished starting. This pipeline uses JDK
21 (`javac --release 21`) and runs on the built-in executor; it does not use a
separate agent.

### A stage fails or the artifact is missing

Open the build's **Console Output** and find the first error; later stages and
the JAR archive are only produced if earlier commands succeed. Check the
checkout, compilation, and test output in order. The artifact is archived only
by the final **Deploy** stage after the JAR has been built and run.

## Project structure

```text
.
├── .gitignore
├── Jenkinsfile
├── LICENSE
├── README.md
└── src
    ├── main/java/com/rapidascent/HelloWorld.java
    └── test/java/com/rapidascent/HelloWorldTest.java
```

The test is a small executable Java class that fails by throwing an
`AssertionError` when the expected result is not returned.
