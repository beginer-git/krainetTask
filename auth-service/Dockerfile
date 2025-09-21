# Используем официальный образ OpenJDK 17
FROM eclipse-temurin:17-jdk-alpine

# Создаем рабочую директорию
WORKDIR /app

# Копируем jar-файл приложения
COPY target/auth-service-0.0.1-SNAPSHOT.jar /app/app.jar

# Запуск приложения напрямую без скрипта ожидания БД
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
