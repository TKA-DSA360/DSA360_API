pipeline {
    agent any

    // ──────────────────────────────────────────────────────────────
    // GLOBAL CONFIG — 2025 Best Practices
    // ──────────────────────────────────────────────────────────────
    environment {
        // Semantic + predictable tagging (no random timestamp!)
        IMAGE_NAME     = "ram2715/dsa360"
        BUILD_TAG      = "${env.BUILD_NUMBER}"           // e.g. 87
        GIT_COMMIT_SHORT = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
        IMAGE_TAG      = "${BUILD_TAG}-${GIT_COMMIT_SHORT}"  // 87-a1b2c3d
        FULL_IMAGE     = "${IMAGE_NAME}:${IMAGE_TAG}"
        LATEST_IMAGE   = "${IMAGE_NAME}:latest"

        // Docker & Registry
        DOCKERFILE     = "Dockerfile"
    }

    tools {
        maven "MAVEN_HOME"
    }

    stages {
        // ──────────────────────────────────────────────────────────────
        stage('Checkout Code') {
            steps {
                echo "Checking out main branch..."
                git branch: 'main', url: 'https://github.com/TKA-DSA360/DSA360_API.git'
            }
        }

        // ──────────────────────────────────────────────────────────────
        stage('Maven Build & Test') {
            steps {
                echo "Building and running tests..."
                bat "mvn -B -Dmaven.test.failure.ignore=false clean verify"
            }
        }

        // ──────────────────────────────────────────────────────────────
        stage('SonarQube Analysis') {
            steps {
                echo "Running SonarQube analysis..."
                withSonarQubeEnv('MySonarQubeServer') {
                    bat "mvn -B sonar:sonar -Dsonar.projectKey=DSA360_Solution -Dsonar.projectName=DSA360_Solution"
                }
            }
        }

        // ──────────────────────────────────────────────────────────────
        stage('Quality Gate') {
            steps {
                echo "Waiting for SonarQube Quality Gate..."
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
                echo "Quality Gate PASSED"
            }
        }

        // ──────────────────────────────────────────────────────────────
        stage('Build Docker Image') {
            steps {
                script {
                    echo "Building Docker image: ${FULL_IMAGE}"
                    bat """
                        docker build -t ${FULL_IMAGE} -t ${LATEST_IMAGE} -f ${DOCKERFILE} .
                    """
                    echo "Image built successfully"
                }
            }
        }

        // ──────────────────────────────────────────────────────────────
        stage('Trivy Vulnerability Scan') {
            steps {
                echo "Scanning image for HIGH/CRITICAL vulnerabilities..."
                bat """
                    if not exist trivy.exe (
                        powershell -Command "Invoke-WebRequest -Uri https://github.com/aquasecurity/trivy/releases/latest/download/trivy_0.58.1_Windows-64bit.zip -OutFile trivy.zip; Expand-Archive trivy.zip .; move trivy_0.58.1_Windows-64bit\\trivy.exe ."
                    )
                    trivy.exe image --exit-code 1 --no-progress --severity HIGH,CRITICAL ${FULL_IMAGE}
                """
                echo "No critical vulnerabilities found"
            }
        }

        // ──────────────────────────────────────────────────────────────
        stage('Push to Docker Hub') {
            steps {
                echo "Pushing image to Docker Hub..."
                withCredentials([usernamePassword(credentialsId: 'dockerhub', 
                                 usernameVariable: 'DOCKER_USER', 
                                 passwordVariable: 'DOCKER_PASS')]) {
                    bat """
                        docker login -u %DOCKER_USER% -p %DOCKER_PASS%
                        docker push ${FULL_IMAGE}
                        docker push ${LATEST_IMAGE}
                    """
                }
                echo "Successfully pushed ${FULL_IMAGE} and latest"
            }
        }

        // ──────────────────────────────────────────────────────────────
        stage('Cleanup Local Images') {
            steps {
                echo "Cleaning up local Docker images..."
                bat """
                    docker rmi ${FULL_IMAGE}  || echo "Not found"
                    docker rmi ${LATEST_IMAGE} || echo "Not found"
                    docker image prune -f
                """
            }
        }
    }

    // ──────────────────────────────────────────────────────────────
    // POST ACTIONS — Professional Email + Artifacts
    // ──────────────────────────────────────────────────────────────
    post {
        always {
            // Archive JAR and log
            archiveArtifacts artifacts: '**/target/*.jar', allowEmptyArchive: true
        }

        success {
            echo 'Pipeline succeeded!'
            script {
                def logFile = "${WORKSPACE}\\build-log-${BUILD_NUMBER}.txt"
                bat "copy \"C:\\ProgramData\\Jenkins\\.jenkins\\jobs\\${env.JOB_NAME}\\builds\\${env.BUILD_NUMBER}\\log\" \"${logFile}\""

                emailext (
                    to: 'salikramchadar@gmail.com',
                    subject: "DSA360 Build #${BUILD_NUMBER} - SUCCESS",
                    mimeType: 'text/html',
                    body: """
                        <h2 style="color:green;">Build Successful</h2>
                        <ul>
                            <li><strong>Project:</strong> ${env.JOB_NAME}</li>
                            <li><strong>Build #:</strong> ${BUILD_NUMBER}</li>
                            <li><strong>Image:</strong> <code>${FULL_IMAGE}</code></li>
                            <li><strong>Git Commit:</strong> ${GIT_COMMIT_SHORT}</li>
                            <li><strong>Duration:</strong> ${currentBuild.durationString}</li>
                        </ul>
                        <p><a href="${env.BUILD_URL}">View in Jenkins</a></p>
                    """,
                    attachmentsPattern: "build-log-${BUILD_NUMBER}.txt"
                )
            }
        }

        failure {
            echo 'Pipeline failed!'
            script {
                def logFile = "${WORKSPACE}\\build-log-${BUILD_NUMBER}.txt"
                bat "copy \"C:\\ProgramData\\Jenkins\\.jenkins\\jobs\\${env.JOB_NAME}\\builds\\${env.BUILD_NUMBER}\\log\" \"${logFile}\""

                emailext (
                    to: 'salikramchadar@gmail.com',
                    subject: "DSA360 Build #${BUILD_NUMBER} - FAILED",
                    mimeType: 'text/html',
                    body: """
                        <h2 style="color:red;">Build Failed</h2>
                        <ul>
                            <li><strong>Project:</strong> ${env.JOB_NAME}</li>
                            <li><strong>Build #:</strong> ${BUILD_NUMBER}</li>
                            <li><strong>Stage Failed:</strong> ${currentBuild.currentResult}</li>
                        </ul>
                        <p><a href="${env.BUILD_URL}">Fix it here</a></p>
                    """,
                    attachLog: true
                )
            }
        }
    }
}