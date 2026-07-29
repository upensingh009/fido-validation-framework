pipeline {
  agent any

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }
    stage('Build') {
      steps {
        sh 'mvn -B -V clean package'
      }
    }
    stage('Unit Tests') {
      steps {
        sh 'mvn test'
      }
    }
    stage('Docker Compose Up') {
      steps {
        sh 'docker-compose up -d --build'
      }
    }
    stage('Playwright Tests') {
      steps {
        echo 'Run Playwright tests (requires separate setup)'
      }
    }
    stage('Publish Allure') {
      steps {
        echo 'Publish Allure (if configured)'
      }
    }
  }

  post {
    always {
      archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
    }
  }
}
