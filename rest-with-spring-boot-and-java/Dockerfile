FROM eclipse-temurin:21-jdk
COPY target/*.jar /app.jar
LABEL authors="sousa"

ENTRYPOINT ["java", "-jar", "/app.jar"]

#How to do:
#docker build -t person-docker:0.0.1-SNAPSHOT .
#docker images
#docker tag person-docker:0.0.1-SNAPSHOT kkosmico/person-docker:1.0
#docker push kkosmico/person-docker:1.
#docker run -d -p 8080:8080 kkosmico/person-docker:1.0
#docker logs -f <container_id>
#docker stop <container_id>
#docker start <CONTAINER_ID>
#docker rm <container_id>


#Railway
#ccnklEEyYOHHUMocgKIsbbhyIIQQRrJG
#ccnklEEyYOHHUMocgKIsbbhyIIQQRrJG
#MYSQL_URL mysql://root:ccnklEEyYOHHUMocgKIsbbhyIIQQRrJG@mysql.railway.internal:3306/railway
#MYSQLUSER root
#MYSQL_PASSWORD ccnklEEyYOHHUMocgKIsbbhyIIQQRrJG
#MYSQL_DATABASE railway
#MYSQL_PORT 3306
#MYSQL_HOST mysql.railway.internal


