def publish(Map config) {
    script {
        def zipFile = config.zipFile
        def repo = config.repoName
        def nexusRepoUrl = "http://172.20.10.25:8081/repository/${repo}"
        def credentialsId = config.credentialsId

        def rawBranch = env.GIT_BRANCH
        def currentBranch = rawBranch.replaceFirst('origin/', '')
        def nexusPath
        if (currentBranch == 'main') {
            nexusPath = 'main'
        } else {
            nexusPath = currentBranch.replaceAll('/', '-')
        }
        
        def versionIdentifier = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
        
        def versionedUrl = "${nexusRepoUrl}/${nexusPath}/${versionIdentifier}/${zipFile}"
        def latestUrl = "${nexusRepoUrl}/${nexusPath}/latest/${zipFile}"

        withCredentials([usernamePassword(credentialsId: credentialsId, usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
            echo "Uploading VERSIONED artifact to: ${versionedUrl}"
            sh """
                curl -v -f -u "\$NEXUS_USER:\$NEXUS_PASS" \\
                     --upload-file ${zipFile} \\
                     "${versionedUrl}"
            """

            echo "Uploading LATEST artifact to: ${latestUrl}"
            sh """
                curl -v -f -u "\$NEXUS_USER:\$NEXUS_PASS" \\
                     --upload-file ${zipFile} \\
                     "${latestUrl}"
            """
        }
        
        echo "Cleaning up local file: ${zipFile}"
        sh "rm -f ${zipFile}"
    }
}

def download(Map config) {
    script {
        def zipFile = config.zipFile
        def repo = config.repoPath
        def nexusRepoUrl = "http://172.20.10.25:8081/repository/${repo}/${zipFile}"
        def credentialsId = config.credentialsId

        withCredentials([usernamePassword(credentialsId: credentialsId, usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
            sh """
                curl -v -f -u "\$NEXUS_USER:\$NEXUS_PASS" -o "${zipFile}" "${nexusRepoUrl}"
            """
        }
    }
}

return this
