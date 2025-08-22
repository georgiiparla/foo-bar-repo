def nexus

pipeline {
    agent any
    
    stages {
        stage('first stage') {
            steps {
                cleanWs()
                checkout scm
                echo "Hello world"
                sh 'echo "This is a sample file for the artifact MAIN" > sample.txt'
                
                script {
                    nexus = load 'nexus.groovy'
                }
            }
        }

        stage('download from nexus') {
            steps {
                script {
                    nexus.download(
                        zipFile: 'base_bin.zip',
                        nexusPath: 'BASE/CS-Katana-lin/main/latest',
                        credentialsId: '7d196d2f-f3c1-4803-bde9-2d17d18776b3'
                    )
                }
            }
        }

        stage('last stage') {
            steps {
                script {
                    sh "mkdir -p Bin"
                    sh "unzip -o base_bin.zip -d Bin/"
                    sh "ls -la"
                    sh 'zip -r foo-bar.zip sample.txt Bin/'
                    
                    nexus.publish(
                        zipFile: 'foo-bar.zip',
                        nexusPath: 'katana/main',
                        credentialsId: '7d196d2f-f3c1-4803-bde9-2d17d18776b3'
                    )
                }
            }
        }
    }
}
