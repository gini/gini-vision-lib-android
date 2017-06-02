pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        parallel(
          "Build": {
            sh './gradlew ginivision:clean ginivision:assembleRelease'
            
          },
          "Launch Emulator": {
            sh 'echo "Launching emulator"'
            
          }
        )
      }
    }
    stage('Unit Test') {
      steps {
        sh 'echo "Running unit tests"'
      }
    }
    stage('Instrumentation Test') {
      steps {
        sh 'echo "Running instr tests"'
      }
    }
    stage('Code Analysis') {
      steps {
        sh 'echo "Analysing code"'
      }
    }
    stage('Publish to Hockeyapp') {
      steps {
        sh 'echo "Publishing to Hockeyapp"'
      }
    }
    stage('Teardown') {
      steps {
        sh 'echo "Tearing down"'
      }
    }
  }
}