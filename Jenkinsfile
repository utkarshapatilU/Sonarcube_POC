pipeline {
    agent any

    environment {
        SONAR_HOST = "http://localhost:9000"
        SONAR_ENV  = "SonarQube"
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'feature-utkarsha', url: 'https://github.com/utkarshapatilU/Sonarcube_POC.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Sonar Scan') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh '''
                    sonar-scanner \
                    -Dsonar.projectKey=${JOB_NAME} \
                    -Dsonar.projectName=${JOB_NAME} \
                    -Dsonar.sources=. \
                    -Dsonar.host.url=http://localhost:9000 \
                    -Dsonar.login=$SONAR_AUTH_TOKEN
                    '''
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
            echo "Scan Success for user: ${env.BUILD_USER}"
        }
        failure {
            echo "Scan Failed for user: ${env.BUILD_USER}"
        }
    }
}
