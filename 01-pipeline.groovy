pipeline {
    agent any

    parameters {
#        string(name: 'TARGET_IPS', description: 'Target IP addresses (separated by hyphens)')
        choice(name: 'TARGET_IPS', choices: ['192.168.0.1', '192.168.0.2'], description: 'Select target IP addresses')
    }

    stages {
        stage('Guardar parámetros en archivo') {
            steps {
                script {
                    def parametros = [
                        'TARGET_IPS': params.TARGET_IPS
                    ]
                    writeFile file: 'parametros.txt', text: parametros.toString()
                }
            }
        }

        stage('Conditional stages') {
            when {
                expression { params.TARGET_IPS == '192.168.0.1' }
            }
            steps {
                // Etapas específicas para 192.168.0.1
                // Agrega aquí las etapas que deseas ejecutar cuando se seleccione 192.168.0.1
            }
        }

        stage('Conditional stages') {
            when {
                expression { params.TARGET_IPS == '192.168.0.2' }
            }
            steps {
                // Etapas específicas para 192.168.0.2
                // Agrega aquí las etapas que deseas ejecutar cuando se seleccione 192.168.0.2
            }
        }
    }
}
