# Microservice based Java Web App

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/d78066416d2e4be1bf1aa259c661bb29)](https://www.codacy.com/manual/forkbomb-666/webapp_maven?utm_source=github.com&utm_medium=referral&utm_content=forkbomb-666/webapp_maven&utm_campaign=Badge_Grade) [![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

## Technology Stack used

-   Java
-   Servlet
-   Maven
-   Tomcat
-   Jenkins
-   Docker
-   Kubernetes

## Contents of the Repository

-   Web Application made with `Java Servlet`
-   `pom.xml` for `maven` to build the application and package it into a `.jar`
-   `Dockerfile` which holds the steps to build a docker image of the web application
-   `Jenkinsfile` to help `Jenkins` automate the building and deploying the web application into `docker hub`

## Run the Web Application locally

```bash
git clone https://github.com/ariG23498/webapp_maven.git
cd webapp_maven
mvn package
cd target/bin/
./webapp
```

The web application is running on your localhost at post 9090

Go to your favorite web-browser and in the address bar type `localhost:9090`

## CI/CD with Jenkins and Kubernetes Deployment

### VM instance setup

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

cat > /etc/docker/daemon.json <<EOF
{
  "exec-opts": ["native.cgroupdriver=systemd"],
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "100m"
  },
  "storage-driver": "overlay2"
}
EOF

mkdir -p /etc/systemd/system/docker.service.d

systemctl daemon-reload
systemctl restart docker
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

Authorize Jenkins bye creating a new kubernetes service account and generating a base64 secret key:

#### **`authorize_jenkins.sh`**

```bash
kubectl -n webapp create sa jenkins
kubectl create clusterrolebinding jenkins --clusterrole cluster-admin --serviceaccount=webapp:jenkins
kubectl get -n webapp sa/jenkins --template='{{range .secrets}}{{ .name }} {{end}}' | xargs -n 1 kubectl -n webapp get secret --template='{{ if .data.token }}{{ .data.token }}{{end}}' | head -n 1 | base64 -d -
```

Save the secret as secret text credential for using [kubernetes-cli-plugin](https://github.com/jenkinsci/kubernetes-cli-plugin) with Jenkins.

### Database setup

After setting up each VMs, we are creating a stateful persistent volume of 4 GiB for our MySQL database storage:

#### **`mysql-pv.yaml`**

```yaml
apiVersion: v1
kind: PersistentVolume
metadata:
  name: mysql-pv-volume
  labels:
    type: local
spec:
  storageClassName: manual
  capacity:
    storage: 4Gi
  accessModes:
    - ReadWriteOnce
  hostPath:
    path: "/mnt/data"
---
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: mysql-pv-claim
spec:
  storageClassName: manual
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 4Gi
```

Now, we need to deploy the mysql:5.7 image as a deployment in the kubernetes cluster.

#### **`mysql-deployment.yaml`**

```yaml
apiVersion: v1
kind: Service
metadata:
  name: mysql
spec:
  type: ClusterIP
  ports:
  - name: mysql
    port: 3306
    protocol: TCP
    targetPort: 3306
  selector:
    app: mysql
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mysql
spec:
  selector:
    matchLabels:
      app: mysql
  strategy:
    type: Recreate
  template:
    metadata:
      labels:
        app: mysql
    spec:
      containers:
      - image: mysql:5.7
        name: mysql
        env:
          # Use secret in real usage
        - name: MYSQL_ROOT_PASSWORD
          value: Dare2@hack
        ports:
        - containerPort: 3306
          name: mysql
        volumeMounts:
        - name: mysql-persistent-storage
          mountPath: /var/lib/mysql
      volumes:
      - name: mysql-persistent-storage
        persistentVolumeClaim:
          claimName: mysql-pv-claim
```

### Automation setup

Setup `Jenkins` as directed by [Jenkins](https://jenkins.io/). 
For the `CI/CD` pipeline we had to authenticate Jenkins with `GitHub`, `Docker Hub` and `Kubernetes`.
We have used the following deployment file to expose our webapp:

#### **`webapp-service-deploy.json`**

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
