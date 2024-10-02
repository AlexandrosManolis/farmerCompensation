pipeline {
    agent any

    environment {
        EMAIL_TO = credentials('my-email')
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
        stage('Install postgres (Add Server to known_hosts)') {
            steps {
                sh '''
                    ssh-keyscan -H "$(awk '/azure-db-server:/ {getline; if ($1 == "ansible_host:") print $2}' ~/workspace/ansible/hosts.yaml | xargs -I {} grep -A 1 "Host {}" ~/.ssh/config | awk -F ' ' '/HostName|Hostname/ {print $2}')">> ~/.ssh/known_hosts
                    export ANSIBLE_CONFIG=~/workspace/ansible/ansible.cfg
                    #run ansible for postgres
                    ansible-playbook -i ~/workspace/ansible/hosts.yaml -l azure-db-server ~/workspace/ansible/playbooks/postgres.yaml  
                '''
            }
        }

        stage('Deploy spring boot app (Add Server to known_hosts)') {
            steps {
                sh '''
                    ssh-keyscan -H "$(awk '/gcloud-app-server:/ {getline; if ($1 == "ansible_host:") print $2}' ~/workspace/ansible/hosts.yaml | xargs -I {} grep -A 1 "Host {}" ~/.ssh/config | awk -F ' ' '/HostName|Hostname/ {print $2}')">> ~/.ssh/known_hosts
                    export ANSIBLE_CONFIG=~/workspace/ansible/ansible.cfg
                    #run ansible for backend
                    ansible-playbook -i ~/workspace/ansible/hosts.yaml -l gcloud-app-server ~/workspace/ansible/playbooks/spring.yaml
                '''
            }
        }
       stage('Deploy frontend (Add Server to known_hosts)') {
            steps {
                sh '''
                    ssh-keyscan -H "$(awk '/frontend-vm:/ {getline; if ($1 == "ansible_host:") print $2}' ~/workspace/ansible/hosts.yaml | xargs -I {} grep -A 1 "Host {}" ~/.ssh/config | awk -F ' ' '/HostName|Hostname/ {print $2}')">> ~/.ssh/known_hosts
                    export ANSIBLE_CONFIG=~/workspace/ansible/ansible.cfg
                    #run ansible for frontend
                    ansible-playbook -i ~/workspace/ansible/hosts.yaml -l frontend-vm ~/workspace/ansible/playbooks/vuejs.yaml
                '''
            }
       }
    }
}