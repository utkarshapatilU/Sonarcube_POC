pipeline {
    agent any

    environment {
        SONAR_HOST = "http://172.25.96.1:9000"
        PROJECT_KEY = "java-poc-pipeline"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scmGit(
                    branches: [[name: 'feature-utkarsha']],
                    userRemoteConfigs: [[
                        credentialsId: 'github-token',
                        url: 'https://github.com/utkarshapatilU/Sonarcube_POC.git'
                    ]]
                )
            }
        }

        stage('Build') {
            steps {
                withMaven(maven: 'Maven 3.9.4') {
                    bat 'mvn clean install -DskipTests'
                }
            }
        }

        // stage('Sonar Scan (Docker)') {
        //     steps {
        //         withCredentials([string(credentialsId: 'SONAR_AUTH_TOKEN', variable: 'SONAR_TOKEN')]) {
        //             bat """
        //             docker run --rm ^
        //               -v "%cd%":/usr/src ^
        //               -e SONAR_HOST_URL="%SONAR_HOST%" ^
        //               -e SONAR_TOKEN="%SONAR_TOKEN%" ^
        //               sonarsource/sonar-scanner-cli ^
        //               -Dsonar.projectKey=%PROJECT_KEY% ^
        //               -Dsonar.projectName=%PROJECT_KEY% ^
        //               -Dsonar.sources=. ^
        //               -Dsonar.java.binaries=target/classes ^
        //               -Dsonar.branch.name=feature-utkarsha
        //             """
        //         }
        //     }
        // }

    stage('SonarQube Code Analysis') {
    steps {
        script {
            def scannerHome = tool 'SonarScanner'
            withSonarQubeEnv('SonarQube') {
                sh """
                ${scannerHome}/bin/sonar-scanner \
                  -Dsonar.projectKey=java-poc-pipeline \
                  -Dsonar.projectName=java-poc-pipeline \
                  -Dsonar.sources=. \
                  -Dsonar.java.binaries=target/classes \
                  -Dsonar.host.url=http://172.25.96.1:9000 \
                  -Dsonar.branch.name=feature-utkarsha \
                  -Dsonar.token=$SONAR_AUTH_TOKEN
                """
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

        // stage('Send Reports') {
        //     steps {
        //         withCredentials([string(credentialsId: 'SONAR_AUTH_TOKEN', variable: 'SONAR_TOKEN')]) {
        //             bat """
        //             curl -s -u %SONAR_TOKEN%: ^
        //             "http://192.168.0.62:9000/api/measures/component?component=java-poc-pipeline&metricKeys=bugs,vulnerabilities,code_smells" ^
        //             -o sonar-report.json
        //             """
        //         }
        //         echo "Saved Sonar report: sonar-report.json"
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
