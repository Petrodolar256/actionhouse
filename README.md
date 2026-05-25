# 🏺 ActionHouse

> *"Lo que para uno es basura, para otro es un tesoro"*

Plataforma web de donación e intercambio de objetos de segunda mano para comunidades universitarias.

🌐 **Demo en vivo:** [actionhouse-production.up.railway.app](https://actionhouse-production.up.railway.app)

---

## ✨ Funcionalidades

- 🔐 **Registro y login seguro** con cifrado BCrypt
- 📦 **Publicar objetos** como donación gratuita o subasta
- 🖼️ **Subida de imágenes** almacenadas en Cloudinary
- 🔍 **Catálogo filtrable** por tipo de publicación
- 🔨 **Sistema de subastas** con registro de ofertas y aceptación
- 💬 **Chat interno** entre comprador y vendedor por objeto
- 📋 **Gestión de publicaciones** propias (entregar, eliminar)
- 👤 **Perfil de usuario** con edición de datos y eliminación de cuenta
- ♿ **Accesibilidad WCAG 2.1 AA** — navegación por teclado y lector de pantalla
- 📱 **Diseño responsivo** para móvil y escritorio
- 🔌 **API REST** para integración con herramientas externas

---

## 🛠️ Stack tecnológico

| Capa | Tecnología |
|---|---|
| Backend | Java 17 + SpringBoot 3.x |
| Seguridad | Spring Security 6.x + BCrypt |
| Base de datos | MySQL 8.0 + JDBC |
| Frontend | Thymeleaf + HTML5 + CSS3 + JavaScript |
| Imágenes | Cloudinary |
| Deploy | Railway |
| Control de versiones | Git + GitHub |

---

## 🚀 Instalación local

### Requisitos previos
- Java JDK 17
- MySQL 8.0
- Maven 3.x

### Pasos

**1. Clona el repositorio**
```bash
git clone https://github.com/PetroDolar256/actionhouse.git
cd actionhouse/actionhouse
```

**2. Crea la base de datos**

Ejecuta en MySQL Workbench:
```sql
CREATE DATABASE actionhouse;
USE actionhouse;

CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol ENUM('user','admin') DEFAULT 'user',
    activo BOOLEAN DEFAULT TRUE,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE objetos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    descripcion TEXT,
    tipo ENUM('donacion','subasta') NOT NULL,
    estado ENUM('disponible','reservado','entregado') DEFAULT 'disponible',
    precio_inicial DECIMAL(10,2) DEFAULT 0.00,
    imagen_url VARCHAR(255),
    id_usuario INT NOT NULL,
    fecha_publicacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
);

CREATE TABLE ofertas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    monto DECIMAL(10,2) NOT NULL,
    id_objeto INT NOT NULL,
    id_usuario INT NOT NULL,
    aceptada BOOLEAN DEFAULT FALSE,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_objeto) REFERENCES objetos(id),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
);

CREATE TABLE mensajes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_objeto INT NOT NULL,
    id_emisor INT NOT NULL,
    id_receptor INT NOT NULL,
    contenido TEXT NOT NULL,
    leido BOOLEAN DEFAULT FALSE,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_objeto) REFERENCES objetos(id),
    FOREIGN KEY (id_emisor) REFERENCES usuarios(id),
    FOREIGN KEY (id_receptor) REFERENCES usuarios(id)
);
```

**3. Configura application.properties**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/actionhouse
spring.datasource.username=root
spring.datasource.password=TU_PASSWORD

cloudinary.cloud-name=TU_CLOUD_NAME
cloudinary.api-key=TU_API_KEY
cloudinary.api-secret=TU_API_SECRET
```

**4. Ejecuta la aplicación**
```bash
mvn spring-boot:run
```

**5. Abre en el navegador**
```
http://localhost:8080
```

---

## 🔌 API REST

| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| `GET` | `/api/objetos` | Lista objetos disponibles | No |
| `GET` | `/api/objetos/{id}` | Obtiene objeto por ID | No |
| `GET` | `/api/objetos/{id}/ofertas` | Lista ofertas de un objeto | No |
| `POST` | `/api/registro` | Registra nuevo usuario | No |

### Ejemplo POST /api/registro
```json
{
    "nombre": "Juan Pérez",
    "email": "juan@email.com",
    "password": "12345678"
}
```

---

## 📁 Estructura del proyecto

```
actionhouse/
├── src/main/java/com/actionhouse/actionhouse/
│   ├── config/          # SecurityConfig, WebConfig
│   ├── controller/      # AuthController, CatalogoController, ChatController, ApiController
│   ├── model/           # Usuario, Objeto, Oferta, Mensaje
│   ├── repository/      # Acceso a BD con JDBC
│   └── service/         # UsuarioService, CloudinaryService
├── src/main/resources/
│   ├── static/css/      # app.css, auth.css, forms.css, chat.css, accessibility.css
│   ├── templates/       # 9 páginas Thymeleaf
│   └── application.properties
└── pom.xml
```

---

## ♿ Accesibilidad

La plataforma cumple **WCAG 2.1 nivel AA**:
- Skip link en todas las páginas
- Estructura semántica HTML5 con roles ARIA
- Focus visible en todos los elementos interactivos
- Contraste de colores conforme al estándar
- Compatible con Windows Narrator
- Navegación completa por teclado
- Soporte para `prefers-reduced-motion`

---

## 📄 Licencia

Proyecto académico — Programación Web 2026-1
