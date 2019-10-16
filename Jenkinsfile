pipeline {
	environment {
		registry = "arig23498/webapp"
		registryCredential = 'dockerhubid'
		dockerImage = ''
	}
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
					sh "whoami"
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
		stage('Deploy Application') {
			steps {
				withKubeConfig(caCertificate: '', clusterName: 'kubernetes-admin', contextName: 'kubernetes-admin', credentialsId: 'kubeSecret', namespace: 'webapp', serverUrl: 'https://10.0.0.7:6443') {
					script {
						sh '''if kubectl get deployments.apps | grep -q webapp-v;
	then
		kubectl delete deployment webapp-v`expr $BUILD_NUMBER - 1`;
	fi'''
						sh "kubectl run --image=arig23498/webapp:$BUILD_NUMBER webapp-v$BUILD_NUMBER --port=9090 --replicas=3"
					}
				}
			}
		}
		stage('Expose Application') {
			steps {
				withKubeConfig(caCertificate: '', clusterName: 'kubernetes-admin', contextName: 'kubernetes-admin', credentialsId: 'kubeSecret', namespace: 'webapp', serverUrl: 'https://10.0.0.7:6443') {
					script {
						sh '''if kubectl get services | grep -q webapp-v;
	then
		kubectl delete service webapp-v`expr $BUILD_NUMBER - 1`;
	fi'''
						// sh "kubectl expose deployment webapp-v$BUILD_NUMBER --type=LoadBalancer --port=9090 --target-port=9090 --external-ip=104.211.230.185"
						sh '''cat <<EOF >service-deploy.json
						{
							"apiVersion": "v1",
							"kind": "Service",
							"metadata": {
								"name": "webapp-v$BUILD_NUMBER",
								"labels": {
									"run": "webapp-v$BUILD_NUMBER"
								},
								"namespace": "webapp"
							},
							"spec": {
								"externalIPs": [
									"104.211.230.185"
								],
								"externalTrafficPolicy": "Cluster",
								"ports": [
									{
										"nodePort": 32607,
										"port": 9090,
										"protocol": "TCP",
										"targetPort": 9090
									}
								],
								"selector": {
									"run": "webapp-v$BUILD_NUMBER"
								},
								"sessionAffinity": "None",
								"type": "LoadBalancer"
							},
							"status": {
								"loadBalancer": {}
							}
						}
						'''
						sh "kubectl apply -f service-deploy.json"
					}
				}
			}
		}
	}
}
