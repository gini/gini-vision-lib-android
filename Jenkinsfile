pipeline {
    agent any
    stages {
            stage('Test: list global variables') { 
                steps { 
                    echo "${env.BRANCH_NAME}"
                    echo "${env.CHANGE_ID}"
                    echo "${env.CHANGE_URL}"
                    echo "${env.CHANGE_TITLE}"
                    echo "${env.CHANGE_AUTHOR}"
                    echo "${env.CHANGE_AUTHOR_DISPLAY_NAME}"
                    echo "${env.CHANGE_AUTHOR_EMAIL}"
                    echo "${env.CHANGE_TARGET}"
                    echo "${env.BUILD_NUMBER}"
                    echo "${env.BUILD_TAG}"
                    echo "${env.JOB_NAME}"
                    echo "${env.JOB_BASE_NAME}"
                    echo "${env.BUILD_DISPLAY_NAME}"
                }
            }
        }
}
