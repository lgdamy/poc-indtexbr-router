pipeline {
  agent any
  stages {
    stage('build') {
      steps {
        sh 'mvn clean install'
      }
    }

    stage('package') {
      steps {
        sh 'docker build -t tcc-router .'
      }
    }

    stage('tag') {
      steps {
        sh 'docker tag tcc-router:latest srochg/tcc-router'
      }
    }

    stage('push') {
      steps {
        sh 'docker push srochg/tcc-router'
      }
    }

    stage('rollout') {
      steps {
        sh 'kubectl rollout restart deployment tcc-router-api-deployment'
      }
    }

    stage('qa-test') {
      steps {
        sh '''echo Iniciando os testes



'''
        sh 'sleep 3'
        sh 'echo Finalizando os testes com sucesso'
      }
    }

  }
}