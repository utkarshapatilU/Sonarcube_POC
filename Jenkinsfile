pipeline {
    agent any

    environment {
        SONAR_HOST = "http://192.168.0.153:9000"
        BASE_PROJECT_KEY = "java-poc-pipeline"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                withMaven(maven: 'Maven 3.9.4') {
                    bat 'mvn clean compile'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    // Get branch name for logging
                    def branchName = env.BRANCH_NAME ?: env.GIT_BRANCH ?: 'unknown'
                    branchName = branchName.replaceAll('origin/', '').replaceAll('refs/heads/', '')
                    
                    echo "Running SonarQube analysis on branch: ${branchName}"

                    // Verify compiled classes exist
                    bat '''
                        if not exist "target\\classes" (
                            echo ERROR: target\\classes directory does not exist. Build may have failed.
                            exit /b 1
                        )
                    '''

                    def scannerHome = tool 'SonarScanner'

                    withSonarQubeEnv('SonarQube') {
                        bat """
                        "${scannerHome}\\bin\\sonar-scanner.bat" ^
                          -Dsonar.projectKey="${BASE_PROJECT_KEY}" ^
                          -Dsonar.projectName="${BASE_PROJECT_KEY}" ^
                          -Dsonar.sources=java-poc/src/main/java ^
                          -Dsonar.java.binaries=target/classes ^
                          -Dsonar.host.url=${SONAR_HOST}
                        """
                    }
                }
            }
        }

        // stage('Quality Gate') {
        //     steps {
        //         timeout(time: 5, unit: 'MINUTES') {
        //             waitForQualityGate abortPipeline: true
        //         }
        //     }
        // }
    }

    post {
        success {
            echo "✅ Scan Success for build: ${env.BUILD_NUMBER}"
        }
        failure {
            echo "❌ Scan Failed for build: ${env.BUILD_NUMBER}"
        }
    }
}
 