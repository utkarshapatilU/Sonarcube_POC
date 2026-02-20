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
                    bat 'mvn clean install -DskipTests'
                }
            }
        }

        stage('SonarQube Code Analysis') {
    steps {
        script {

            def branchName = env.BRANCH_NAME ?: "main"
            def sanitizedBranch = branchName.replaceAll("[^a-zA-Z0-9-_]", "_")
            def dynamicProjectKey = "${BASE_PROJECT_KEY}-${sanitizedBranch}"

            echo "Running Sonar scan for branch: ${branchName}"
            echo "Dynamic Project Key: ${dynamicProjectKey}"

            def scannerHome = tool 'SonarScanner'

            withSonarQubeEnv('SonarQube') {
                bat """
                "${scannerHome}\\bin\\sonar-scanner.bat" ^
                  -Dsonar.projectKey=${dynamicProjectKey} ^
                  -Dsonar.projectName=${dynamicProjectKey} ^
                  -Dsonar.sources=. ^
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
                    waitForQualityGate abortPipeline: true
                }
            }
        }
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
 