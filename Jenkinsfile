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
    }
    stage('Instrumentation Tests') {
      steps {
        sh '$ANDROID_HOME/emulator/emulator -ports 5554,5555 -prop persist.sys.language=en -prop persist.sys.country=US -avd mobilecd_android-25_google_apis-x86_512M -no-snapshot-load -no-snapshot-save -camera-back emulated &'
        sh 'scripts/wait-for-emulator-to-boot emulator-5554'
        sh './gradlew ginivision:targetedDebugAndroidTest -PpackageName=net.gini.android.vision -PtestTarget=emulator-5554'
      }
      post {
        always {
          junit allowEmptyResults: true, testResults: 'ginivision/build/reports/androidTests/targeted/ginivisiondebugTestResults.xml'
          sh 'adb -s emulator-5554 emu kill || true'
        }
      }
    }
    stage('Code Analysis') {
      steps {
        sh 'echo "Analysing code"'
      }
    }
  }
}