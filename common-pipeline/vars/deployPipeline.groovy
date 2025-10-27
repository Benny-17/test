def call(Map config) {
    pipeline {
        agent any

        // Parameters for dropdowns
        parameters {
            choice(name: 'SERVICE', choices: ['app-service', 'user-service', 'payment-service'], description: 'Select the service to build')
            choice(name: 'ENVIRONMENT', choices: ['dev', 'qa', 'prod'], description: 'Select target environment')
            choice(name: 'BRANCH', choices: ['main', 'develop', 'feature'], description: 'Select Git branch')
        }

        stages {
            stage('Checkout') {
                steps {
                    echo "Checking out ${params.SERVICE} code from branch ${params.BRANCH}..."
                    git branch: params.BRANCH, url: config.gitRepo
                }
            }

            stage('Build') {
                steps {
                    echo "Building ${params.SERVICE} for ${params.ENVIRONMENT}"
                    sh 'npm install'
                    sh 'npm run build'
                }
            }
        }

        post {
            success {
                echo "${params.SERVICE} build successful for ${params.ENVIRONMENT}"
            }
            failure {
                echo "${params.SERVICE} build failed for ${params.ENVIRONMENT}"
            }
            always {
                echo "Pipeline finished for ${params.SERVICE} (${params.ENVIRONMENT})"
            }
        }
    }
}
