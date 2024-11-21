# Используем базовый образ с JDK
FROM openjdk:17-jdk-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем .jar файл в рабочую директорию
COPY target/CloudService-0.0.1-SNAPSHOT.jar app.jar

# Открываем порт, который будет использоваться приложением
EXPOSE 8043

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]