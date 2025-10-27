def call(Map config) {
    pipeline {
        agent any

        environment {
            SERVICE = config.serviceName
            ENVIRONMENT = config.environment
            S3_BUCKET = config.s3Bucket
            CLOUDFRONT_ID = config.cloudFrontId
            HEALTH_URL = config.healthEndpoint
        }

        stages {
            stage('Checkout') {
                steps {
                    echo "Checking out ${SERVICE} code..."
                    dir("${WORKSPACE}/${SERVICE}") {
                        git branch: config.branch ?: 'main', url: config.gitRepo
                    }
                }
            }

            stage('Build') {
                steps {
                    dir("${WORKSPACE}/${SERVICE}") {
                        echo "Building ${SERVICE} for ${ENVIRONMENT}"
                        sh 'npm install'
                        sh 'npm run build'
                    }
                }
            }

            stage('Deploy') {
                steps {
                    echo "Deploying ${SERVICE} to ${ENVIRONMENT}"
                    sh """
                        aws s3 sync ${WORKSPACE}/${SERVICE}/dist/ s3://${S3_BUCKET}/
                        aws cloudfront create-invalidation --distribution-id ${CLOUDFRONT_ID} --paths "/*"
                    """
                }
            }

            stage('Health Check') {
                steps {
                    echo "Checking health of ${SERVICE}..."
                    script {
                        try {
                            sh "curl -f ${HEALTH_URL}"
                            echo "Health check passed!"
                        } catch (err) {
                            error "Health check failed!"
                        }
                    }
                }
            }
        }

        post {
            success {
                echo "${SERVICE} deployed successfully to ${ENVIRONMENT}"
            }
            failure {
                echo "${SERVICE} deployment failed in ${ENVIRONMENT}"
            }
            always {
                echo "Pipeline finished for ${SERVICE} (${ENVIRONMENT})"
            }
        }
    }
}
