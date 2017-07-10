pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        sh './gradlew ginivision:clean ginivision:assembleDebug'
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
        sh '$ANDROID_HOME/emulator/emulator -ports 5554,5555 -prop persist.sys.language=en -prop persist.sys.country=US -avd mobilecd_android-25_google_apis-x86_512M -no-snapshot-load -no-snapshot-save -camera-back emulated &'
        sh 'scripts/wait-for-emulator-to-boot emulator-5554'
        sh './gradlew ginivision:targetedDebugAndroidTest -PpackageName=net.gini.android.vision -PtestTarget=emulator-5554'
      }
      post {
        always {
          junit allowEmptyResults: true, testResults: 'ginivision/build/outputs/androidTest-results/targeted/*.xml'
          sh 'adb -s emulator-5554 emu kill || true'
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
  }
}