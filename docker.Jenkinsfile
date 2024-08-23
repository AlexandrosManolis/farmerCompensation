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
        stage('Test') {
            steps {
                sh './mvnw test'
            }
        }
        
        stage('Docker build and push') {
            steps {
                sh '''
                    HEAD_COMMIT=$(git rev-parse --short HEAD)
                    TAG=$HEAD_COMMIT-$BUILD_ID
                    docker build --rm -t $DOCKER_PREFIX:$TAG -t $DOCKER_PREFIX:latest  -f nonroot.Dockerfile .
                    echo $DOCKER_TOKEN | docker login $DOCKER_SERVER -u $DOCKER_USER --password-stdin
                    docker push $DOCKER_PREFIX --all-tags
                '''
            }
        }
        stage('run ansible pipeline') {
            steps {
                build job: 'ansible'
            }
        }
        stage('Add docker-server to known-hosts and install project with docker compose') {
                    steps {
                        sh '''
                            ssh-keyscan -H "$(awk '/docker-server:/ {getline; if ($1 == "ansible_host:") print $2}' ~/workspace/ansible/hosts.yaml | xargs -I {} grep -A 1 "Host {}" ~/.ssh/config | awk -F ' ' '/HostName|Hostname/ {print $2}')">> ~/.ssh/known_hosts
                            export ANSIBLE_CONFIG=~/workspace/ansible/ansible.cfg
                            ansible-playbook -i ~/workspace/ansible/hosts.yaml -l docker-server ~/workspace/ansible/playbooks/spring-vue-docker.yaml
                        '''
                    }
         }
    }

    post {
        always {
            mail
                to: "${EMAIL_TO}",
                subject: "JENKINS: Project name -> ${env.JOB_NAME}, Build -> ${currentBuild.currentResult}",
                body: """Project ${env.JOB_NAME} <br>
                        Build status: ${currentBuild.currentResult} <br>
                        Build Number: ${env.BUILD_NUMBER} <br>
                        Build URL: ${env.BUILD_URL}"""            
        }
    }
}