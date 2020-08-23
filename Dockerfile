FROM openjdk:10-jre-slim
MAINTAINER VidyashriMB
COPY ./heremapsusecaseapplication/target/heremapsusecaseapplication-0.0.1-SNAPSHOT.jar /usr/src/heremapsusecaseapplication/
WORKDIR /usr/src/heremapsusecaseapplication/
EXPOSE 8000
CMD ["java", "-jar", "heremapsusecaseapplication-0.0.1-SNAPSHOT.jar"]
