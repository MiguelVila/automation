
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
                script {
                git branch: "$GIT_BRANCH", changelog: false, poll: false, url: "https://$GIT_URL"
                def mavenPom = readMavenPom file: 'pom.xml'
                }  
            }
        }
        stage('Conditional stages') {
            when {
                expression { params.ENV_TARGET == 'dev' }
            }
            steps {
                sh "ansible-playbook -i localhost 04-playbook.yml"
            }
        }
        stage('Conditional stages') {
            when {
                expression { params.ENV_TARGET == 'qa' }
            }
            steps {
                sh "ansible-playbook -i localhost 04-playbook.yml"
            }
        }
    }
}

pipeline {
    agent any

    stages {
        stage('Ejecutar playbook de Ansible') {
            steps {
                withCredentials([file(credentialsId: 'ID_DEL_SECRETO', variable: 'CRED_FILE')]) {
                    script {
                        def kubeconfigPath = sh(script: 'echo $CRED_FILE', returnStdout: true).trim()

                        ansiblePlaybook(
                            playbook: 'path/to/playbook.yml',
                            inventory: 'localhost',
                            extraVars: [
                                kubernetes_context: params.KUBERNETES_CONTEXT
                            ],
                            credentialsId: kubeconfigPath
                        )
                    }
                }
            }
        }
    }
}
