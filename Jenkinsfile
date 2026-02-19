pipeline {
    agent any

    environment {
        SONAR_ENV  = "SonarQube" 
        SONAR_HOST = "http://192.168.0.193:9000"  
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'feature-utkarsha',
                    credentialsId: 'github-token',
                    url: 'https://github.com/utkarshapatilU/Sonarcube_POC.git'
            }
        }

        stage('Build') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'mvn clean compile'
                    } else {
                        bat 'mvn clean compile'
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
                              -Dsonar.projectKey=\$JOB_NAME \\
                              -Dsonar.projectName=\$JOB_NAME \\
                              -Dsonar.sources=.
                            """
                        } else {
                            bat """
                            docker run --rm ^
                              -v "%cd%":/usr/src ^
                              -e SONAR_HOST_URL="%SONAR_HOST%" ^
                              -e SONAR_TOKEN="%TOKEN%" ^
                              sonarsource/sonar-scanner-cli ^
                              -Dsonar.projectKey=%JOB_NAME% ^
                              -Dsonar.projectName=%JOB_NAME% ^
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
