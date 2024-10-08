pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '30', artifactNumToKeepStr: '30'))
    }

    environment {
        EMAIL_TO = credentials('my-email')
        DOCKER_TOKEN = credentials('docker-push-secret')
        DOCKER_USER = credentials('docker-username')
        DOCKER_SERVER = 'ghcr.io'
        DOCKER_PREFIX_BACKEND = 'ghcr.io/alexandrosmanolis/farmercompensation-spring'
        DOCKER_PREFIX_FRONTEND = 'ghcr.io/zenxaris03/devops-vue'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'git@github.com:AlexandrosManolis/farmerCompensation.git'
            }
        }
        // stage('Test') {
        //     steps {
        //         sh './mvnw test'
        //     }
        // }
        // stage('Docker build and push') {
        //     steps {
        //         sh '''
        //             HEAD_COMMIT=$(git rev-parse --short HEAD)
        //             TAG=$HEAD_COMMIT-$BUILD_ID
        //             docker build --rm -t $DOCKER_PREFIX:$TAG -t $DOCKER_PREFIX:latest  -f nonroot.Dockerfile .
        //             echo $DOCKER_TOKEN | docker login $DOCKER_SERVER -u $DOCKER_USER --password-stdin
        //             docker push $DOCKER_PREFIX --all-tags
        //         '''
        //     }
        // }

        stage('Create .dockerconfig.json') {
            steps {
                script {
                    // Generate the Base64-encoded auth string using a shell command
                    def authString = sh(
                        script: "echo -n '${DOCKER_USER}:${DOCKER_TOKEN}' | base64 -w 0",
                        returnStdout: true
                    ).trim()

                    // Create the content of .dockerconfig.json
                    def dockerConfigContent = """
                    {
                        "auths": {
                            "https://${DOCKER_SERVER}": {
                                "username": "${DOCKER_USER}",
                                "password": "${DOCKER_TOKEN}",
                                "email": "${EMAIL_TO}",
                                "auth": "${authString}"
                            }
                        }
                    }
                    """

                    // Define the path for .dockerconfig.json
                    def dockerConfigPath = "${WORKSPACE}/k8s/.dockerconfig.json"

                    // Write the content to .dockerconfig.json
                    writeFile file: dockerConfigPath, text: dockerConfigContent

                    // Create or update the Kubernetes secret
                    sh '''                    
                    kubectl create secret docker-registry registry-credentials --from-file=.dockerconfigjson=k8s/.dockerconfig.json --dry-run=client -o yaml | kubectl apply -f -
                    '''

                    // Print the path to the console
                    echo "The .dockerconfig.json file has been created at: ${dockerConfigPath}"
                }
            }
        }

        stage('deploy to k8s') {
            steps {
                sh '''
                    HEAD_COMMIT=$(git rev-parse --short HEAD)
                    TAG=latest
                    # if we had multiple configurations in kubeconfig file, we should select the correct one
                    # kubectl config use-context devops
                    

                    # Install cert-manager and wait for it to be ready
                    kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.15.3/cert-manager.yaml
                    kubectl wait --for=condition=available --timeout=2m -n cert-manager deployment/cert-manager

                    # Apply the cert-issuer configuration
                    kubectl apply -f k8s/cert/cert-issuer.yaml

                    # Apply the certificate for spring production
                    kubectl apply -f k8s/cert/certificate.yaml

                    
                    kubectl apply -f k8s/postgres/postgres-pvc.yaml
                    kubectl apply -f k8s/postgres/postgres-deployment.yaml
                    kubectl apply -f k8s/postgres/postgres-svc.yaml

                    kubectl apply -f k8s/mailhog/mailhog-deployment.yaml
                    kubectl apply -f k8s/mailhog/mailhog-svc.yaml

                    kubectl apply -f k8s/spring/spring-deployment.yaml
                    kubectl apply -f k8s/spring/spring-svc.yaml
                    
                    kubectl apply -f k8s/vue/vue-deployment.yaml
                    kubectl apply -f k8s/vue/vue-spring-mailhog-ingress-tls.yaml
                    kubectl apply -f k8s/vue/vue-svc.yaml

                    #kubectl set image deployment/postgres-deployment postgres=postgres:$TAG
                    #kubectl set image deployment/mailhog-deployment mailhog=mailhog:$TAG
                    kubectl set image deployment/spring-deployment spring=$DOCKER_PREFIX_BACKEND:$TAG
                    #kubectl set image deployment/vue-deployment vue=$DOCKER_PREFIX_FRONTEND:$TAG
                    
                    #kubectl rollout status deployment/postgres-deployment --watch --timeout=2m
                    #kubectl rollout status deployment/mailhog-deployment --watch --timeout=2m
                    #kubectl rollout status deployment/spring-deployment --watch --timeout=2m
                    #kubectl rollout status deployment/vue-deployment --watch --timeout=2m
                '''
            }
        }

    }
}