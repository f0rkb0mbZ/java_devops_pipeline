FROM openjdk:8
COPY . /usr/src/inframind
WORKDIR /usr/src/inframind
EXPOSE 8080
CMD [ "sh", "target/bin/webapp" ]