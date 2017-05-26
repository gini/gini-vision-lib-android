pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        sh './gradlew ginivision:clean ginivision:assembleRelease'
      }
    }
  }
}