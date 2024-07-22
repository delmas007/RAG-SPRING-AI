FROM eclipse-temurin:21.0.3_9-jre
LABEL maintainer="angamancedrick@gmail.com"
WORKDIR /app
COPY target/RAG_openAI-0.0.1-SNAPSHOT.jar rag.jar
#EXPOSE 7788
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "rag.jar"]