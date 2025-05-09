// src/main/java/com/telastech360/crmTT360/util/DataLoader.java
package com.telastech360.crmTT360.util;

import com.telastech360.crmTT360.entity.Permiso; // <-- Añadir import
import com.telastech360.crmTT360.entity.Rol;
import com.telastech360.crmTT360.entity.Usuario;
import com.telastech360.crmTT360.repository.PermisoRepository; // <-- Añadir import
import com.telastech360.crmTT360.repository.RolRepository;
import com.telastech360.crmTT360.repository.UsuarioRepository;
import com.telastech360.crmTT360.service.RolPermisoService; // <-- Añadir import
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*; // <-- Añadir import
import java.util.stream.Stream; // <-- Añadir import

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    @Autowired private RolRepository rolRepository;
    @Autowired private PermisoRepository permisoRepository; // <-- Inyectar Repo Permiso
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private RolPermisoService rolPermisoService; // <-- Inyectar Servicio RolPermiso

    // --- Contraseñas iniciales ---
    @Value("${dataloader.admin.password:PasswordAdmin123.}") private String adminPassword;
    @Value("${dataloader.gerente.password:PasswordGerente456.}") private String gerentePassword;
    @Value("${dataloader.operario.password:PasswordOperario789.}") private String operarioPassword;
    @Value("${dataloader.cajero.password:PasswordCajero012.}") private String cajeroPassword;
    // -----------------------------

    @Override
    @Transactional // Envuelve toda la lógica en una única transacción
    public void run(String... args) throws Exception {
        log.info("Iniciando la carga de datos iniciales (Roles, Permisos, Usuarios y Asignaciones)...");

        // --- 1. Crear Roles si no existen ---
        Rol rolAdmin = crearRolSiNoExiste("ADMIN", "Administrador total del sistema.");
        Rol rolGerente = crearRolSiNoExiste("GERENTE", "Responsable de sucursal o área.");
        Rol rolOperario = crearRolSiNoExiste("OPERARIO", "Usuario de producción o almacén.");
        Rol rolCajero = crearRolSiNoExiste("CAJERO", "Usuario de punto de venta.");

        // --- 2. Crear Permisos si no existen ---
        List<String> nombresPermisos = Arrays.asList(
                // Usuarios
                "LEER_USUARIOS", "CREAR_USUARIO", "EDITAR_USUARIO", "ELIMINAR_USUARIO", "BUSCAR_USUARIOS", "LISTAR_USUARIOS_POR_ROL",
                // Roles & Permisos
                "LEER_ROLES", "CREAR_ROL", "EDITAR_ROL", "ELIMINAR_ROL",
                "LEER_PERMISOS_BASE", "CREAR_PERMISO_BASE", "EDITAR_PERMISO_BASE", "ELIMINAR_PERMISO_BASE",
                "LEER_PERMISOS_ROL", "MODIFICAR_PERMISOS_ROL",
                // Productos
                "LEER_PRODUCTOS", "CREAR_PRODUCTO", "EDITAR_PRODUCTO", "ELIMINAR_PRODUCTO", "BUSCAR_PRODUCTOS",
                // Materias Primas
                "LEER_MATERIAS_PRIMAS", "CREAR_MATERIA_PRIMA", "EDITAR_MATERIA_PRIMA", "ELIMINAR_MATERIA_PRIMA", "BUSCAR_MATERIAS_PRIMAS",
                // Items (Genérico)
                "LEER_ITEMS", "CREAR_ITEM", "EDITAR_ITEM", "ELIMINAR_ITEM",
                // Pedidos
                "LEER_PEDIDO", "CREAR_PEDIDO", "EDITAR_PEDIDO", "ELIMINAR_PEDIDO", "BUSCAR_PEDIDOS",
                // Pedido Detalles
                "LEER_DETALLES_PEDIDO_TODOS", "AGREGAR_DETALLE_PEDIDO", "EDITAR_DETALLE_PEDIDO", "ELIMINAR_DETALLE_PEDIDO",
                // Facturas
                "LEER_FACTURAS", "CREAR_FACTURA", "EDITAR_FACTURA", "ELIMINAR_FACTURA", "BUSCAR_FACTURAS",
                "BUSCAR_FACTURAS_PENDIENTES", "VER_REPORTES_FACTURACION",
                // Clientes
                "LEER_CLIENTES", "CREAR_CLIENTE", "EDITAR_CLIENTE", "ELIMINAR_CLIENTE", "BUSCAR_CLIENTES", "BUSCAR_CLIENTES_POR_PRESUPUESTO",
                // Proveedores
                "LEER_PROVEEDORES", "CREAR_PROVEEDOR", "EDITAR_PROVEEDOR", "ELIMINAR_PROVEEDOR", "BUSCAR_PROVEEDORES",
                // Bodegas
                "LEER_BODEGAS", "CREAR_BODEGA", "EDITAR_BODEGA", "ELIMINAR_BODEGA", "BUSCAR_BODEGAS", "BUSCAR_BODEGAS_CAPACIDAD",
                // Categorías
                "LEER_CATEGORIAS", "CREAR_CATEGORIA", "EDITAR_CATEGORIA", "ELIMINAR_CATEGORIA",
                // Estados
                "LEER_ESTADOS", "CREAR_ESTADO", "EDITAR_ESTADO", "ELIMINAR_ESTADO", "BUSCAR_ESTADOS"
        );

        Map<String, Permiso> mapaPermisos = new HashMap<>();
        for (String nombrePermiso : nombresPermisos) {
            Permiso p = crearPermisoSiNoExiste(nombrePermiso.toUpperCase(), "Permiso para " + nombrePermiso.toLowerCase().replace('_', ' '));
            mapaPermisos.put(nombrePermiso.toUpperCase(), p); // Guardar en mayúsculas en el mapa también
        }

        // --- 3. Asignar Permisos a Roles si no existen ---
        log.info("Asignando permisos iniciales a roles...");

        // ADMIN (Todos los permisos)
        mapaPermisos.values().forEach(permiso -> asignarPermisoARolSiNoExiste(rolAdmin, permiso));
        log.info("Permisos asignados a ADMIN.");

        // GERENTE (Subconjunto - AJUSTA ESTA LISTA SEGÚN NECESITES)
        Stream.of("LEER_USUARIOS", "EDITAR_USUARIO", "BUSCAR_USUARIOS", "LISTAR_USUARIOS_POR_ROL",
                        "LEER_ROLES", "LEER_PERMISOS_ROL",
                        "LEER_PRODUCTOS", "CREAR_PRODUCTO", "EDITAR_PRODUCTO", "ELIMINAR_PRODUCTO", "BUSCAR_PRODUCTOS",
                        "LEER_MATERIAS_PRIMAS", "CREAR_MATERIA_PRIMA", "EDITAR_MATERIA_PRIMA", "ELIMINAR_MATERIA_PRIMA", "BUSCAR_MATERIAS_PRIMAS",
                        "LEER_PEDIDO", "CREAR_PEDIDO", "EDITAR_PEDIDO", "ELIMINAR_PEDIDO", "BUSCAR_PEDIDOS", "ELIMINAR_DETALLE_PEDIDO", "EDITAR_DETALLE_PEDIDO", "AGREGAR_DETALLE_PEDIDO",
                        "LEER_FACTURAS", "CREAR_FACTURA", "EDITAR_FACTURA", "ELIMINAR_FACTURA", "BUSCAR_FACTURAS", "VER_REPORTES_FACTURACION", "BUSCAR_FACTURAS_PENDIENTES",
                        "LEER_CLIENTES", "CREAR_CLIENTE", "EDITAR_CLIENTE", "ELIMINAR_CLIENTE", "BUSCAR_CLIENTES", "BUSCAR_CLIENTES_POR_PRESUPUESTO",
                        "LEER_PROVEEDORES", "CREAR_PROVEEDOR", "EDITAR_PROVEEDOR", "ELIMINAR_PROVEEDOR", "BUSCAR_PROVEEDORES",
                        "LEER_BODEGAS", "CREAR_BODEGA", "EDITAR_BODEGA", "ELIMINAR_BODEGA", "BUSCAR_BODEGAS", "BUSCAR_BODEGAS_CAPACIDAD",
                        "LEER_CATEGORIAS", "CREAR_CATEGORIA", "EDITAR_CATEGORIA", "ELIMINAR_CATEGORIA",
                        "LEER_ESTADOS", "CREAR_ESTADO", "EDITAR_ESTADO", "ELIMINAR_ESTADO", "BUSCAR_ESTADOS",
                        "LEER_ITEMS", "EDITAR_ITEM"
                )
                .map(String::toUpperCase) // Asegurar que buscamos en mayúsculas
                .map(mapaPermisos::get)
                .filter(Objects::nonNull)
                .forEach(permiso -> asignarPermisoARolSiNoExiste(rolGerente, permiso));
        log.info("Permisos asignados a GERENTE.");

        // OPERARIO (Subconjunto - AJUSTA ESTA LISTA SEGÚN NECESITES)
        Stream.of("LEER_PRODUCTOS", "CREAR_PRODUCTO", "EDITAR_PRODUCTO", "BUSCAR_PRODUCTOS",
                        "LEER_MATERIAS_PRIMAS", "CREAR_MATERIA_PRIMA", "EDITAR_MATERIA_PRIMA", "BUSCAR_MATERIAS_PRIMAS",
                        "LEER_PEDIDO",
                        "LEER_BODEGAS", "BUSCAR_BODEGAS",
                        "LEER_CATEGORIAS",
                        "LEER_ESTADOS",
                        "LEER_ITEMS"
                )
                .map(String::toUpperCase)
                .map(mapaPermisos::get)
                .filter(Objects::nonNull)
                .forEach(permiso -> asignarPermisoARolSiNoExiste(rolOperario, permiso));
        log.info("Permisos asignados a OPERARIO.");

        // CAJERO (Subconjunto - AJUSTA ESTA LISTA SEGÚN NECESITES)
        Stream.of("LEER_PRODUCTOS", "BUSCAR_PRODUCTOS",
                        "LEER_PEDIDO", "CREAR_PEDIDO", "EDITAR_PEDIDO",
                        "AGREGAR_DETALLE_PEDIDO", "EDITAR_DETALLE_PEDIDO",
                        "LEER_FACTURAS", "CREAR_FACTURA", "EDITAR_FACTURA", "BUSCAR_FACTURAS", "BUSCAR_FACTURAS_PENDIENTES",
                        "LEER_CLIENTES", "CREAR_CLIENTE", "EDITAR_CLIENTE", "BUSCAR_CLIENTES",
                        "LEER_ESTADOS"
                )
                .map(String::toUpperCase)
                .map(mapaPermisos::get)
                .filter(Objects::nonNull)
                .forEach(permiso -> asignarPermisoARolSiNoExiste(rolCajero, permiso));
        log.info("Permisos asignados a CAJERO.");


        // --- 4. Crear Usuarios por defecto si no existen ---
        crearUsuarioSiNoExiste("Administrador Principal", "admin@telastech360.com", adminPassword, "ACTIVO", rolAdmin);
        crearUsuarioSiNoExiste("Gerente General", "gerente@telastech360.com", gerentePassword, "ACTIVO", rolGerente);
        crearUsuarioSiNoExiste("Operario Bodega", "operario@telastech360.com", operarioPassword, "ACTIVO", rolOperario);
        crearUsuarioSiNoExiste("Cajero Principal", "cajero@telastech360.com", cajeroPassword, "ACTIVO", rolCajero);

        log.info("Carga de datos iniciales finalizada.");
    }

    // Método auxiliar para crear Rol (sin cambios)
    private Rol crearRolSiNoExiste(String nombre, String descripcion) {
        Optional<Rol> rolOpt = rolRepository.findByNombreIgnoreCase(nombre);
        if (rolOpt.isEmpty()) {
            Rol nuevoRol = new Rol();
            nuevoRol.setNombre(nombre.toUpperCase());
            nuevoRol.setDescripcion(descripcion);
            Rol rolGuardado = rolRepository.save(nuevoRol);
            log.info("Rol '{}' creado con ID: {}", rolGuardado.getNombre(), rolGuardado.getRolId());
            return rolGuardado;
        } else {
            log.debug("Rol '{}' ya existe.", nombre.toUpperCase());
            return rolOpt.get();
        }
    }

    // Método auxiliar para crear Usuario (sin cambios)
    private void crearUsuarioSiNoExiste(String nombre, String email, String password, String estado, Rol rol) {
        if (rol == null) {
            log.error("No se puede crear el usuario '{}' porque el rol proporcionado es nulo.", email);
            return;
        }
        if (!usuarioRepository.existsByEmail(email)) {
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(nombre);
            nuevoUsuario.setEmail(email);
            nuevoUsuario.setPasswordHash(passwordEncoder.encode(password));
            nuevoUsuario.setEstado(estado);
            nuevoUsuario.setRol(rol);
            Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);
            log.info("Usuario '{}' ({}) creado con ID: {} y Rol: {}",
                    nombre, email, usuarioGuardado.getUsuarioId(), rol.getNombre());
        } else {
            log.debug("Usuario con email '{}' ya existe.", email);
        }
    }

    // --- NUEVOS MÉTODOS AUXILIARES ---

    /**
     * Crea un permiso si no existe uno con el mismo nombre (ignorando mayúsculas/minúsculas).
     * Guarda los nombres en mayúsculas por convención.
     * @param nombre Nombre del permiso (se convertirá a mayúsculas).
     * @param descripcion Descripción del permiso.
     * @return La entidad Permiso existente o la recién creada.
     */
    private Permiso crearPermisoSiNoExiste(String nombre, String descripcion) {
        String nombreUpper = nombre.toUpperCase(); // Estandarizar
        Optional<Permiso> permisoOpt = permisoRepository.findByNombreIgnoreCase(nombreUpper);
        if (permisoOpt.isEmpty()) {
            Permiso nuevoPermiso = new Permiso();
            nuevoPermiso.setNombre(nombreUpper);
            nuevoPermiso.setDescripcion(descripcion);
            Permiso permisoGuardado = permisoRepository.save(nuevoPermiso);
            log.info("Permiso '{}' creado con ID: {}", permisoGuardado.getNombre(), permisoGuardado.getPermisoId());
            return permisoGuardado;
        } else {
            log.debug("Permiso '{}' ya existe.", nombreUpper);
            return permisoOpt.get();
        }
    }

    /**
     * Asigna un permiso a un rol si la asignación no existe previamente.
     * Utiliza RolPermisoService para manejar la lógica de asignación.
     * @param rol El Rol al que asignar el permiso.
     * @param permiso El Permiso a asignar.
     */
    private void asignarPermisoARolSiNoExiste(Rol rol, Permiso permiso) {
        if (rol == null || permiso == null) {
            log.error("Intento de asignar permiso nulo o a rol nulo. Rol: {}, Permiso: {}", rol, permiso);
            return;
        }
        // Es más eficiente verificar aquí antes de llamar al servicio que podría lanzar excepción
        if (!rolPermisoService.existeRelacionRolPermiso(rol.getRolId(), permiso.getPermisoId())) {
            try {
                rolPermisoService.asignarPermiso(rol.getRolId(), permiso.getPermisoId());
                // El log de éxito está dentro del servicio
            } catch (Exception e) {
                // Captura cualquier excepción durante la asignación (ej. ResourceNotFound si algo falló)
                log.error("Error al intentar asignar Permiso '{}' (ID:{}) a Rol '{}' (ID:{}): {}",
                        permiso.getNombre(), permiso.getPermisoId(), rol.getNombre(), rol.getRolId(), e.getMessage());
            }
        } else {
            log.trace("Permiso '{}' ya estaba asignado a Rol '{}'. No se reasigna.", permiso.getNombre(), rol.getNombre());
        }
    }
}