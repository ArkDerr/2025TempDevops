# Usa una imagen base de Java 23 con herramientas para compilar (JDK)
FROM eclipse-temurin:23-jdk AS builder

# Establece la carpeta de trabajo dentro del contenedor (/app). Todas las rutas desde ahora serán relativas a /app.
WORKDIR /app

# Copy the application code
#Copia todo el contenido del proyecto local al contenedor (incluyendo src, .mvn, pom.xml, etc.)
COPY . .
COPY ./pom.xml /app
COPY ./.mvn /app/.mvns
COPY ./mvnw /app
#El wallet de Oracle se copia a /app/Wallet_DSY1103002V (muy importante para conectarse a Oracle Cloud).
COPY ./Wallet /app/Wallet

# Da permisos de ejecución al script mvnw (el wrapper de Maven).
RUN chmod +x mvnw
#Cambia los permisos de todos los archivos del wallet para que sean accesibles por Oracle dentro del contenedor.
RUN chmod -R 755 /app/Wallet

# Descarga todas las dependencias de Maven sin compilar aún, para acelerar futuros builds y validar que no falte nada.
RUN ./mvnw dependency:go-offline

#### COPIAR EL CODIGO FUENTE DENTRO DEL CONTENEDOR
#### COPY ./src /app/src

# Compila el proyecto y genera el .jar final en /app/target/. La opción -DskipTests evita que se ejecuten los tests (útil para producción o acelerar el build).
RUN ./mvnw clean package -DskipTests

# Stage 2: Run the application
#Esta imagen solo tiene el Java Runtime Environment (JRE). Es más liviana que la anterior y suficiente para ejecutar el .jar generado.
FROM eclipse-temurin:23-jre

# Establece la carpeta de trabajo en la imagen final también.
WORKDIR /app

# Copia el archivo .jar desde la etapa de construcción al contenedor final. Se renombra a app.jar para simplificar.
COPY --from=builder /app/target/*.jar app.jar
#Copia el wallet desde la etapa anterior al contenedor final. Clave para que tu app pueda conectarse a Oracle Cloud.
COPY --from=builder /app/Wallet /app/Wallet

# Expone el puerto 8080, que es donde normalmente corre una app Spring Boot. Esto no abre el puerto, solo es informativo.
EXPOSE 8080

# Este es el comando que se ejecuta cuando corres el contenedor. Lanza tu app Spring Boot.
ENTRYPOINT ["java", "-jar", "app.jar"]


