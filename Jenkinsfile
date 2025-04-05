pipeline {
    agent any

    environment {
        NEXUS_USER = credentials('nexus_credentials')
        NEXUS_PASS = credentials('nexus_credentials')
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
                echo 'Starting Junit Test...'
        		bat 'gradlew test'
        		echo 'Junit Test completed.'
            }
            post {
                always {
                    junit 'build/test-results/test/*.xml'
                }
            }
        }
		
		stage('Publish to Nexus') {
            steps {
				echo 'Starting Publishing Jar file fo Nexus Repository...'
                bat "gradlew publish -PnexusUsername=${env.NEXUS_USER} -PnexusPassword=${env.NEXUS_PASS}"
                echo 'Publishing completed.'
            }
        }
        
        stage('Deploy') {
            steps {
                echo "Deploying application..."
            }
        }
    }
}
