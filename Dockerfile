FROM eclipse-temurin:22-jre-alpine
WORKDIR /app
COPY app.jar /app/app.jar
COPY wrapper/chain-config.json /app/wrapper/chain-config.json
CMD ["java", "-jar", "app.jar", "wrapper/chain-config.json"]