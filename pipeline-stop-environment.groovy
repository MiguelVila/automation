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
        stage('Obtain templates') {
            steps {
                script 
                {
                git branch: "$GIT_BRANCH", changelog: false, poll: false, url: "https://$GIT_URL"
                }  
            }
        }
        stage('Drain Nodes') {
            steps {
                withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
                    script {
                        if (params.ENV_TARGET == 'dev') 
                        {
                            def fileContents = readFile('nodos-dev.txt') // Leer el contenido del archivo
                            def nodes = fileContents.readLines() // Obtener una lista de nodos a partir de las líneas del archivo
                            for (node in nodes) {
                                sh "kubectl drain ${node} --ignore-daemonsets --delete-local-data"
                            }

                        } else if (params.ENV_TARGET == 'qa') 
                        {
                            def fileContents = readFile('nodos-qa.txt') // Leer el contenido del archivo
                            def nodes = fileContents.readLines() // Obtener una lista de nodos a partir de las líneas del archivo
                            for (node in nodes) {
                                sh "kubectl drain ${node} --ignore-daemonsets --delete-local-data"
                            }
                        }
                    }
                }
            }
        }
        stage('shutdown Environment'){
            steps {
                script {
                    if (params.ENV_TARGET == 'dev') 
                    {
                        sh "ansible-playbook -i localhost playbook-stop-environment.yml"
                    } else if (params.ENV_TARGET == 'qa') 
                    {
                        sh "ansible-playbook -i localhost playbook-stop-environment.yml"
                    }
                }
            }
        }
    }
}