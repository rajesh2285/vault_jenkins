import groovy.json.JsonOutput
import java.net.URLEncoder
import hudson.model.Result
import jenkins.model.*

def VAULT_ROLE = 'vault_role_credential_name_in_jenkins'
def VAULT_HOST = 'vault.app.com'
def VAULT_AUTH = "${VAULT_HOST}" + '/v1/auth/approle/login'
def SECRET_ID = 'vault_secret_id_credential_name_in_jenkins'
def VAULT_PATH = "${VAULT_HOST}" + '/v1/secret/xxxxx/xxxxx'

pipeline {
    agent {
        node {
            label "master"
        }
    }
    environment {
        VAULT_TOKEN = vaultToken("${VAULT_ROLE}", "${VAULT_AUTH}", "${SECRET_ID}")
    }
    stages {
        stage('Build') {
            steps {
                    script {

                    cred = vaultSecrets("${VAULT_PATH}", "${VAULT_TOKEN}")

                    env.username = cred."pcf_nxus_uname"
                    env.nxus_pswd = cred."pcf_nxus"
                    env.sonar_pswd = cred."snr_cred"
                    env.pcf_pwd = cred."pcf_prpd"
                }
            }
        }
    }
}
