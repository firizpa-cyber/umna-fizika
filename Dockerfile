# Используем проверенный образ с Android SDK и Java 17
FROM mingchen/android-build-box:latest

# Устанавливаем рабочую директорию
WORKDIR /project

# Копируем все файлы проекта в контейнер
COPY . .

# Даем права на выполнение скрипта сборки
RUN chmod +x ./gradlew || true

# Команда по умолчанию: сборка отладочного APK
# Мы используем '--no-daemon' для корректной работы в Docker
CMD ["./gradlew", "assembleDebug", "--no-daemon"]
