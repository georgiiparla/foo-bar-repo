pipeline {
    agent any

    stage('first stage') {
        steps {
            checkout scm
            echo "Hello world"
            sh 'echo "This is a sample file for the artifact." > sample.txt'
            sh 'zip foo-bar.zip sample.txt'
        }
    }

    stage('upload to nexus') {
        steps {
            script {
                def publisher = load 'nexusPublisher.groovy'
                publisher.publish(zipFile: 'foo-bar.zip', nexusRepoUrl: 'http://172.20.10.25:8081/repository/katana', credentialsId: '7d196d2f-f3c1-4803-bde9-2d17d18776b3')
            }
        }
    }

    stage('last stage') {
        steps {
            echo "Bye world"
        }
    }
}
