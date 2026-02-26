pipeline {
    agent any

    environment {
        SONAR_HOST = "http://192.168.0.133:9000"
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

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    script {
                        def qg = waitForQualityGate()
                        echo "Quality Gate status: ${qg.status}"
                        if (qg.status != 'OK') {
                            currentBuild.result = 'FAILURE'
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                // Last commit author information for this build
                def authorName = bat(
                    script: 'git log -1 --pretty=format:%an',
                    returnStdout: true
                ).trim()

                def authorEmail = bat(
                    script: 'git log -1 --pretty=format:%ae',
                    returnStdout: true
                ).trim()

                echo "Report user: ${authorName} <${authorEmail}>"
                echo "Final Quality Gate / Build result: ${currentBuild.currentResult}"

                // Show in Jenkins build header
                currentBuild.description = "User: ${authorName} | QG: ${currentBuild.currentResult}"
            }
        }
        success {
            echo "✅ Scan Success for build: ${env.BUILD_NUMBER}"
        }
        failure {
            echo "❌ Scan Failed for build: ${env.BUILD_NUMBER}"
        }
    }
}
 