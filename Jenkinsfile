pipeline {
    agent any

    // environment {
    //     EMAIL_TO = "enter-your-email" //your email
    // }

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
        stage('run ansible pipeline') {
            steps {
                build job: 'ansible'
            }
        }
        stage('install ansible prerequisites') {
            steps {
                sh '''
                    ansible-galaxy install geerlingguy.postgresql
                '''
            }
        }
        stage('Add Servers to known_hosts') {
            steps {
                sh '''
                    # List of servers to add to known_hosts
                    servers="azure-db-server gcloud-app-server frontend-vm"

                    for server in $servers; do
                        ssh-keyscan -H "$(grep -A 1 '$server:' ~/workspace/ansible/hosts.yaml | grep 'ansible_host' | awk '{print $2}' | xargs -I {} sh -c 'grep -A 1 -w "Host {}" ~/.ssh/config | grep -i "HostName\|Hostname" | awk "{print \$2}"')" >> ~/.ssh/known_hosts
                    done
                '''
            }
        }
        stage('Install postgres') {
            steps {
                sh '''
                    export ANSIBLE_CONFIG=~/workspace/ansible/ansible.cfg
                    #run ansible for postgres
                    ansible-playbook -i ~/workspace/ansible/hosts.yaml -l azure-db-server ~/workspace/ansible/playbooks/postgres.yaml  
                '''
            }
        }

        stage('Deploy spring boot app') {
            steps {
                sh '''
                    export ANSIBLE_CONFIG=~/workspace/ansible/ansible.cfg
                    #run ansible for backend
                    ansible-playbook -i ~/workspace/ansible/hosts.yaml -l gcloud-app-server ~/workspace/ansible/playbooks/spring.yaml
                '''
            }
        }
       stage('Deploy frontend') {
            steps {
                sh '''
                    export ANSIBLE_CONFIG=~/workspace/ansible/ansible.cfg
                    #run ansible for frontend
                    ansible-playbook -i ~/workspace/ansible/hosts.yaml -l frontend-vm ~/workspace/ansible/playbooks/vuejs.yaml
                '''
            }
       }
    }

    // post {
    //     always {
    //         mail  to: "${EMAIL_TO}", body: "Project ${env.JOB_NAME} <br>, Build status ${currentBuild.currentResult} <br> Build Number: ${env.BUILD_NUMBER} <br> Build URL: ${env.BUILD_URL}", subject: "JENKINS: Project name -> ${env.JOB_NAME}, Build -> ${currentBuild.currentResult}"
    //     }
    // }
}