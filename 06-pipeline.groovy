def GIT_URL='github.com/MiguelVila/automation.git'
def GIT_BRANCH='main'

pipeline {
    agent any
    
    parameters {
        choice(name: 'ENV_TARGET', choices: ['dev', 'qa'], description: 'Selecciona el ambiente a apagar')
    }
    environment {
        PATH = "/var/jenkins_home/:$PATH"
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
        stage('Crear archivo .aws/credentials') {
            steps {
                script {
                    def credentialsContent = '''
                        [default]
                        aws_access_key_id = AKIAWLHLQVRVPNWV3LYU
                        aws_secret_access_key = 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
                    '''
                    writeFile file: '.aws/credentials', text: credentialsContent
                }
            }
        }
        stage('Apagado de ambiente') {
            steps {
                withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
                    script {
                        if (params.ENV_TARGET == 'dev') 
                        {
                            def fileContents = readFile('nodos-dev.txt') // Leer el contenido del archivo
                            def nodes = fileContents.readLines() // Obtener una lista de nodos a partir de las líneas del archivo
                            sh "echo variables listas"
                            
                            for (node in nodes) {
                                sh "echo Ejecutando comando: kubectl drain ${node}"
                                sh "kubectl drain ${node} --ignore-daemonsets --delete-local-data"
                            }
                            AWS_SHARED_CREDENTIALS_FILE = "$PWD/.aws/credentials"
                            sh "cat .aws/credentials"
                            sh "echo Ejecutando playbook de apago de instancia dev"
                            sh "ansible-playbook -i localhost 04-playbook.yml"
                        } else if (params.ENV_TARGET == 'qa') 
                        {
                            sh "echo Ejecutando playbook de apago de instancia qa"
                            sh "ansible-playbook -i localhost 04-playbook.yml"
                        }
                    }
                }
            }
        }
    }
}

def GIT_URL='github.com/MiguelVila/automation.git'
def GIT_BRANCH='main'

pipeline {
    agent any
    
    parameters {
        choice(name: 'ENV_TARGET', choices: ['dev', 'qa'], description: 'Selecciona el ambiente a apagar')
    }
    environment {
        PATH = "/var/jenkins_home/:$PATH"
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
        stage('Drenar nodos') {
            steps {
                withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
                    script {
                        if (params.ENV_TARGET == 'dev') 
                        {
                            def fileContents = readFile('nodos-dev.txt')
                            def nodes = fileContents.readLines()
                            sh "echo variables listas"
                            
                            for (node in nodes) {
                                sh "echo Ejecutando comando: kubectl drain ${node}"
                                sh "kubectl drain ${node} --ignore-daemonsets --delete-local-data"
                            }
                            def AWS_SHARED_CREDENTIALS_FILE = ".aws/credentials"
                            sh "cat .aws/credentials"
                            sh "echo Ejecutando playbook de apago de instancia dev"
                            sh "ansible-playbook -i localhost 04-playbook.yml"
                        } else if (params.ENV_TARGET == 'qa') 
                        {
                            sh "echo Ejecutando playbook de apago de instancia qa"
                            sh "ansible-playbook -i localhost 04-playbook.yml"
                        }
                    }
                }
            }
        }
        stage('Ejecutar playbook de Ansible') {
            steps {
                withAWS(credentials: 'aws-creds', region: 'us-east-1') {
                    script {
                        if (params.ENV_TARGET == 'dev') 
                        {
                            sh "echo Ejecutando playbook de apago de instancia dev"
                            sh "ansible-playbook -i localhost 04-playbook.yml"
                        } else if (params.ENV_TARGET == 'qa') 
                        {
                            sh "echo Ejecutando playbook de apago de instancia qa"
                            sh "ansible-playbook -i localhost 04-playbook.yml"
                        }
                    }
                }
            }
        }
    }
}