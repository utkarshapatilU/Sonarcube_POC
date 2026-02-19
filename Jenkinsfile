pipeline {
    agent any

    environment {
        SONAR_ENV  = "SonarQube" 
        SONAR_HOST = "http://192.168.0.193:9000"  
        BRANCH_NAME = "${env.BRANCH_NAME}"
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    credentialsId: 'github-token',
                    url: 'https://github.com/utkarshapatilU/Sonarcube_POC.git'
            }
        }

        stage('Build') {
            steps {
                withMaven(maven: 'Maven 3.9.4') {
                script {
                    if (isUnix()) {
                        sh 'mvn clean compile'
                    } else {
                        bat 'mvn clean compile'
                    }
                }
            }
        }
        }

        stage('Sonar Scan (Docker)') {
    steps {
        withCredentials([string(credentialsId: 'SONAR_AUTH_TOKEN', variable: 'TOKEN')]) {
            script {
                if (isUnix()) {
                    sh """
                    docker run --rm \\
                      -v "\$PWD":/usr/src \\
                      -e SONAR_HOST_URL="\$SONAR_HOST" \\
                      -e SONAR_TOKEN="\$TOKEN" \\
                      sonarsource/sonar-scanner-cli \\
                      -Dsonar.projectKey=java-poc-pipeline \\
                      -Dsonar.projectName=java-poc-pipeline \\
                      -Dsonar.sources=.
                    """
                } else {
                    bat """
                    docker run --rm ^
                      -v "%cd%":/usr/src ^
                      -e SONAR_HOST_URL="%SONAR_HOST%" ^
                      -e SONAR_TOKEN="%TOKEN%" ^
                      sonarsource/sonar-scanner-cli ^
                      -Dsonar.projectKey=java-poc-pipeline ^
                      -Dsonar.projectName=java-poc-pipeline ^
                      -Dsonar.sources=.
                    """
                }
            }
        }
    }
}


        stage('Quality Gate') {
            steps {
                timeout(time: 2, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }

    post {
        success {
            echo "Scan Success for build: ${env.BUILD_NUMBER}"
        }
        failure {
            echo "Scan Failed for build: ${env.BUILD_NUMBER}"
        }
    }
}
