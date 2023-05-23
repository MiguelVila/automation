pipeline {
    agent any

    parameters {
        choice(name: 'ENV_TARGET', choices: ['dev', 'qa'], description: 'Selecciona el ambiente a apagar')
    }

    stages {
        stage('Guardar parámetros en archivo') {
            steps {
                script {
                    def parametros = [
                        'ENV_TARGET': params.ENV_TARGET
                    ]
                    writeFile file: 'parametros.txt', text: parametros.toString()
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
