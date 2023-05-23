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
                                sh "kubectl drain ${node}"
                            }
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