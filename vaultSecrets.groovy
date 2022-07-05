import groovy.json.JsonOutput

def call(String path, String token, boolean asString=false) {
    def rawOut
    try {
        if (isUnix()) {
            rawOut = readJSON text: (
                sh ( returnStdout: true, script:  
                    """
                        set +x
                        curl -H "X-Vault-Token: ${token}" -H "Accept: application/json" -X GET "https://${path}" 
                        set -x
                    """
                )
            )
        }
        else {
            rawOut = readJSON text: (
                powershell ( returnStdout: true, script: 
                    """
                        [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
                        \$header = @{"X-Vault-Token"="${token}";"Accept"="application/json"}
                        invoke-RestMethod -Uri https://${path} -Headers \${header} | ConvertTo-Json
                    """
                )
            )
        }       
    } catch (err) {
        println err
        return 'no secrets to provide'
    }
    return asString ? JsonOutput.toJson(rawOut.data) : rawOut.data
}
