# Mascotas Perdidas Service

Un servicio backend desarrollado en Java con Spring Boot para la gestión de avisos de mascotas perdidas y encontradas. Este sistema permite a los usuarios reportar mascotas extraviadas, buscar mascotas encontradas y gestionar la información de contacto de manera segura.

## 🚀 Características Principales

- **Gestión de Avisos**: Crear, actualizar y resolver avisos de mascotas perdidas/encontradas
- **Sistema de Imágenes**: Subida y gestión de múltiples imágenes por aviso usando Cloudinary
- **Autenticación JWT**: Sistema de tokens para usuarios anónimos
- **Notificaciones por Email**: Envío automático de confirmaciones y enlaces de gestión
- **Sistema de Reportes**: Los usuarios pueden reportar avisos inapropiados
- **Filtrado Avanzado**: Búsqueda y filtrado de avisos por múltiples criterios
- **API RESTful**: Endpoints bien documentados con paginación

## 🛠️ Tecnologías Utilizadas

### Backend
- **Java 24** - Lenguaje de programación
- **Spring Boot 3.5.5** - Framework principal
- **Spring Data JPA** - Persistencia de datos
- **Spring Security** - Seguridad y autenticación
- **Spring Mail** - Envío de emails
- **Hibernate** - ORM
- **Lombok** - Reducción de código boilerplate
- **Bean Validation** - Validación de datos

### Base de Datos
- **PostgreSQL** - Base de datos principal
- **Hibernate Vector** - Soporte para búsquedas vectoriales

### Servicios Externos
- **Cloudinary** - Almacenamiento y gestión de imágenes
- **Gmail SMTP** - Servicio de envío de emails
- **JWT (JSON Web Tokens)** - Autenticación stateless

### Herramientas de Desarrollo
- **Gradle** - Gestión de dependencias y build
- **Docker** - Containerización
- **Eclipse Temurin** - JDK/JRE

## 📋 Requisitos del Sistema

### Versiones Necesarias
- **Java**: 24 o superior
- **Gradle**: 8.0 o superior
- **PostgreSQL**: 12 o superior
- **Docker**: 20.0 o superior (opcional)

### Variables de Entorno Requeridas
```bash
# Base de datos
DATABASE_URL=jdbc:postgresql://localhost:5432/mascotas_perdidas
USER=tu_usuario_db
PASSWORD=tu_password_db

# Email
EMAIL=tu_email@gmail.com
EMAIL_PASSWORD=tu_app_password

# Cloudinary
CLOUDINARY_KEY=tu_cloudinary_key
CLOUDINARY_API_SECRET=tu_cloudinary_secret

# JWT
JWT_SECRET=tu_jwt_secret_muy_seguro
TOKEN_PEPPER=tu_token_pepper

# UI
UI_URL=http://localhost:3000
UI_NAME=Mascotas Perdidas

# Servicios externos
IMAGE_CONVERTER_URL=http://localhost:8080
PORT=10000
```

## 🚀 Instalación y Configuración

### 1. Clonar el Repositorio
```bash
git clone <url-del-repositorio>
cd mascotas-perdidas-service
```

### 2. Configurar Variables de Entorno
Crear un archivo `.env` o configurar las variables de entorno en tu sistema:
```bash
export DATABASE_URL="jdbc:postgresql://localhost:5432/mascotas_perdidas"
export USER="tu_usuario"
export PASSWORD="tu_password"
# ... resto de variables
```

### 3. Ejecutar la Aplicación

#### Opción A: Con Gradle
```bash
./gradlew bootRun
```

#### Opción B: Con Docker
```bash
# Construir imagen
docker build -t mascotas-perdidas-service .

# Ejecutar contenedor
docker run -p 10000:10000 --env-file .env mascotas-perdidas-service
```

#### Opción C: JAR Ejecutable
```bash
./gradlew build
java -jar build/libs/mascotas-perdidas-service-0.0.1-SNAPSHOT.jar
```

La aplicación estará disponible en `http://localhost:10000`

## 📚 API Endpoints

### Base URL
```
http://localhost:10000/api
```

### VERSION PRODUCTIVA
```
https://mascotas-perdidas-service.onrender.com/api
```


### 🔍 Avisos (Notices)

#### GET /api/notices
Obtiene una lista paginada de avisos con filtros opcionales.

**Parámetros de Query:**
- `page` (int): Número de página (default: 0)
- `size` (int): Tamaño de página (default: 20)
- `sort` (string): Campo de ordenamiento (default: id,desc)
- `title` (string): Filtrar por título
- `specie` (string): Filtrar por especie (perro, gato, otro)
- `noticeType` (string): Filtrar por tipo (perdido, encontrado)
- `status` (string): Filtrar por estado (abierto, resuelto)
- `location` (string): Filtrar por ubicación
- `size` (string): Filtrar por tamaño (pequeño, mediano, grande)

**Respuesta:**
```json
{
  "content": [
    {
      "id": "uuid",
      "createdAt": "2024-01-01T10:00:00Z",
      "title": "Perro perdido en el centro",
      "description": "Se perdió mi perro...",
      "specie": "perro",
      "location": "Centro de la ciudad",
      "noticeType": "perdido",
      "name": "Max",
      "race": "Labrador",
      "color": "Dorado",
      "age": 3,
      "size": "mediano",
      "status": "abierto",
      "contactInfo": {
        "email": "usuario@email.com",
        "phone": "+1234567890"
      },
      "images": [...]
    }
  ],
  "pageable": {...},
  "totalElements": 100,
  "totalPages": 5
}
```

#### GET /api/notices/{id}
Obtiene un aviso específico por ID.

