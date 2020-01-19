pipeline {
        agent any
        stages {
                stage('Build'){
                    steps {
                        sh 'mvn --version'
                        sh 'mvn clean compile'
                    }
                }
                stage('Test'){
                    steps {
                        sh 'mvn test'
                    }
                }
                stage('Deploy'){
                    steps {
                        sh 'mvn package install'
                    } 
                }
        }
}