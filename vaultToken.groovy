def call(String role, String auth, String secret) {
    withCredentials([
        string(credentialsId: "${secret}", variable: 'SECRET_ID'),
        string(credentialsId: "${role}", variable: 'ROLE')
    ]){
    if (isUnix()) {
        def rawOut = readJSON text: (
            sh ( returnStdout: true, script:  
                """
                    set +x
                    json='{ "role_id":"'"${ROLE}"'", "secret_id":"'"${SECRET_ID}"'" }'
                    curl --request POST --data "\$json" "https://${auth}"
                    set -x
        		"""
            )
        )
        return  rawOut.auth.client_token
    }
    else {
        def rawOut = readJSON text: (
            powershell ( returnStdout: true, script: 
                """
                	[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
                	\$payload= @{\"role_id\"=\"${ROLE}\"; \"secret_id\"=\"${SECRET_ID}\"} |ConvertTo-Json
                	curl -method POST -body \$payload -uri https://${auth} | Select-Object -Expand Content
                """
            )
        )
        return  rawOut.auth.client_token
    }       
}
}
