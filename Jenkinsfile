pipeline {
	agent {
		docker {
			image 'maven:3-jdk-8'
			args '-v /root/.m2:/root/.m2'
		}
	}
	stages {
		stage('Build') {
			steps {
				powershell 'mvn -B -DskipTests clean package'
			}
		}
	}
}
