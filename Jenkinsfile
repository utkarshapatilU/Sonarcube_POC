pipeline {
    agent any

    // tools {
    //     jdk 'JDK_21' // Use the name of your JDK installation in Global Tool Configuration
    //     maven 'Maven3' // Use the name of your Maven installation in Global Tool Configuration
    // }
    environment {
        SONAR_ENV  = "SonarQube" 
        SONAR_HOST = "http://192.168.0.62:9000"  
        BRANCH_NAME = "${env.BRANCH_NAME}"

    }

    stages {
        stage('Clone Repository') {
            steps {
                script {
                    echo "Cloning the repository..."
                    checkout scmGit(branches: [[name: '*/feature-utkarsha']],extensions: [],userRemoteConfigs: [[credentialsId: 'github-token',  url: 'https://github.com/utkarshapatilU/Sonarcube_POC.git']])
                }
            }
        }
        // stage('Validate Dependencies') {
        //     steps {
        //         sh 'mvn validate'
        //     }
        // }
         stage('Build & Sonar') {
            steps {
                withMaven(maven: 'Maven 3.9.4') {
                    withSonarQubeEnv('SonarQube') { 
                        bat 'mvn clean verify sonar:sonar -Dsonar.projectKey=java-poc-pipeline'
                    }
                }
            }
        }

        // stage('Run Tests and Generate Coverage Report') {
        //     steps {
        //         echo "Running tests and generating code coverage report..."
        //         sh 'mvn test install site -P test'
        //     }
        // }
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
                    script: """curl -s -u %SONAR_TOKEN%: "http://192.168.0.62:9000/api/measures/component?componentKey=java-poc-pipeline&metricKeys=bugs,vulnerabilities,code_smells" """,
                    returnStdout: true
                )
                writeFile file: 'sonar-report.json', text: response
                echo "Saved Sonar report: sonar-report.json"
            }
        }
    }
}




        // stage('Quality Gate') {
        //     steps {
        //         script {
        //             echo "Waiting for SonarQube Quality Gate..."
        //             timeout(time: 5, unit: 'MINUTES') {
        //                 waitForQualityGate abortPipeline: true
        //             }
        //         }
        //     }
        // }
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

 