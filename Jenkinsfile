pipeline {
    agent any

    parameters {
        choice(name: 'DEPLOY_ENV', choices: ['Dev', 'SIT', 'UAT', 'Prod'], description: 'Choose the environment to deploy')
        string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'Git branch to build and deploy')
    }

    environment {
        NEXUS_CREDS = credentials('nexus_credentials')
        NEXUS_URL_SNAPSHOTS = 'http://localhost:9090/repository/maven-snapshots/'
        NEXUS_URL_RELEASES = 'http://localhost:9090/repository/maven-releases/'

        // Dynamically calculate repository type based on DEPLOY_ENV
        REPOSITORY_TYPE = "${params.DEPLOY_ENV in ['Dev', 'SIT'] ? 'snapshots' : 'releases'}"
        DEPLOY_PORT = "${params.DEPLOY_ENV == 'Dev' ? '8081' : params.DEPLOY_ENV == 'SIT' ? '8082' : params.DEPLOY_ENV == 'UAT' ? '8083' : '8084'}"
    }

    stages {
        stage('Print Environment Variables') {
            steps {
                echo "🧩 Deploy Environment: ${params.DEPLOY_ENV}"
                echo "🚀 Repository Type: ${env.REPOSITORY_TYPE}"
                echo "🚪 Deploy Port: ${env.DEPLOY_PORT}"
            }
        }

        stage('Clone Repository') {
            steps {
                echo 'Cloning Git Repository project...'
                git branch: params.BRANCH_NAME, url: 'https://github.com/smartsanjayeng/DevOps.git'
                echo 'Cloning Git Repository Completed'
            }
        }

        stage('Increment Version & Git Tag') {
            steps {
                script {
                    echo '🔢 Incrementing version and tagging Git...'
                    bat "gradlew incrementVersion -PrepositoryType=${env.REPOSITORY_TYPE}"
                    bat "gradlew gitTag -PrepositoryType=${env.REPOSITORY_TYPE}"
                    echo '🔢 Incrementing version and tagging Git done'
                }
            }
        }

        stage('Build') {
            steps {
                echo '🛠️ Building the project...'
                bat "gradlew clean build -PrepositoryType=${env.REPOSITORY_TYPE}"
                echo '🛠️ Build completed'
            }
        }

        stage('Archive Artifact') {
            steps {
                echo 'Archiving JAR file...'
                archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
                echo 'JAR file successfully archived in Jenkins.'
            }
        }

        stage('Deploy') {
		    steps {
		        echo "Deploying application to ${params.DEPLOY_ENV} environment on port ${env.DEPLOY_PORT}..."

	        // Stop existing process if running
	        bat """
	        FOR /F "tokens=5" %%a IN ('netstat -aon ^| findstr :${env.DEPLOY_PORT} ^| findstr LISTENING') DO (
	            echo Killing process on port ${env.DEPLOY_PORT} with PID %%a
	            taskkill /F /PID %%a
	        )
	        IF ERRORLEVEL 1 (
	            echo No process found on port ${env.DEPLOY_PORT}
	        )
	        """
	
	        // Get absolute path of the workspace
	        def workspacePath = pwd()
	
	        // Set the absolute path to the JAR file
	        def jarPath = "${workspacePath}\\build\\libs\\*.jar"
	
	        // Check if the JAR file exists
	        bat """
	        echo Current Directory: %cd%
	        echo Workspace Path: ${workspacePath}
	        IF EXIST ${jarPath} (
	            echo JAR file found: ${jarPath}
	            start \"ShoppingCart\" cmd /c \"java -jar ${jarPath} --server.port=${env.DEPLOY_PORT}\"
	        ) ELSE (
	            echo ERROR: JAR file not found at ${jarPath}
	            exit /B 1
	        )
	        """
	
	        echo "Deployment completed."
		}
    }

    post {
        always {
            echo 'Pipeline completed.'
            //cleanWs()
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
