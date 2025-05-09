// src/main/java/com/telastech360/crmTT360/security/config/OpenApiConfig.java
package com.telastech360.crmTT360.security.config;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

// Imports adicionales necesarios para la configuración de seguridad
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

/**
 * Configuración global de OpenAPI 3 para la documentación de la API generada por SpringDoc.
 * Define la información general de la API (título, versión, descripción, contacto, licencia),
 * los servidores disponibles, y configura el esquema de seguridad JWT Bearer para
 * permitir la autenticación desde la interfaz de Swagger UI.
 */
@OpenAPIDefinition(
        info = @Info(
                title = "API CRM TT360 - TelasTech360", // Título de tu API
                version = "1.0.0", // Versión de tu API
                description = "Documentación de la API del sistema CRM para TelasTech360. " +
                        "Gestiona inventario, pedidos, usuarios, roles, proveedores y más.", // Descripción detallada
                contact = @Contact(
                        name = "Equipo TelasTech360", // Nombre del contacto
                        url = "http://www.telastech360.com/soporte", // URL de soporte (ejemplo)
                        email = "soporte@telastech360.com" // Email de contacto (ejemplo)
                ),
                license = @License(
                        name = "Apache 2.0", // Licencia (ejemplo)
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html" // URL de la licencia
                )
        ),
        // Define los servidores donde la API está disponible
        servers = {
                @Server(url = "http://localhost:8080", description = "Servidor de Desarrollo Local"),
                // Puedes añadir más servidores si aplica (ej. Staging, Producción)
                // @Server(url = "https://api.telastech360.com", description = "Servidor de Producción")
        },
        // Enlace a documentación externa adicional (opcional)
        externalDocs = @ExternalDocumentation(
                description = "Wiki Interna del Proyecto",
                url = "http://wiki.telastech360.com/crm" // URL ejemplo
        ),
        // Define que la seguridad 'bearerAuth' (definida abajo) se aplica globalmente a la API
        security = @SecurityRequirement(name = "bearerAuth")
)
// Define el esquema de seguridad llamado 'bearerAuth' para JWT
@SecurityScheme(
        name = "bearerAuth", // Nombre de referencia para el esquema de seguridad
        description = "Autenticación JWT Bearer. Ingresar el token precedido por 'Bearer ' (ej: Bearer eyJhbGciOiJI...)", // Descripción para Swagger UI
        scheme = "bearer", // Esquema de autenticación HTTP (Bearer)
        type = SecuritySchemeType.HTTP, // Tipo de esquema (HTTP)
        bearerFormat = "JWT", // Formato específico del token Bearer (JWT)
        in = SecuritySchemeIn.HEADER // Indica que el token se envía en la cabecera HTTP
)
public class OpenApiConfig {
    // Esta clase actúa como contenedor para las anotaciones de configuración de OpenAPI.
    // No necesita contenido en el cuerpo de la clase.
}