# MicroService based Java WebApp

### Technology Stack used:

* Java
* Servlet
* Maven
* Tomcat
* Jenkins
* Docker
* Kubernetes



### Contents of the Repository:

* Web Application made with `Java Servlet`
* `pom.xml` for `maven` to build the application and package it into a `.jar`
* `Dockerfile` which holds the steps to build a docker image of the web application
* `Jenkinsfile` to help `Jenkins` automate the building and deploying the web application into `docker hub`



## Run the Web Application locally:

```bash
$ git clone https://github.com/ariG23498/webapp_maven.git
$ cd webapp_maven
$ mvn package
$ cd target/bin/
$ ./webapp
```

The web application is running on your localhost at post 9090

Go to your favourite web-browser and in the address bar type `localhost:9090`

## CI/CD with Jenkins and Kubernetes Deployment:
### VM instance setup:
In the following steps we will talk specifically of the Kubernetes cluster that has been created, the cluster consists of 2 VM instances one working as the `master` other as the `worker`.
The VMs `must` be in same `resource group` and share the same `virtual network`.
In each of the VMs we need to run the following block of codes.
#### **`setup_docker.sh`**
```bash
sudo apt-get remove docker docker-engine docker.io containerd runc
sudo apt-get update
sudo apt-get install -y \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg-agent \
    software-properties-common
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo apt-key fingerprint 0EBFCD88
sudo add-apt-repository \
   "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
   $(lsb_release -cs) \
   stable"
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io
```
#### **`setup_kebernetes.sh`**
```bash
apt-get update && apt-get install -y apt-transport-https curl
curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key add -
cat <<EOF >/etc/apt/sources.list.d/kubernetes.list
deb https://apt.kubernetes.io/ kubernetes-xenial main
EOF
apt-get update
apt-get install -y kubelet kubeadm kubectl
apt-mark hold kubelet kubeadm kubectl
```
Setup ` Jenkins` as directed by [Jenkins](https://jenkins.io/). 
For the `CI/CD` pipeline we had to authenticate Jenkins with `GitHub`, `Docker Hub` and `Kubernetes`.
We have used the following deployment file:
#### **`service-deploy.json`**
```json
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
```
The rest of the CI/CD pipeline is taken care of by Jenkins. :heart:

After `successfully` deploying the web app the app is exposed at `<worker-node-IP>:32607`
