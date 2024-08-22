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
        DOCKER_PREFIX = 'ghcr.io/alexandrosmanolis/farmercompensation-spring'
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

        sstage('Create .dockerconfig.json') {
            steps {
                script {
                    // Generate the Base64-encoded auth string using a shell command
                    def authString = sh(
                        script: "echo -n '${DOCKER_USER}:${DOCKER_TOKEN}' | base64",
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
                    def dockerConfigPath = "${WORKSPACE}/k8s-application/k8s/.dockerconfig.json"

                    // Write the content to .dockerconfig.json
                    writeFile file: dockerConfigPath, text: dockerConfigContent
                }
            }
        }

        stage('deploy to k8s') {
            steps {
                sh '''
                    HEAD_COMMIT=$(git rev-parse --short HEAD)
                    TAG=$HEAD_COMMIT-$BUILD_ID
                    # if we had multiple configurations in kubeconfig file, we should select the correct one
                    # kubectl config use-context devops
                    
                    cd
                    
                    ./kubectl apply -f ~/workspace/k8s-application/k8s/postgres/postgres-pvc.yaml
                    ./kubectl apply -f ~/workspace/k8s-application/k8s/postgres/postgres-deployment.yaml
                    ./kubectl apply -f ~/workspace/k8s-application/k8s/postgres/postgres-svc.yaml

                    ./kubectl apply -f ~/workspace/k8s-application/k8s/spring/spring-deployment.yaml
                    ./kubectl apply -f ~/workspace/k8s-application/k8s/spring/spring-ingress.yaml
                    ./kubectl apply -f ~/workspace/k8s-application/k8s/spring/spring-svc.yaml
                    
                    ./kubectl apply -f ~/workspace/k8s-application/k8s/vue/vue-deployment.yaml
                    ./kubectl apply -f ~/workspace/k8s-application/k8s/vue/vue-ingress.yaml
                    ./kubectl apply -f ~/workspace/k8s-application/k8s/vue/vue-svc.yaml

                    ./kubectl set image deployment/postgres-deployment postgres=$DOCKER_PREFIX:$TAG
                    ./kubectl set image deployment/spring-deployment spring=$DOCKER_PREFIX:$TAG
                    ./kubectl set image deployment/vue-deployment vue=$DOCKER_PREFIX:$TAG
                    
                    #./kubectl rollout status deployment/postgres-deployment --watch --timeout=2m
                    #./kubectl rollout status deployment/spring-deployment --watch --timeout=2m
                    #./kubectl rollout status deployment/vue-deployment --watch --timeout=2m
                '''
            }
        }

    }
}