#!/usr/bin/env groovy
pipeline {
    agent any
    environment {
        NEXUS_MAVEN = credentials('external-nexus-maven-repo-credentials')
        GIT = credentials('github')
        COMPONENT_API_EXAMPLE_APP_KEYSTORE_PSW = credentials('gini-vision-library-android_component-api-example-app-release-keystore-password')
        COMPONENT_API_EXAMPLE_APP_KEY_PSW = credentials('gini-vision-library-android_component-api-example-app-release-key-password')
        SCREEN_API_EXAMPLE_APP_KEYSTORE_PSW = credentials('gini-vision-library-android_screen-api-example-app-release-keystore-password')
        SCREEN_API_EXAMPLE_APP_KEY_PSW = credentials('gini-vision-library-android_screen-api-example-app-release-key-password')
        EXAMPLE_APP_CLIENT_CREDENTIALS = credentials('gini-vision-library-android_gini-api-client-credentials')
        COMPONENT_API_EXAMPLE_APP_HOCKEYAPP_API_TOKEN = credentials('gini-vision-library-android_component-api-example-app-hockeyapp-api-token')
        SCREEN_API_EXAMPLE_APP_HOCKEYAPP_API_TOKEN = credentials('gini-vision-library-android_screen-api-example-app-hockeyapp-api-token')
    }
    stages {
        stage('Import Pipeline Libraries') {
            steps{
                library 'android-tools'
            }
        }
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
                sh '''
                    ./gradlew clean \
                    ginivision:assembleDebug ginivision:assembleRelease \
                    ginivision-network:assembleDebug ginivision-network:assembleRelease \
                    ginivision-accounting-network:assembleDebug ginivision-accounting-network:assembleRelease
                '''
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
                sh './gradlew ginivision:testDebugUnitTest'
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
                script {
                    avd.deleteCorrupt()
                    avd.create("api-25-pixel", "system-images;android-25;google_apis;x86", "pixel")
                    avd.create("api-25-nexus-9", "system-images;android-25;google_apis;x86", "Nexus 9")
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
                script {
                    def emulatorPort = emulator.start(avd.createName("api-25-pixel"), "pixel", "-prop persist.sys.language=en -prop persist.sys.country=US -gpu host -camera-back emulated -no-snapshot")
                    sh "echo $emulatorPort > emulator_port"
                    adb.setAnimationDurationScale("emulator-$emulatorPort", 0)
                    withEnv(["PATH+TOOLS=$ANDROID_HOME/tools", "PATH+TOOLS_BIN=$ANDROID_HOME/tools/bin", "PATH+PLATFORM_TOOLS=$ANDROID_HOME/platform-tools"]) {
                        sh "ANDROID_SERIAL=emulator-$emulatorPort ./gradlew ginivision:connectedAndroidTest"
                    }
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'ginivision/build/outputs/androidTest-results/connected/*.xml'
                    script {
                        def emulatorPort = sh returnStdout:true, script: 'cat emulator_port'
                        emulatorPort = emulatorPort.trim().replaceAll("\r", "").replaceAll("\n", "")
                        emulator.stop(emulatorPort)
                        sh 'rm emulator_port || true'
                    }
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
                script {
                    def emulatorPort = emulator.start(avd.createName("api-25-nexus-9"), "nexus_9", "-prop persist.sys.language=en -prop persist.sys.country=US -gpu host -camera-back emulated -no-snapshot")
                    sh "echo $emulatorPort > emulator_port"
                    adb.setAnimationDurationScale("emulator-$emulatorPort", 0)
                    withEnv(["PATH+TOOLS=$ANDROID_HOME/tools", "PATH+TOOLS_BIN=$ANDROID_HOME/tools/bin", "PATH+PLATFORM_TOOLS=$ANDROID_HOME/platform-tools"]) {
                        sh "ANDROID_SERIAL=emulator-$emulatorPort ./gradlew ginivision:connectedAndroidTest"
                    }
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'ginivision/build/outputs/androidTest-results/connected/*.xml'
                    script {
                        def emulatorPort = sh returnStdout:true, script: 'cat emulator_port'
                        emulatorPort = emulatorPort.trim().replaceAll("\r", "").replaceAll("\n", "")
                        emulator.stop(emulatorPort)
                        sh 'rm emulator_port || true'
                    }
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
                sh './gradlew ginivision:unifyConnectedTestCoverage ginivision:jacocoTestDebugUnitTestReport'
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
                sh './gradlew generateJavadocCoverage'
                publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'ginivision/build/reports/javadoc-coverage', reportFiles: 'index.html', reportName: 'GVL Javadoc Coverage Report', reportTitles: ''])
                publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'ginivision-network/build/reports/javadoc-coverage', reportFiles: 'index.html', reportName: 'GVL Network Javadoc Coverage Report', reportTitles: ''])
                publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'ginivision-accounting-network/build/reports/javadoc-coverage', reportFiles: 'index.html', reportName: 'GVL Accounting Network Javadoc Coverage Report', reportTitles: ''])
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
                sh './gradlew ginivision-network:lint ginivision-network:checkstyle ginivision-network:findbugs ginivision-network:pmd'
                sh './gradlew ginivision-accounting-network:lint ginivision-accounting-network:checkstyle ginivision-accounting-network:findbugs ginivision-accounting-network:pmd'
                androidLint canComputeNew: false, defaultEncoding: '', healthy: '', pattern: 'ginivision/build/reports/lint-results.xml', unHealthy: ''
                androidLint canComputeNew: false, defaultEncoding: '', healthy: '', pattern: 'ginivision-network/build/reports/lint-results.xml', unHealthy: ''
                androidLint canComputeNew: false, defaultEncoding: '', healthy: '', pattern: 'ginivision-accounting-network/build/reports/lint-results.xml', unHealthy: ''
                checkstyle canComputeNew: false, defaultEncoding: '', healthy: '', pattern: 'ginivision/build/reports/checkstyle/checkstyle.xml', unHealthy: ''
                checkstyle canComputeNew: false, defaultEncoding: '', healthy: '', pattern: 'ginivision-network/build/reports/checkstyle/checkstyle.xml', unHealthy: ''
                checkstyle canComputeNew: false, defaultEncoding: '', healthy: '', pattern: 'ginivision-accounting-network/build/reports/checkstyle/checkstyle.xml', unHealthy: ''
                findbugs canComputeNew: false, defaultEncoding: '', excludePattern: '', healthy: '', includePattern: '', pattern: 'ginivision/build/reports/findbugs/findbugs.xml', unHealthy: ''
                findbugs canComputeNew: false, defaultEncoding: '', excludePattern: '', healthy: '', includePattern: '', pattern: 'ginivision-network/build/reports/findbugs/findbugs.xml', unHealthy: ''
                findbugs canComputeNew: false, defaultEncoding: '', excludePattern: '', healthy: '', includePattern: '', pattern: 'ginivision-accounting-network/build/reports/findbugs/findbugs.xml', unHealthy: ''
                pmd canComputeNew: false, defaultEncoding: '', healthy: '', pattern: 'ginivision/build/reports/pmd/pmd.xml', unHealthy: ''
                pmd canComputeNew: false, defaultEncoding: '', healthy: '', pattern: 'ginivision-network/build/reports/pmd/pmd.xml', unHealthy: ''
                pmd canComputeNew: false, defaultEncoding: '', healthy: '', pattern: 'ginivision-accounting-network/build/reports/pmd/pmd.xml', unHealthy: ''
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
                sh './gradlew generateJavadoc'
                publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'ginivision/build/docs/javadoc', reportFiles: 'index.html', reportName: 'GVL Javadoc', reportTitles: ''])
                publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'ginivision-network/build/docs/javadoc', reportFiles: 'index.html', reportName: 'GVL Network Javadoc', reportTitles: ''])
                publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'ginivision-accounting-network/build/docs/javadoc', reportFiles: 'index.html', reportName: 'GVL Accounting Network Javadoc', reportTitles: ''])
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
                sh './gradlew screenapiexample::clean screenapiexample::assembleRelease -PreleaseKeystoreFile=screen_api_example.jks -PreleaseKeystorePassword="$SCREEN_API_EXAMPLE_APP_KEYSTORE_PSW" -PreleaseKeyAlias=screen_api_example -PreleaseKeyPassword="$SCREEN_API_EXAMPLE_APP_KEY_PSW" -PclientId=$EXAMPLE_APP_CLIENT_CREDENTIALS_USR -PclientSecret=$EXAMPLE_APP_CLIENT_CREDENTIALS_PSW'
                sh './gradlew componentapiexample::clean componentapiexample::assembleRelease -PreleaseKeystoreFile=component_api_example.jks -PreleaseKeystorePassword="$COMPONENT_API_EXAMPLE_APP_KEYSTORE_PSW" -PreleaseKeyAlias=component_api_example -PreleaseKeyPassword="$COMPONENT_API_EXAMPLE_APP_KEY_PSW" -PclientId=$EXAMPLE_APP_CLIENT_CREDENTIALS_USR -PclientSecret=$EXAMPLE_APP_CLIENT_CREDENTIALS_PSW'
                archiveArtifacts 'screenapiexample/build/outputs/apk/screenapiexample-release.apk,componentapiexample/build/outputs/apk/componentapiexample-release.apk,screenapiexample/build/outputs/mapping/release/mapping.txt,componentapiexample/build/outputs/mapping/release/mapping.txt'
            }
        }
