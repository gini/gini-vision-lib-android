#!/usr/bin/env groovy
pipeline {
    agent any
    environment {
        NEXUS_MAVEN = credentials('external-nexus-maven-repo-credentials')
        GIT = credentials('github')
        COMPONENT_API_EXAMPLE_APP_KEYSTORE_PSW = credentials('component-api-example-app-release-keystore-password')
        COMPONENT_API_EXAMPLE_APP_KEY_PSW = credentials('component-api-example-app-release-key-password')
        SCREEN_API_EXAMPLE_APP_KEYSTORE_PSW = credentials('screen-api-example-app-release-keystore-password')
        SCREEN_API_EXAMPLE_APP_KEY_PSW = credentials('screen-api-example-app-release-key-password')
        EXAMPLE_APP_CLIENT_CREDENTIALS = credentials('gini-api-client-credentials')
        COMPONENT_API_EXAMPLE_APP_HOCKEYAPP_API_TOKEN = credentials('component-api-example-app-hockeyapp-api-token')
        SCREEN_API_EXAMPLE_APP_HOCKEYAPP_API_TOKEN = credentials('screen-api-example-app-hockeyapp-api-token')
    }
    stages {
        stage('Build') {
            when {
                anyOf {
                    not {
                        branch 'master'
                    }
                    allOf {
                        branch 'master'
                        expression {
                            def tag = sh(returnStdout: true, script: 'git tag --contains $(git rev-parse HEAD)').trim()
                            return !tag.isEmpty()
                        }
                    }
                }
            }
            steps {
                sh './gradlew ginivision:clean ginivision:assembleDebug ginivision:assembleRelease'
            }
        }
        stage('Unit Tests') {
            when {
                anyOf {
                    not {
                        branch 'master'
                    }
                    allOf {
                        branch 'master'
                        expression {
                            def tag = sh(returnStdout: true, script: 'git tag --contains $(git rev-parse HEAD)').trim()
                            return !tag.isEmpty()
                        }
                    }
                }
            }
            steps {
                sh './gradlew ginivision:test'
            }
            post {
                always {
                    publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'ginivision/build/reports/tests/testDebugUnitTest', reportFiles: 'index.html', reportName: 'Unit Test Results', reportTitles: ''])
                }
            }
        }
        stage('Create AVDs') {
            when {
                anyOf {
                    not {
                        branch 'master'
                    }
                    allOf {
                        branch 'master'
                        expression {
                            def tag = sh(returnStdout: true, script: 'git tag --contains $(git rev-parse HEAD)').trim()
                            return !tag.isEmpty()
                        }
                    }
                }
            }
            steps {
                withEnv(["PATH+TOOLS=$ANDROID_HOME/tools", "PATH+TOOLS_BIN=$ANDROID_HOME/tools/bin", "PATH+PLATFORM_TOOLS=$ANDROID_HOME/platform-tools"]) {
                    sh 'scripts/delete-corrupt-avds.sh'
                    sh 'scripts/create-avd-for-device.sh api-25-nexus-5x "system-images;android-25;google_apis;x86" "Nexus 5X" || true'
                    sh 'scripts/create-avd-for-device.sh api-25-nexus-9 "system-images;android-25;google_apis;x86" "Nexus 9" || true'
                }
            }
        }
        stage('Instrumentation Tests - Phone') {
            when {
                anyOf {
                    not {
                        branch 'master'
                    }
                    allOf {
                        branch 'master'
                        expression {
                            def tag = sh(returnStdout: true, script: 'git tag --contains $(git rev-parse HEAD)').trim()
                            return !tag.isEmpty()
                        }
                    }
                }
            }
            steps {
                withEnv(["PATH+TOOLS=$ANDROID_HOME/tools", "PATH+TOOLS_BIN=$ANDROID_HOME/tools/bin", "PATH+PLATFORM_TOOLS=$ANDROID_HOME/platform-tools"]) {
                    sh 'scripts/start-emulator-with-skin.sh "api-25-nexus-5x"_$(scripts/get-avd-name.sh) nexus_5x -prop persist.sys.language=en -prop persist.sys.country=US -no-snapshot-load -no-snapshot-save -gpu on -camera-back emulated > emulator_port'
                    sh 'emulator_port=$(cat emulator_port) && scripts/wait-for-emulator-to-boot.sh emulator-$emulator_port 20'
                    sh 'emulator_port=$(cat emulator_port) && ./gradlew ginivision:targetedDebugAndroidTest -PpackageName=net.gini.android.vision -PtestTarget=emulator-$emulator_port'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'ginivision/build/outputs/androidTest-results/targeted/*.xml'
                    withEnv(["PATH+PLATFORM_TOOLS=$ANDROID_HOME/platform-tools"]) {
                        sh 'emulator_port=$(cat emulator_port) && adb -s emulator-$emulator_port emu kill || true'
                        sh 'emulator_port=$(cat emulator_port) && scripts/wait-for-emulator-to-stop.sh emulator-$emulator_port 20'
                    }
                    sh 'rm emulator_port || true'
                }
            }
        }
        stage('Instrumentation Tests - Tablet') {
            when {
                anyOf {
                    not {
                        branch 'master'
                    }
                    allOf {
                        branch 'master'
                        expression {
                            def tag = sh(returnStdout: true, script: 'git tag --contains $(git rev-parse HEAD)').trim()
                            return !tag.isEmpty()
                        }
                    }
                }
            }
            steps {
                withEnv(["PATH+TOOLS=$ANDROID_HOME/tools", "PATH+TOOLS_BIN=$ANDROID_HOME/tools/bin", "PATH+PLATFORM_TOOLS=$ANDROID_HOME/platform-tools"]) {
                    sh 'scripts/start-emulator-with-skin.sh "api-25-nexus-9"_$(scripts/get-avd-name.sh) nexus_9 -prop persist.sys.language=en -prop persist.sys.country=US -no-snapshot-load -no-snapshot-save -gpu on -camera-back emulated > emulator_port'
                    sh 'emulator_port=$(cat emulator_port) && scripts/wait-for-emulator-to-boot.sh emulator-$emulator_port 20'
                    sh 'emulator_port=$(cat emulator_port) && ./gradlew ginivision:targetedDebugAndroidTest -PpackageName=net.gini.android.vision -PtestTarget=emulator-$emulator_port'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'ginivision/build/outputs/androidTest-results/targeted/*.xml'
                    withEnv(["PATH+PLATFORM_TOOLS=$ANDROID_HOME/platform-tools"]) {
                        sh 'emulator_port=$(cat emulator_port) && adb -s emulator-$emulator_port emu kill || true'
                        sh 'emulator_port=$(cat emulator_port) && scripts/wait-for-emulator-to-stop.sh emulator-$emulator_port 20'
                    }
                    sh 'rm emulator_port || true'
                }
            }
        }
        stage('Code Coverage') {
            when {
                anyOf {
                    not {
                        branch 'master'
                    }
                    allOf {
                        branch 'master'
                        expression {
                            def tag = sh(returnStdout: true, script: 'git tag --contains $(git rev-parse HEAD)').trim()
                            return !tag.isEmpty()
                        }
                    }
                }
            }
            steps {
                sh './gradlew ginivision:unifyTargetedTestCoverage ginivision:jacocoTestDebugUnitTestReport'
                publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'ginivision/build/reports/jacoco/jacocoTestDebugUnitTestReport/html', reportFiles: 'index.html', reportName: 'Code Coverage Report', reportTitles: ''])
            }
        }
        stage('Javadoc Coverage') {
            when {
                anyOf {
                    not {
                        branch 'master'
                    }
                    allOf {
                        branch 'master'
                        expression {
                            def tag = sh(returnStdout: true, script: 'git tag --contains $(git rev-parse HEAD)').trim()
                            return !tag.isEmpty()
                        }
                    }
                }
            }
            steps {
                sh './gradlew ginivision:generateJavadocCoverage'
                publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'ginivision/build/reports/javadoc-coverage', reportFiles: 'index.html', reportName: 'Javadoc Coverage Report', reportTitles: ''])
            }
        }
        stage('Code Analysis') {
            when {
                anyOf {
                    not {
                        branch 'master'
                    }
                    allOf {
                        branch 'master'
                        expression {
                            def tag = sh(returnStdout: true, script: 'git tag --contains $(git rev-parse HEAD)').trim()
                            return !tag.isEmpty()
                        }
                    }
                }
            }
            steps {
                sh './gradlew ginivision:lint ginivision:checkstyle ginivision:findbugs ginivision:pmd'
                androidLint canComputeNew: false, defaultEncoding: '', healthy: '', pattern: 'ginivision/build/reports/lint-results.xml', unHealthy: ''
                checkstyle canComputeNew: false, defaultEncoding: '', healthy: '', pattern: 'ginivision/build/reports/checkstyle/checkstyle.xml', unHealthy: ''
                findbugs canComputeNew: false, defaultEncoding: '', excludePattern: '', healthy: '', includePattern: '', pattern: 'ginivision/build/reports/findbugs/findbugs.xml', unHealthy: ''
                pmd canComputeNew: false, defaultEncoding: '', healthy: '', pattern: 'ginivision/build/reports/pmd/pmd.xml', unHealthy: ''
            }
        }
        stage('Build Documentation') {
            when {
                anyOf {
                    not {
                        branch 'master'
                    }
                    allOf {
                        branch 'master'
                        expression {
                            def tag = sh(returnStdout: true, script: 'git tag --contains $(git rev-parse HEAD)').trim()
                            return !tag.isEmpty()
                        }
                    }
                }
            }
            steps {
                withEnv(["PATH+=/usr/local/bin"]) {
                    sh 'scripts/build-sphinx-doc.sh'
                }
                publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'ginivision/src/doc/build/html', reportFiles: 'index.html', reportName: 'Documentation', reportTitles: ''])
            }
        }
        stage('Generate Javadoc') {
            when {
                anyOf {
                    not {
                        branch 'master'
                    }
                    allOf {
                        branch 'master'
                        expression {
                            def tag = sh(returnStdout: true, script: 'git tag --contains $(git rev-parse HEAD)').trim()
                            return !tag.isEmpty()
                        }
                    }
                }
            }
            steps {
                sh './gradlew ginivision:generateJavadoc'
                publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'ginivision/build/docs/javadoc', reportFiles: 'index.html', reportName: 'Javadoc', reportTitles: ''])
            }
        }
        stage('Archive Artifacts') {
            when {
                anyOf {
                    not {
                        branch 'master'
                    }
                    allOf {
                        branch 'master'
                        expression {
                            def tag = sh(returnStdout: true, script: 'git tag --contains $(git rev-parse HEAD)').trim()
                            return !tag.isEmpty()
                        }
                    }
                }
            }
            steps {
                sh 'cd ginivision/build/reports/jacoco/jacocoTestDebugUnitTestReport && zip -r testCoverage.zip html && cd -'
                sh 'cd ginivision/build/reports && zip -r javadocCoverage.zip javadoc-coverage && cd -'
                archiveArtifacts 'ginivision/build/outputs/aar/*.aar,ginivision/build/reports/jacoco/jacocoTestDebugUnitTestReport/testCoverage.zip,ginivision/build/reports/javadocCoverage.zip'
            }
        }
        stage('Build Example Apps') {
            when {
                anyOf {
                    not {
                        branch 'master'
                    }
                    allOf {
                        branch 'master'
                        expression {
                            def tag = sh(returnStdout: true, script: 'git tag --contains $(git rev-parse HEAD)').trim()
                            return !tag.isEmpty()
                        }
                    }
                }
            }
            steps {
                sh './gradlew screenapiexample::clean screenapiexample::insertClientCredentials screenapiexample::assembleRelease -PreleaseKeystoreFile=screen_api_example.jks -PreleaseKeystorePassword="$SCREEN_API_EXAMPLE_APP_KEYSTORE_PSW" -PreleaseKeyAlias=screen_api_example -PreleaseKeyPassword="$SCREEN_API_EXAMPLE_APP_KEY_PSW" -PclientId=$EXAMPLE_APP_CLIENT_CREDENTIALS_USR -PclientSecret=$EXAMPLE_APP_CLIENT_CREDENTIALS_PSW'
                sh './gradlew componentapiexample::clean componentapiexample::insertClientCredentials componentapiexample::assembleRelease -PreleaseKeystoreFile=component_api_example.jks -PreleaseKeystorePassword="$COMPONENT_API_EXAMPLE_APP_KEYSTORE_PSW" -PreleaseKeyAlias=component_api_example -PreleaseKeyPassword="$COMPONENT_API_EXAMPLE_APP_KEY_PSW" -PclientId=$EXAMPLE_APP_CLIENT_CREDENTIALS_USR -PclientSecret=$EXAMPLE_APP_CLIENT_CREDENTIALS_PSW'
                archiveArtifacts 'screenapiexample/build/outputs/apk/screenapiexample-release.apk,componentapiexample/build/outputs/apk/componentapiexample-release.apk,screenapiexample/build/outputs/mapping/release/mapping.txt,componentapiexample/build/outputs/mapping/release/mapping.txt'
            }
        }
        stage('Upload Example Apps to Hockeyapp') {
            steps {
                step([$class: 'HockeyappRecorder', applications: [[apiToken: SCREEN_API_EXAMPLE_APP_HOCKEYAPP_API_TOKEN, downloadAllowed: true, dsymPath: 'screenapiexample/build/outputs/mapping/release/mapping.txt', filePath: 'screenapiexample/build/outputs/apk/screenapiexample-release.apk', mandatory: false, notifyTeam: false, releaseNotesMethod: [$class: 'ChangelogReleaseNotes'], uploadMethod: [$class: 'AppCreation', publicPage: false]]], debugMode: false, failGracefully: false])
                step([$class: 'HockeyappRecorder', applications: [[apiToken: COMPONENT_API_EXAMPLE_APP_HOCKEYAPP_API_TOKEN, downloadAllowed: true, dsymPath: 'componentapiexample/build/outputs/mapping/release/mapping.txt', filePath: 'componentapiexample/build/outputs/apk/componentapiexample-release.apk', mandatory: false, notifyTeam: false, releaseNotesMethod: [$class: 'ChangelogReleaseNotes'], uploadMethod: [$class: 'AppCreation', publicPage: false]]], debugMode: false, failGracefully: false])
            }
        }
        stage('Release Documentation') {
            when {
                branch 'master'
                expression {
                    def tag = sh(returnStdout: true, script: 'git tag --contains $(git rev-parse HEAD)').trim()
                    return !tag.isEmpty()
                }
                expression {
                    boolean publish = false
                    try {
                        def version = sh(returnStdout: true, script: './gradlew -q printLibraryVersion').trim()
                        def sha = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
                        input "Release documentation for ${version} from branch ${env.BRANCH_NAME} commit ${sha}?"
                        publish = true
                    } catch (final ignore) {
                        publish = false
                    }
                    return publish
                }
            }
            steps {
                sh 'scripts/release-javadoc.sh $GIT_USR $GIT_PSW'
                sh 'scripts/release-doc.sh $GIT_USR $GIT_PSW'
            }
        }
        stage('Release Library') {
            when {
                branch 'master'
                expression {
                    def tag = sh(returnStdout: true, script: 'git tag --contains $(git rev-parse HEAD)').trim()
                    return !tag.isEmpty()
                }
                expression {
                    boolean publish = false
                    try {
                        def version = sh(returnStdout: true, script: './gradlew -q printLibraryVersion').trim()
                        def sha = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
                        input "Release ${version} from branch ${env.BRANCH_NAME} commit ${sha}?"
                        publish = true
                    } catch (final ignore) {
                        publish = false
                    }
                    return publish
                }
            }
            steps {
                sh './gradlew ginivision:uploadArchives -PmavenOpenRepoUrl=https://repo.gini.net/nexus/content/repositories/open -PrepoUser=$NEXUS_MAVEN_USR -PrepoPassword=$NEXUS_MAVEN_PSW'
            }
        }
    }
}
