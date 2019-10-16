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



## Run the Repository:

```bash
$ git clone https://github.com/ariG23498/webapp_maven.git
$ cd webapp_maven
$ mvn package
$ cd target/bin/
$ ./webapp
```

The web application is running on your localhost at post 9090

Go to your favourite web-browser and in the address bar type `localhost:9090`

## Sample web app commit line from eclipse
