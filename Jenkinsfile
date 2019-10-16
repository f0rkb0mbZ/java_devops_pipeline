pipeline {
	agent any
	stages {
		stage('SCM Checkout') {
			steps{
				git 'https://github.com/forkbomb-666/webapp_maven'
			}
		}
		stage('Compile-Package') {
			steps {
				script{
					def mvn_home = tool name: 'maven-3.6.0', type: 'maven'
					sh "${mvn_home}/bin/mvn package"
				}
			}
		}
		stage('Building Image') {
			steps {
				script {
					dockerImage = docker.build registry + ":$BUILD_NUMBER"
				}
			}
		}
		stage('Deploy Image') {
			steps {
				script {
					docker.withRegistry('', registryCredential) {
						dockerImage.push()
					}
				}
			}
		}
		stage('Remove Unused docker image') {
			steps {
				script {
					sh "docker rmi $registry:$BUILD_NUMBER"
				}
			}
		}
	}
}
