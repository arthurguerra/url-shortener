# Usando imagem oficial do Java
FROM eclipse-temurin:21-jre

# Diretório de trabalho dentro do container
WORKDIR /app

# Copia o jar gerado
COPY target/urlshortener-0.0.1-SNAPSHOT.jar app.jar

# Porta que a aplicação irá expor
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]