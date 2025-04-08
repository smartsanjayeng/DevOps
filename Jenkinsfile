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

        stage('Publish to Nexus') {
            steps {
                script {
                    echo '🚀 Publishing to Nexus...'
                    bat "gradlew publish -PnexusUsername=${env.NEXUS_CREDS_USR} -PnexusPassword=${env.NEXUS_CREDS_PSW} -PrepositoryType=${env.REPOSITORY_TYPE}"
                    echo '🚀 Publish completed'
                }
            }
        }

        stage('Deploy Application') {
            steps {
                script {
                    echo '🚀 Deploying the application...'
                    // Sample deployment logic, you can customize it
                    echo "Deploying to port ${env.DEPLOY_PORT}"
                    // Add your deployment commands here
                    echo '🚀 Deployment completed'
                }
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline completed successfully!'
        }
        failure {
            echo '❌ Pipeline failed!'
        }
    }
}
