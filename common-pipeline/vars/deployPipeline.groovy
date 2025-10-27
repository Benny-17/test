def call(Map config) {
    pipeline {
        agent any

        stages {
            stage('Initialize') {
                steps {
                    script {
                        // Dynamically assign environment variables
                        env.SERVICE = config.serviceName
                        env.ENVIRONMENT = config.environment
                    }
                }
            }

            stage('Checkout') {
                steps {
                    echo "Checking out ${env.SERVICE} code..."
                    git branch: config.branch ?: 'main', url: config.gitRepo
                }
            }

            stage('Build') {
                steps {
                    echo "Building ${env.SERVICE} for ${env.ENVIRONMENT}"
                    sh 'npm install'
                    sh 'npm run build'
                }
            }
        }

        post {
            success {
                echo "${env.SERVICE} build successful for ${env.ENVIRONMENT}"
            }
            failure {
                echo "${env.SERVICE} build failed for ${env.ENVIRONMENT}"
            }
            always {
                echo "Pipeline finished for ${env.SERVICE} (${env.ENVIRONMENT})"
            }
        }
    }
}
