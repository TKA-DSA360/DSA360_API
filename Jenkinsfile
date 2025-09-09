pipeline {
    agent any
    
    environment {
        DATE_TIME = new Date().format('yyyyMMddHHmmss')
        DOCKER_IMAGE_NAME = "ram2715/dsa360-${DATE_TIME}"
        DOCKERFILE_PATH = 'Dockerfile'
        DOCKERHUB_CREDENTIALS = credentials('dockerpassword')
        PREVIOUS_IMAGE = ''
    }

    tools {
        maven "MAVEN_HOME"
    }

    stages {
        stage('Get Code') {
            steps {
                echo "Cloning the repository..."
                git branch: 'main', url: 'https://github.com/TKA-DSA360/DSA360_API.git'
            }
        }

        stage('Build') {
            steps {
                echo "Building the Maven project..."
                bat "mvn -Dmaven.test.failure.ignore=true clean package"
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo "Running SonarQube analysis..."
                withSonarQubeEnv('MySonarQubeServer') {
                    bat "mvn sonar:sonar"
                }
            }
        }

        stage('Find Previous Docker Image') {
            steps {
                script {
                    echo "Searching for previous Docker image..."

                    // List all Docker images and filter programmatically
                    def existingImages = bat(script: 'docker images --format "{{.Repository}}:{{.Tag}}"', returnStdout: true).trim()

                    PREVIOUS_IMAGE = ''
                    existingImages.split('\n').each { img ->
                        if (img.contains("ram2715/dsa360-")) {
                            PREVIOUS_IMAGE = img.trim()
                            echo "Found previous image: ${PREVIOUS_IMAGE}"
                        }
                    }

                    if (PREVIOUS_IMAGE) {
                        echo "Previous image found: ${PREVIOUS_IMAGE}"
                    } else {
                        echo "No previous image found. Proceeding with a new build."
                    }
                }
            }
        }

        stage('Remove Previous Docker Image') {
            when {
                expression { PREVIOUS_IMAGE != '' }
            }
            steps {
                script {
                    echo "Removing previous Docker image: ${PREVIOUS_IMAGE}"
                    bat "docker rmi ${PREVIOUS_IMAGE} || echo 'Failed to delete previous image or not found.'"
                }
            }
        }

        stage('Build New Docker Image') {
            steps {
                script {
                    echo "Building new Docker image..."
                    def dockerImage = docker.build(env.DOCKER_IMAGE_NAME, "--cache-from ${env.DOCKER_IMAGE_NAME} -f ${env.DOCKERFILE_PATH} .")
                    echo "Docker image built: ${env.DOCKER_IMAGE_NAME}"
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    echo "Pushing Docker image to DockerHub..."
                    withCredentials([string(credentialsId: 'dockertoken', variable: 'dockertoken')]) {
                        bat "docker login -u ram2715 -p ${dockertoken}"
                        bat "docker push ${env.DOCKER_IMAGE_NAME}"
                        echo "Docker image pushed successfully!"
                    }
                }
            }
        }

        stage('Cleanup Workspace') {
            steps {
                echo "Cleaning up unnecessary files..."
                bat "mvn clean"
                bat "docker system prune -f"
            }
        }
    }
post {
    success {
        echo '✅ Pipeline succeeded! Docker image pushed and cleaned up successfully.'

        script {
            def logFileSource = "C:\\ProgramData\\Jenkins\\.jenkins\\jobs\\${env.JOB_NAME}\\builds\\${env.BUILD_NUMBER}\\log"
            def logFileDestination = "${WORKSPACE}\\build-log-${env.BUILD_NUMBER}.txt"

            echo "Copying Jenkins console log file..."
            bat "copy \"${logFileSource}\" \"${logFileDestination}\""

            emailext (
                to: 'salikramchadar@gmail.com',
                subject: "✅ SUCCESS: DSA360 Pipeline - Build #${env.BUILD_NUMBER}",
                body: """
                    <h3>Jenkins Build Success</h3>
                    <p><strong>Project:</strong> ${env.JOB_NAME}</p>
                    <p><strong>Build Number:</strong> ${env.BUILD_NUMBER}</p>
                    <p><strong>Status:</strong> ✅ SUCCESS</p>
                    <p><strong>Docker Image:</strong> ${env.DOCKER_IMAGE_NAME}</p>
                    <p><strong>View Build:</strong> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                """,
                attachLog: true,
                attachmentsPattern: "build-log-${env.BUILD_NUMBER}.txt",
                mimeType: 'text/html'
            )
        }
    }

    failure {
        echo '❌ Pipeline failed! Check logs for details.'

        script {
            def logFileSource = "C:\\ProgramData\\Jenkins\\.jenkins\\jobs\\${env.JOB_NAME}\\builds\\${env.BUILD_NUMBER}\\log"
            def logFileDestination = "${WORKSPACE}\\build-log-${env.BUILD_NUMBER}.txt"

            echo "Copying Jenkins console log file..."
            bat "copy \"${logFileSource}\" \"${logFileDestination}\""

            emailext (
                to: 'salikramchadar@gmail.com',
                subject: "❌ FAILURE: DSA360 Pipeline - Build #${env.BUILD_NUMBER}",
                body: """
                    <h3>Jenkins Build Failure</h3>
                    <p><strong>Project:</strong> ${env.JOB_NAME}</p>
                    <p><strong>Build Number:</strong> ${env.BUILD_NUMBER}</p>
                    <p><strong>Status:</strong> ❌ FAILED</p>
                    <p><strong>View Build:</strong> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                    <p>Please check the attached log for details.</p>
                """,
                attachLog: true,
                attachmentsPattern: "build-log-${env.BUILD_NUMBER}.txt",
                mimeType: 'text/html'
            )
        }
    }
}

}



