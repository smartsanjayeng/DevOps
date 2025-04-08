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
        DEPLOY_PORT = ''
        REPOSITORY_TYPE = ''
    }

    stages {

        stage('Setup Environment Variables') {
            steps {
                script {
                    if (params.DEPLOY_ENV in ['Dev', 'SIT']) {
                        env.REPOSITORY_TYPE = 'snapshots'
                        env.DEPLOY_PORT = (params.DEPLOY_ENV == 'Dev') ? '8081' : '8082'
                    } else {
                        env.REPOSITORY_TYPE = 'releases'
                        env.DEPLOY_PORT = (params.DEPLOY_ENV == 'UAT') ? '8083' : '8084'
                    }
                    echo "🧩 Deploy Environment: ${params.DEPLOY_ENV}"
                    echo "🚀 Repository Type: ${env.REPOSITORY_TYPE}"
                    echo "🚪 Deploy Port: ${env.DEPLOY_PORT}"
                }
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

        stage('Run Tests') {
            steps {
                echo '🧪 Running tests...'
                bat "gradlew test"
                echo '🧪 Running tests completed'
            }
            post {
                always {
                    junit 'build/test-results/test/*.xml'
                }
            }
        }

        stage('Publish to Nexus') {
            steps {
                echo "🚀 Publishing to Nexus (${env.REPOSITORY_TYPE} repository)..."
                bat "gradlew publish -PnexusUsername=${env.NEXUS_CREDS_USR} -PnexusPassword=${env.NEXUS_CREDS_PSW} -PrepositoryType=${env.REPOSITORY_TYPE}"
                echo "🚀 Published to Nexus (${env.REPOSITORY_TYPE} repository)"
            }
        }

        stage('Deploy Application') {
            steps {
                script {
                    echo "🚀 Deploying application to ${params.DEPLOY_ENV} environment on port ${env.DEPLOY_PORT}..."
                    bat '''
                    for /f "delims=" %%i in (version.txt) do set VERSION=%%i
                    set JAR_NAME=shopping-app-%VERSION%.jar
                    echo 🧩 Running application: build\\libs\\%JAR_NAME%
                    java -jar build\\libs\\%JAR_NAME% --server.port=%DEPLOY_PORT%
                    '''
                    echo "🚀 Deploying application to ${params.DEPLOY_ENV} environment on port ${env.DEPLOY_PORT} completed"
                }
            }
        }

        stage('Archive Artifact') {
            steps {
                archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
                echo '📦 Artifact archived for download.'
            }
        }
    }

    post {
        success {
            echo '✅ Build completed successfully!'
        }
        failure {
            echo '❌ Build failed!'
        }
    }
}
