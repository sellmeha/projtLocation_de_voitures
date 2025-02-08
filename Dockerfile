# Utiliser une image Java
FROM openjdk:21-jdk-slim

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier JAR dans l'image
COPY /jwt-spring-boot/target/jwt-spring-boot-0.0.1-SNAPSHOT.jar app.jar


# Exposer le port
EXPOSE 8080

# Lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
