pipeline {
    agent any
    
    stages {
        stage('first stage') {
            steps {
                cleanWs()
                checkout scm
                echo "Hello world"
                sh 'echo "This is a sample file for the artifact BOV" > sample.txt'
            }
        }

        stage('upload to nexus') {
            steps {
                script {
                    def downloader = load 'nexusPublisher.groovy'
                    downloader.download(zipFile: 'base_bin.zip', repoPath: 'BASE/bov/latest', credentialsId: '7d196d2f-f3c1-4803-bde9-2d17d18776b3')
                }
            }
        }

        stage('last stage') {
            steps {
                script {
                    def publisher = load 'nexus.groovy'
                    sh "mkdir -p Bin"
                    sh "unzip -o base_bin.zip -d Bin/"
                    sh "ls -la"
                    sh 'zip -r foo-bar.zip sample.txt Bin/'
                    publisher.publish(zipFile: 'foo-bar.zip', repoName: 'katana', credentialsId: '7d196d2f-f3c1-4803-bde9-2d17d18776b3')
                }
            }
        }
    }
}
