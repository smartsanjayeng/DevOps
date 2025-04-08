pipeline {
    agent any

    parameters {
        choice(name: 'DEPLOY_ENV', choices: ['Dev', 'SIT', 'UAT', 'Prod'], description: 'Choose the environment to deploy')
        string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'Git branch to build and deploy')
    }

    environment {
        NEXUS_CREDS = credentials('nexus_credentials')  // Nexus credentials
        NEXUS_URL = 'http://localhost:9090/repository/maven-releases/'
    }

    stages {
        stage('Setup Environment Variables') {
            steps {
                script {
                    if (params.DEPLOY_ENV == 'Dev') {
                        env.DEPLOY_PORT = '8081'
                        env.SPRING_PROFILE = 'dev'
                    } else if (params.DEPLOY_ENV == 'SIT') {
                        env.DEPLOY_PORT = '8082'
                        env.SPRING_PROFILE = 'sit'
                    } else if (params.DEPLOY_ENV == 'UAT') {
                        env.DEPLOY_PORT = '8083'
                        env.SPRING_PROFILE = 'uat'
                    } else if (params.DEPLOY_ENV == 'Prod') {
                        env.DEPLOY_PORT = '8084'
                        env.SPRING_PROFILE = 'prod'
                    }
                    echo "Environment: ${params.DEPLOY_ENV}, Port: ${env.DEPLOY_PORT}, Profile: ${env.SPRING_PROFILE}"
                }
            }
        }

        stage('Clone Repository') {
            steps {
                git branch: params.BRANCH_NAME, url: 'https://github.com/smartsanjayeng/DevOps.git'
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
                echo 'Starting Publishing JAR file to Nexus Repository...'
                bat "gradlew publish -PnexusUsername=${env.NEXUS_CREDS_USR} -PnexusPassword=${env.NEXUS_CREDS_PSW} -PnexusUrl=${env.NEXUS_URL}"
                echo 'Publishing completed.'
            }
        }

        stage('Kill Process on Port') {
            steps {
                script {
                    echo "Attempting to kill any process running on port ${env.DEPLOY_PORT}..."
                    try {
                        bat """
                        FOR /F "tokens=5" %%a IN ('netstat -aon ^| findstr :${env.DEPLOY_PORT} ^| findstr LISTENING') DO (
                            echo Killing process with PID %%a on port ${env.DEPLOY_PORT}...
                            taskkill /F /PID %%a
                        )
                        """
                        echo "Process on port ${env.DEPLOY_PORT} killed successfully (if any)."
                    } catch (Exception e) {
                        echo "No process found running on port ${env.DEPLOY_PORT}, or failed to kill process: ${e.getMessage()}"
                    }
                }
            }
        }

        stage('Deploy Application') {
            steps {
                echo "Deploying application to ${params.DEPLOY_ENV} environment on port ${env.DEPLOY_PORT}..."
                bat "java -jar build/libs/shopping-app-1.0.1.jar --server.port=${env.DEPLOY_PORT} --spring.profiles.active=${env.SPRING_PROFILE}"
            }
        }
    }
}
