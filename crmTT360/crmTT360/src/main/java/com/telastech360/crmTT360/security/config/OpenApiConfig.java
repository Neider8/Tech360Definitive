package com.telastech360.crmTT360.security.config;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

// Configuración principal de OpenAPI para la documentación de la API
@OpenAPIDefinition(
        info = @Info(
                title = "API CRM TT360 - TelasTech360", // Título de tu API
                version = "1.0.0", // Versión de tu API
                description = "Documentación de la API del sistema CRM para TelasTech360. " +
                        "Gestiona inventario, pedidos, usuarios, roles, proveedores y más.", // Descripción detallada
                contact = @Contact(
                        name = "Tu Nombre/Equipo", // Nombre del contacto
                        url = "http://tudominio.com/soporte", // URL de soporte (opcional)
                        email = "soporte@tudominio.com" // Email de contacto (opcional)
                ),
                license = @License(
                        name = "Apache 2.0", // Licencia
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html" // URL de la licencia
                )
        ),
        // Puedes añadir servidores si tu API se despliega en múltiples entornos
        servers = {
                @Server(url = "http://localhost:8080", description = "Servidor de Desarrollo Local")
                // @Server(url = "https://api.tudominio.com", description = "Servidor de Producción")
        },
        // Puedes añadir documentación externa si la tienes
        externalDocs = @ExternalDocumentation(
                description = "Documentación de la API Externa",
                url = "http://tudominio.com/docs/external"
        )
)
public class OpenApiConfig {
    // Esta clase no necesita tener código dentro, las anotaciones son suficientes
}