pipeline {
    // Use the Jenkins executor, which already provides JDK 21.
    agent any

    options {
        // Keep source retrieval visible in the dedicated Checkout stage.
        skipDefaultCheckout(true)
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out repository source code...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Compiling the Java application and packaging the artifact...'
                sh '''
                    mkdir -p build/classes
                    javac --release 21 -d build/classes $(find src/main/java -name '*.java')
                    jar --create --file build/jenkins-ci-cd-demo.jar \\
                        --main-class com.rapidascent.HelloWorld -C build/classes .
                '''
            }
        }

        stage('Test') {
            steps {
                echo 'Compiling and running automated unit tests...'
                sh '''
                    mkdir -p build/test-classes
                    javac --release 21 -cp build/classes -d build/test-classes \\
                        $(find src/test/java -name '*.java')
                    java -cp build/classes:build/test-classes com.rapidascent.HelloWorldTest
                '''
            }
        }

        stage('Deploy') {
            steps {
                echo 'Simulating deployment and archiving the compiled artifact...'
                sh 'java -jar build/jenkins-ci-cd-demo.jar'
                archiveArtifacts artifacts: 'build/jenkins-ci-cd-demo.jar', fingerprint: true
            }
        }
    }

    post {
        success {
            echo 'SUCCESS: Jenkins CI/CD demonstration completed successfully.'
        }
        failure {
            echo 'FAILURE: Jenkins CI/CD demonstration failed. Review the stage output above.'
        }
    }
}
