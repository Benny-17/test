def call(Map config) {
    pipeline {
        agent any

        environment {
            SERVICE = config.serviceName
            ENVIRONMENT = config.environment
        }

        stages {
            stage('Checkout') {
                steps {
                    echo "Checking out ${SERVICE} code..."
                    git branch: config.branch ?: 'main', url: config.gitRepo
                }
            }

            stage('Build') {
                steps {
                    echo "Building ${SERVICE} for ${ENVIRONMENT}"
                    sh 'npm install'
                    sh 'npm run build'
                }
            }
        }

        post {
            success {
                echo "${SERVICE} build successful for ${ENVIRONMENT}"
            }
            failure {
                echo "${SERVICE} build failed for ${ENVIRONMENT}"
            }
            always {
                echo "Pipeline finished for ${SERVICE} (${ENVIRONMENT})"
            }
        }
    }
}