**Parámetros:**
- `id` (UUID): ID del aviso

**Respuesta:** Objeto Notice completo

#### POST /api/notices
Crea un nuevo aviso.

**Content-Type:** `multipart/form-data`

**Body:**
- `notice` (string): JSON con los datos del aviso
- `images` (file[]): Archivos de imagen (opcional)

**Ejemplo de JSON para `notice`:**
```json
{
  "title": "Perro perdido en el centro",
  "description": "Se perdió mi perro...",
  "specie": "perro",
  "location": "Centro de la ciudad",
  "noticeType": "perdido",
  "name": "Max",
  "race": "Labrador",
  "color": "Dorado",
  "age": 3,
  "size": "mediano",
  "contactInfo": {
    "email": "usuario@email.com",
    "phone": "+1234567890"
  }
}
```

**Respuesta:**
```json
{
  "noticeId": "uuid",
  "token": "jwt_token_para_gestionar_el_aviso"
}
```

#### PUT /api/notices/{id}
Actualiza un aviso existente.

**Headers:**
- `Authorization: Bearer {jwt_token}`

**Content-Type:** `multipart/form-data`

**Body:**
- `notice` (string): JSON con los datos actualizados
- `newImages` (file[]): Nuevas imágenes (opcional)

**Respuesta:** 204 No Content

#### POST /api/notices/{id}/resolve
Marca un aviso como resuelto.

**Headers:**
- `Authorization: Bearer {jwt_token}`

**Respuesta:** 202 Accepted

#### POST /api/notices/{id}/manage
Verifica si el token es válido para gestionar el aviso.

**Headers:**
- `Authorization: Bearer {jwt_token}`

**Respuesta:** 200 OK si es válido, 401 Unauthorized si no

### 📋 Reportes (Reports)

#### GET /api/reports
Obtiene una lista de reportes.

**Parámetros de Query:**
- `noticeId` (UUID): Filtrar reportes por aviso específico

**Respuesta:**
```json
[
  {
    "id": "uuid",
    "reportedNotice": {...},
    "reason": "spam",
    "details": "Este aviso es spam",
    "createdAt": "2024-01-01T10:00:00Z"
  }
]
```

#### GET /api/reports/{id}
Obtiene un reporte específico por ID.

**Respuesta:** Objeto UserReport completo

#### POST /api/reports
Crea un nuevo reporte.

**Body:**
```json
{
  "noticeId": "uuid",
  "reason": "spam",
  "details": "Este aviso es spam"
}
```

**Respuesta:** 201 Created con el ID del reporte

## 🔐 Autenticación

El sistema utiliza JWT (JSON Web Tokens) para la autenticación de usuarios anónimos:

1. Al crear un aviso, se genera un JWT token
2. Este token se envía por email al usuario
3. El token permite actualizar y gestionar el aviso
4. Los tokens tienen una duración de 30 días (configurable)

### Estructura del Token
```json
{
  "noticeId": "uuid_del_aviso",
  "iat": 1640995200,
  "exp": 1643587200,
  "iss": "mascotas-perdidas"
}
```

## 🗄️ Modelo de Datos

### Notice (Aviso)
- `id`: UUID único
- `title`: Título del aviso
- `description`: Descripción detallada
- `specie`: Especie (perro, gato, otro)
- `location`: Ubicación donde se perdió/encontró
- `noticeType`: Tipo (perdido, encontrado)
- `name`: Nombre de la mascota
- `race`: Raza
- `color`: Color
- `age`: Edad en años
- `size`: Tamaño (pequeño, mediano, grande)
- `status`: Estado (abierto, resuelto)
- `contactInfo`: Información de contacto (JSON)
- `images`: Lista de imágenes asociadas
- `createdAt`: Fecha de creación

### UserReport (Reporte)
- `id`: UUID único
- `reportedNotice`: Aviso reportado
- `reason`: Razón del reporte (spam, inapropiado, informacionFalsa, otro)
- `details`: Detalles adicionales
- `createdAt`: Fecha de creación


## 🐳 Docker

### Construir Imagen
```bash
docker build -t mascotas-perdidas-service .
```

### Ejecutar con Docker Compose
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "10000:10000"
    environment:
      - DATABASE_URL=jdbc:postgresql://db:5432/mascotas_perdidas
      - USER=postgres
      - PASSWORD=password
    depends_on:
      - db
  
  db:
    image: postgres:15
    environment:
      - POSTGRES_DB=mascotas_perdidas
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=password
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```
## 📝 Logs

La aplicación utiliza SLF4J con Logback para el logging. Los logs se pueden configurar en `src/main/resources/logback-spring.xml`.

## 🤝 Equipo

- Cristian Moreno, Desarrollador Frontend (React), [+54 9 3874 45-0711]
- Nicolas Montes, Desarrollador Backend (Java), [+54 9 3875 08-6607]
- Nahuel Serrudo, Especialista en IA y Base de Datos, [+54 9 3875 145 165]

## Links:
- Demo pública (HTTPS): https://mascotas-perdidas.vercel.app/
- Repositorio (GitHub/GitLab/Bitbucket):
- Frontend (React): https://github.com/cscristianmoreno/app-mascotas-perdidas
- Backend (Servicio IA - Python): https://github.com/jnserrudo/Reconocimiento-Mascotas
- Backend (Principal - Java): https://github.com/nicomontes0/mascotas-perdidas-service

## 🔄 Changelog

### v0.0.1-SNAPSHOT
- Implementación inicial
- CRUD de avisos
- Sistema de imágenes con Cloudinary
- Autenticación JWT
- Notificaciones por email
- Sistema de reportes
- Filtrado avanzado
