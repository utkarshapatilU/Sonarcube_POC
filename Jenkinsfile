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

        stage('Build & Sonar') {
            steps {
                withMaven(maven: 'Maven 3.9.4') {
                    withSonarQubeEnv('SonarQube') { 
                        bat 'mvn clean verify sonar:sonar -Dsonar.projectKey=java-poc-pipeline'
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
        
stage('Send Reports') {
    steps {
        withCredentials([string(credentialsId: 'SONAR_AUTH_TOKEN', variable: 'SONAR_TOKEN')]) {
            script {

           def response = bat(
    script: """curl -s -u %SONAR_TOKEN%: "http://192.168.0.193:9000/api/measures/component?componentKey=java-poc-pipeline&metricKeys=bugs,vulnerabilities,code_smells" > sonar-report.json""",
    returnStdout: true
)

                writeFile file: 'sonar-report.json', text: response
                echo "Saved Sonar report: sonar-report.json"
            }
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
