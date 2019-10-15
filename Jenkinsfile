node{
	stage('SCM Checkout') {
		git 'https://github.com/forkbomb-666/webapp_maven'
	}
	stage('Compile-Package') {
		def mvn_home = tool name: 'maven-3.6.0', type: 'maven'
		sh "${mvn_home}/bin/mvn package"
	}
}
