pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        sh './gradlew ginivision:clean ginivision:assembleDebug ginivision:assembleRelease'
      }
    }
    stage('Unit Tests') {
      steps {
        sh './gradlew ginivision:test'
      }
      post {
        always {
          publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'ginivision/build/reports/tests/testDebugUnitTest', reportFiles: 'index.html', reportName: 'Unit Test Results', reportTitles: ''])
        }
      }
    }
    stage('Instrumentation Tests') {
      steps {
        sh 'scripts/start-emulator.sh mobilecd_android-25_google_apis-x86_512M -prop persist.sys.language=en -prop persist.sys.country=US -no-snapshot-load -no-snapshot-save -camera-back emulated > emulator_port'
        sh 'emulator_port=$(cat emulator_port) && scripts/wait-for-emulator-to-boot.sh emulator-$emulator_port 20'
        sh 'emulator_port=$(cat emulator_port) && ./gradlew ginivision:targetedDebugAndroidTest -PpackageName=net.gini.android.vision -PtestTarget=emulator-$emulator_port'
      }
      post {
        always {
          junit allowEmptyResults: true, testResults: 'ginivision/build/outputs/androidTest-results/targeted/*.xml'
          sh 'emulator_port=$(cat emulator_port) && adb -s emulator-$emulator_port emu kill || true'
          sh 'rm emulator_port || true'
        }
      }
    }
    stage('Code Coverage') {
      steps {
        sh './gradlew ginivision:unifyTargetedTestCoverage ginivision:jacocoTestDebugUnitTestReport'
        publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'ginivision/build/reports/jacoco/jacocoTestDebugUnitTestReport/html', reportFiles: 'index.html', reportName: 'Code Coverage Report', reportTitles: ''])
      }
    }
    stage('Javadoc Coverage') {
      steps {
        sh './gradlew ginivision:generateJavadocCoverage'
        publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'ginivision/build/reports/javadoc-coverage', reportFiles: 'index.html', reportName: 'Javadoc Coverage Report', reportTitles: ''])
      }
    }
    stage('Code Analysis') {
      steps {
        sh './gradlew ginivision:lint ginivision:checkstyle ginivision:findbugs ginivision:pmd'
        androidLint canComputeNew: false, defaultEncoding: '', healthy: '', pattern: 'ginivision/build/reports/lint-results.xml', unHealthy: ''
        checkstyle canComputeNew: false, defaultEncoding: '', healthy: '', pattern: 'ginivision/build/reports/checkstyle/checkstyle.xml', unHealthy: ''
        findbugs canComputeNew: false, defaultEncoding: '', excludePattern: '', healthy: '', includePattern: '', pattern: 'ginivision/build/reports/findbugs/findbugs.xml', unHealthy: ''
        pmd canComputeNew: false, defaultEncoding: '', healthy: '', pattern: 'ginivision/build/reports/pmd/pmd.xml', unHealthy: ''
      }
    }
    stage('Build Documentation') {
      steps {
        sh 'scripts/build-sphinx-doc.sh'
        publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'ginivision/src/doc/build/html', reportFiles: 'index.html', reportName: 'Documentation', reportTitles: ''])
      }
    }
    stage('Build Javadoc') {
      steps {
        sh './gradlew ginivision:generateJavadoc'
        publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'ginivision/build/docs/javadoc', reportFiles: 'index.html', reportName: 'Javadoc', reportTitles: ''])
      }
    }
    stage('Archive Artifacts') {
      steps {
         sh 'cd ginivision/build/reports/jacoco/jacocoTestDebugUnitTestReport && zip -r testCoverage.zip html && cd -'
         sh 'cd ginivision/build/reports && zip -r javadocCoverage.zip javadoc-coverage && cd -'
         archiveArtifacts 'ginivision/build/outputs/aar/*.aar,ginivision/build/reports/jacoco/jacocoTestDebugUnitTestReport/testCoverage.zip,ginivision/build/reports/javadocCoverage.zip'
      }
    }
  }
}