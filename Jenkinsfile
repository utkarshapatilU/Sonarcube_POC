pipeline {
    agent any

    environment {
        SONAR_HOST = "http://10.104.224.85:9000"
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

        stage('SonarQube Code Analysis') {
            steps {
                script {
                    // Detect branch name - works for both multibranch and regular pipelines
                    // env.BRANCH_NAME is automatically set in multibranch pipelines
                    // env.GIT_BRANCH is set when using Git plugin
                    def branchName = env.BRANCH_NAME ?: env.GIT_BRANCH
                    
                    // Remove 'origin/' prefix if present (common in GIT_BRANCH)
                    if (branchName) {
                        branchName = branchName.replaceAll('origin/', '').replaceAll('refs/heads/', '')
                    }
                    
                    // Fallback to 'main' if still empty or HEAD
                    if (!branchName || branchName == 'HEAD' || branchName.isEmpty()) {
                        branchName = 'main'
                    }
                    
                    def sanitizedBranch = branchName.replaceAll("[^a-zA-Z0-9-_]", "_")
                    def dynamicProjectKey = "${BASE_PROJECT_KEY}-${sanitizedBranch}"

                    echo "=========================================="
                    echo "Branch Detection:"
                    echo "  - BRANCH_NAME env var: ${env.BRANCH_NAME ?: 'NOT SET'}"
                    echo "  - GIT_BRANCH env var: ${env.GIT_BRANCH ?: 'NOT SET'}"
                    echo "  - Detected branch: ${branchName}"
                    echo "  - Sanitized branch: ${sanitizedBranch}"
                    echo "  - SonarQube Project Key: ${dynamicProjectKey}"
                    echo "=========================================="

                    // Verify that target/classes exists before running SonarQube
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
                          -Dsonar.projectKey=${dynamicProjectKey} ^
                          -Dsonar.projectName=${dynamicProjectKey} ^
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
 