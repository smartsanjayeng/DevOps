pipeline {
    agent any

    parameters {
        choice(name: 'DEPLOY_ENV', choices: ['Dev', 'SIT', 'UAT', 'Prod'], description: 'Choose the environment to deploy')
        string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'Git branch to build and deploy')
    }

    environment {
        NEXUS_CREDS = credentials('nexus_credentials')  // Nexus credentials
        DEPLOY_PORT = '' // Dynamic port assignment based on environment
        SPRING_PROFILE = '' // Spring profile for environment
        NEXUS_URL = 'http://localhost:8081/repository/maven-releases/'
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

        stage('Archive Artifact') {
            steps {
                echo 'Archiving JAR file...'
                archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
                echo 'JAR file archived in Jenkins.'
            }
        }

        stage('Deploy') {
            steps {
                echo "Deploying application to ${params.DEPLOY_ENV} environment on port ${env.DEPLOY_PORT}..."

                // Stop existing process if running
                bat '''
                FOR /F "tokens=5" %%a IN ('netstat -aon ^| findstr :%DEPLOY_PORT% ^| findstr LISTENING') DO (
                    taskkill /F /PID %%a
                )
                '''

                // Run the application JAR
                bat "java -jar build\\libs\\*.jar --spring.profiles.active=${env.SPRING_PROFILE} --server.port=${env.DEPLOY_PORT}"

                echo "Deployment completed."
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
