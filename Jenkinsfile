pipeline {
    agent any

    stages {
        stage('Clone Repository') {
            steps {
                git branch: 'main', url: 'https://github.com/smartsanjayeng/DevOps.git'
            }
        }

        stage('Build') {
            steps {
                bat 'gradlew clean build'
            }
        }

        stage('Run Tests') {
            steps {
                bat 'gradlew test'
            }
            post {
                always {
                    junit 'build/test-results/test/*.xml'
                }
            }
        }

        stage('Deploy') {
            steps {
                echo "Deploying application..."
            }
        }
    }
}
