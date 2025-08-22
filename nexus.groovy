def publish(Map config) {
    script {
        def zipFile = config.zipFile
        def nexusPath = config.nexusPath        // e.g., 'BASE/my-project/main'
        def credentialsId = config.credentialsId

        def nexusBaseUrl = "http://bo.rs2.com:8081"

        def versionIdentifier = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
        
        def versionedUrl = "${nexusBaseUrl}/repository/${nexusPath}/${versionIdentifier}/${zipFile}"
        def latestUrl = "${nexusBaseUrl}/repository/${nexusPath}/latest/${zipFile}"

        withCredentials([usernamePassword(credentialsId: credentialsId, usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
            echo "Uploading VERSIONED artifact to: ${versionedUrl}"
            sh "curl -v -f -u \"\$NEXUS_USER:\$NEXUS_PASS\" --upload-file ${zipFile} \"${versionedUrl}\""

            echo "Uploading LATEST artifact to: ${latestUrl}"
            sh "curl -v -f -u \"\$NEXUS_USER:\$NEXUS_PASS\" --upload-file ${zipFile} \"${latestUrl}\""
        }
        
        sh "rm -f ${zipFile}"
    }
}

def download(Map config) {
    script {
        def zipFile = config.zipFile
        def nexusPath = config.nexusPath
        def credentialsId = config.credentialsId
        
        def nexusBaseUrl = "http://bo.rs2.com:8081"

        def fullDownloadUrl = "${nexusBaseUrl}/repository/${nexusPath}/${zipFile}"
        
        withCredentials([usernamePassword(credentialsId: credentialsId, usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
            sh "curl -v -f -u \"\$NEXUS_USER:\$NEXUS_PASS\" -o \"${zipFile}\" \"${fullDownloadUrl}\""
        }
    }
}

return this