//        stage('Upload Example Apps to Hockeyapp') {
//            steps {
//                step([$class: 'HockeyappRecorder', applications: [[apiToken: SCREEN_API_EXAMPLE_APP_HOCKEYAPP_API_TOKEN, downloadAllowed: true, dsymPath: 'screenapiexample/build/outputs/mapping/release/mapping.txt', filePath: 'screenapiexample/build/outputs/apk/release/screenapiexample-release.apk', mandatory: false, notifyTeam: false, releaseNotesMethod: [$class: 'ChangelogReleaseNotes'], uploadMethod: [$class: 'AppCreation', publicPage: false]]], debugMode: false, failGracefully: false])
//                step([$class: 'HockeyappRecorder', applications: [[apiToken: COMPONENT_API_EXAMPLE_APP_HOCKEYAPP_API_TOKEN, downloadAllowed: true, dsymPath: 'componentapiexample/build/outputs/mapping/release/mapping.txt', filePath: 'componentapiexample/build/outputs/apk/release/componentapiexample-release.apk', mandatory: false, notifyTeam: false, releaseNotesMethod: [$class: 'ChangelogReleaseNotes'], uploadMethod: [$class: 'AppCreation', publicPage: false]]], debugMode: false, failGracefully: false])
//            }
//        }
        stage('Release Documentation') {
            when {
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
        stage('Release Library Snapshot') {
            when {
                branch 'develop'
            }
            steps {
                sh '''
                    ./gradlew ginivision:uploadArchives \
                    ginivision-network:uploadArchives \
                    ginivision-accounting-network:uploadArchives \
                    -PmavenSnapshotsRepoUrl=https://repo.gini.net/nexus/content/repositories/snapshots \
                    -PrepoUser=$NEXUS_MAVEN_USR \
                    -PrepoPassword=$NEXUS_MAVEN_PSW
                '''
            }
        }
        stage('Release Library') {
            when {
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
                sh '''
                    ./gradlew ginivision:uploadArchives \
                    ginivision-network:uploadArchives \
                    ginivision-accounting-network:uploadArchives \
                    -PmavenRepoUrl=https://repo.gini.net/nexus/content/repositories/open \
                    -PrepoUser=$NEXUS_MAVEN_USR \
                    -PrepoPassword=$NEXUS_MAVEN_PSW
                '''
            }
        }
    }
}
