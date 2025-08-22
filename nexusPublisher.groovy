def publish(Map config) {
    script {
        // --- 1. Get Parameters from the function call ---
        def zipFile = config.zipFile
        def nexusRepoUrl = config.nexusRepoUrl
        def credentialsId = config.credentialsId

        // --- 2. Determine Paths & Version (Logic is now inside the function) ---
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

        // --- 3. Upload to Nexus ---
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
        
        // --- 4. Clean up the local zip file ---
        echo "Cleaning up local file: ${zipFile}"
        sh "rm -f ${zipFile}"
    }
}

// This line is essential for the 'load' step to work
return this
