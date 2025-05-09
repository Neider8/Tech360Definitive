package com.telastech360.crmTT360.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telastech360.crmTT360.entity.*;
import com.telastech360.crmTT360.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder; // Importar
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date; // Importar java.sql.Date
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Usar Transactional para rollback automático simplifica el tearDown
class CategoriaControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private CategoriaRepository categoriaRepository;
    @Autowired private ItemRepository itemRepository;
    @Autowired private BodegaRepository bodegaRepository;
    @Autowired private EstadoRepository estadoRepository;
    @Autowired private ProveedorRepository proveedorRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private RolRepository rolRepository; // Añadir
    @Autowired private PasswordEncoder passwordEncoder; // Añadir

    private Categoria categoriaParaBorrar;
    private Categoria categoriaEnUso;
    private Item itemEnUso;

    @BeforeEach
    void setUp() {
        // Asegurar dependencias básicas
        Estado estadoActivo = estadoRepository.findByTipoAndValor(Estado.TipoEstado.ACTIVO, "Activo Test Categoria")
                .orElseGet(() -> estadoRepository.saveAndFlush(new Estado(Estado.TipoEstado.ACTIVO, "Activo Test Categoria")));
        Estado estadoItem = estadoRepository.findByTipoAndValor(Estado.TipoEstado.ITEM, "Item Activo Test Categoria")
                .orElseGet(() -> estadoRepository.saveAndFlush(new Estado(Estado.TipoEstado.ITEM, "Item Activo Test Categoria")));

        Bodega bodega = bodegaRepository.findByNombre("Bodega Cat Test")
                .orElseGet(() -> {
                    Bodega b = new Bodega();
                    b.setNombre("Bodega Cat Test");
                    b.setTipoBodega(Bodega.TipoBodega.TEMPORAL);
                    b.setCapacidadMaxima(100);
                    b.setUbicacion("Ubic Cat Test");
                    b.setEstado(estadoActivo); // Asignar estado válido
                    return bodegaRepository.saveAndFlush(b);
                });

        Proveedor proveedor = proveedorRepository.findByEmail("prov.cat.test@example.com")
                .orElseGet(() -> proveedorRepository.saveAndFlush(new Proveedor("Prov Cat Test", "prov.cat.test@example.com")));

        // --- CORRECCIÓN APLICADA AQUÍ ---
        Rol rolAdmin = rolRepository.findByNombre("ADMIN")
                .orElseGet(() -> { // Usa una lambda para crear y configurar
                    Rol nuevoRol = new Rol(); // 1. Usa el constructor por defecto
                    nuevoRol.setNombre("ADMIN"); // 2. Establece el nombre
                    nuevoRol.setDescripcion("Admin para test cat"); // 3. Establece la descripción
                    return rolRepository.saveAndFlush(nuevoRol); // 4. Guarda el objeto configurado
                });
        // --- FIN DE LA CORRECCIÓN ---

        Usuario usuario = usuarioRepository.findByEmail("admin.cat.test@example.com")
                .orElseGet(() -> {
                    Usuario u = new Usuario();
                    u.setNombre("Admin Test Cat");
                    u.setEmail("admin.cat.test@example.com");
                    u.setPasswordHash(passwordEncoder.encode("password"));
                    u.setRol(rolAdmin);
                    u.setEstado("ACTIVO");
                    return usuarioRepository.saveAndFlush(u);
                });


        // Crear categorías
        categoriaParaBorrar = new Categoria("Temporal Borrable Cat");
        categoriaParaBorrar = categoriaRepository.saveAndFlush(categoriaParaBorrar);

        categoriaEnUso = new Categoria("Temporal En Uso Cat");
        categoriaEnUso = categoriaRepository.saveAndFlush(categoriaEnUso);

        // Crear Producto asociado a categoriaEnUso
        Producto productoEnUso = new Producto();
        productoEnUso.setCodigo("ITEMCATTEST" + System.nanoTime()); // Código único
        productoEnUso.setNombre("Item en Cat Test");
        productoEnUso.setUnidadMedida("unid");
        productoEnUso.setPrecio(BigDecimal.ONE);
        productoEnUso.setStockDisponible(10);
        productoEnUso.setStockMinimo(1);
        productoEnUso.setStockMaximo(100); // <<< --- VALOR AÑADIDO --- >>>
        productoEnUso.setEstado(estadoItem); // Estado para Item
        productoEnUso.setProveedor(proveedor);
        productoEnUso.setCategoria(categoriaEnUso); // Asociado a la categoría en uso
        productoEnUso.setBodega(bodega);
        productoEnUso.setUsuario(usuario);
        productoEnUso.setTipoPrenda(Producto.TipoPrenda.OTROS);
        productoEnUso.setTalla(Producto.Talla.UNICA);
        productoEnUso.setColor("Test Color Cat");
        productoEnUso.setComposicion("Test Comp Cat");
        productoEnUso.setFechaFabricacion(new Date(System.currentTimeMillis())); // Usar java.sql.Date

        itemEnUso = itemRepository.saveAndFlush(productoEnUso); // Guardar el item
    }

    /* Ya no es necesario con @Transactional en la clase
    @AfterEach
    void tearDown() {
        // Limpieza manual si NO usas @Transactional
        if (itemEnUso != null && itemEnUso.getItemId() != null) {
            itemRepository.findById(itemEnUso.getItemId()).ifPresent(item -> itemRepository.delete(item));
        }
        if (categoriaEnUso != null && categoriaEnUso.getCategoriaId() != null) {
            categoriaRepository.findById(categoriaEnUso.getCategoriaId()).ifPresent(cat -> categoriaRepository.delete(cat));
        }
        if (categoriaParaBorrar != null && categoriaParaBorrar.getCategoriaId() != null) {
            categoriaRepository.findById(categoriaParaBorrar.getCategoriaId()).ifPresent(cat -> categoriaRepository.delete(cat));
        }
        // Limpiar otras entidades creadas si es necesario y no son parte del DataLoader
    }
    */

    @Test
    @DisplayName("DELETE /api/categorias/{id} - Eliminación Exitosa")
    @WithMockUser(authorities = {"ELIMINAR_CATEGORIA"}) // Usar permiso específico
    void eliminarCategoria_Exito() throws Exception {
        assertNotNull(categoriaParaBorrar, "La categoría para borrar no debe ser nula");
        assertNotNull(categoriaParaBorrar.getCategoriaId(), "El ID de la categoría para borrar no debe ser nulo");

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/categorias/{id}", categoriaParaBorrar.getCategoriaId()))
                .andExpect(status().isNoContent()); // Espera 204 No Content
    }

    @Test
    @DisplayName("DELETE /api/categorias/{id} - Categoría No Encontrada")
    @WithMockUser(authorities = {"ELIMINAR_CATEGORIA"})
    void eliminarCategoria_NoEncontrada() throws Exception {
        Long idInexistente = 9999L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/categorias/{id}", idInexistente))
                .andExpect(status().isNotFound()); // Espera 404 Not Found
    }

    @Test
    @DisplayName("DELETE /api/categorias/{id} - Categoría En Uso")
    @WithMockUser(authorities = {"ELIMINAR_CATEGORIA"})
    void eliminarCategoria_EnUso() throws Exception {
        assertNotNull(categoriaEnUso, "La categoría en uso no debe ser nula");
        assertNotNull(categoriaEnUso.getCategoriaId(), "El ID de la categoría en uso no debe ser nulo");
        assertNotNull(itemEnUso, "El ítem en uso no debe ser nulo");

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/categorias/{id}", categoriaEnUso.getCategoriaId()))
                .andExpect(status().isConflict()); // Espera 409 Conflict
    }

    @Test
    @DisplayName("DELETE /api/categorias/{id} - No Autorizado")
    @WithMockUser(roles = {"OPERARIO"}) // Rol sin permiso para eliminar
    void eliminarCategoria_NoAutorizado() throws Exception {
        assertNotNull(categoriaParaBorrar, "La categoría para borrar no debe ser nula");
        assertNotNull(categoriaParaBorrar.getCategoriaId(), "El ID de la categoría para borrar no debe ser nulo");

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/categorias/{id}", categoriaParaBorrar.getCategoriaId()))
                .andExpect(status().isForbidden()); // Espera 403 Forbidden
    }
}