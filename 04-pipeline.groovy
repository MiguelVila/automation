def GIT_URL='github.com/MiguelVila/automation.git'
def GIT_BRANCH='main'

pipeline {
    agent any
    
    parameters {
        choice(name: 'ENV_TARGET', choices: ['dev', 'qa'], description: 'Selecciona el ambiente a apagar')
    }
    
    stages {
        stage('Git Checkout') {
            steps {
                script 
                {
                git branch: "$GIT_BRANCH", changelog: false, poll: false, url: "https://$GIT_URL"
                }  
            }
        }
        stage('Conditional stages dev') {
            steps {
                withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
                    def kubeconfigPath = sh(script: 'echo $CRED_FILE', returnStdout: true).trim()
                    script {
                        if (params.ENV_TARGET == 'dev') 
                        {
                        sh "ansible-galaxy collection install kubernetes.core"
                        sh "ansible-playbook -i localhost 12-playbook.yml"
                        } else if (params.ENV_TARGET == 'qa') 
                        {
                        sh "ansible-playbook -i localhost 12-playbook.yml"
                        }
                    }
                }
            }
        }
    }
}