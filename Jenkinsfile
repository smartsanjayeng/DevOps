pipeline {
    agent any

    environment {
        NEXUS_CREDS = credentials('nexus_credentials')  // Assuming 'Username with password' credentials in Jenkins
    }

    stages {
        stage('Clone Repository') {
            steps {
                git branch: 'main', url: 'https://github.com/smartsanjayeng/DevOps.git'
            }
        }

        stage('Build') {
            steps {
                echo 'Starting Clean & Build...'
                bat 'gradlew clean build'
                echo 'Build completed.'
            }
        }

        stage('Run Tests') {
            steps {
                echo 'Starting JUnit Test Cases...'
                bat 'gradlew test'
                echo 'JUnit Test Cases completed.'
            }
            post {
                always {
                    junit 'build/test-results/test/*.xml'
                }
            }
        }

        stage('Publish to Nexus') {
            steps {
                echo 'Starting Publishing Jar file to Nexus Repository...'
                bat "gradlew publish -PnexusUsername=${env.NEXUS_CREDS_USR} -PnexusPassword=${env.NEXUS_CREDS_PSW}"
                echo 'Publishing completed.'
            }
        }

        stage('Archive Artifact') {
            steps {
                echo 'Archiving JAR file...'
                archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
                echo 'JAR file archived in Jenkins.'
            }
        }

        stage('Deploy') {
            steps {
                echo "Deploying application..."
                // Add deployment steps here (Docker, Kubernetes, etc.)
            }
        }
    }

    post {
        always {
            echo 'Pipeline completed.'
        }
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
