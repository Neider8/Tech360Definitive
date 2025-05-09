package com.telastech360.crmTT360.controller;

import com.telastech360.crmTT360.entity.*;
import com.telastech360.crmTT360.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date; // Asegurar importación correcta
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ItemControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ItemRepository itemRepository;
    @Autowired private BodegaRepository bodegaRepository;
    @Autowired private CategoriaRepository categoriaRepository;
    @Autowired private EstadoRepository estadoRepository;
    @Autowired private ProveedorRepository proveedorRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private RolRepository rolRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private Estado estadoActivoItem;
    private Estado estadoActivoBodega;
    private Bodega bodega;
    private Categoria categoria;
    private Proveedor proveedor;
    private Usuario usuario;
    private Item itemCreadoParaTest; // Para asegurar que se crea una vez

    @BeforeEach
    void setUpTestData() {
        // Buscar o crear Estado para Item
        estadoActivoItem = estadoRepository.findByTipoAndValor(Estado.TipoEstado.ITEM, "Activo Item Test Int")
                .orElseGet(() -> estadoRepository.saveAndFlush(new Estado(Estado.TipoEstado.ITEM, "Activo Item Test Int")));

        // Buscar o crear Estado para Bodega (puede ser tipo ACTIVO genérico o específico)
        estadoActivoBodega = estadoRepository.findByTipoAndValor(Estado.TipoEstado.ACTIVO, "Activo Bodega Test")
                .orElseGet(() -> estadoRepository.saveAndFlush(new Estado(Estado.TipoEstado.ACTIVO, "Activo Bodega Test")));


        // Buscar o crear Bodega
        bodega = bodegaRepository.findByNombre("Bodega Test Item Int")
                .orElseGet(() -> {
                    Bodega b = new Bodega();
                    b.setNombre("Bodega Test Item Int");
                    b.setTipoBodega(Bodega.TipoBodega.PRODUCTO_TERMINADO);
                    b.setCapacidadMaxima(1000);
                    b.setUbicacion("Ubic Test Item Int");
                    b.setEstado(estadoActivoBodega); // Usar estado correcto para bodega
                    return bodegaRepository.saveAndFlush(b);
                });

        // Buscar o crear Categoria
        categoria = categoriaRepository.findByNombre("Cat Test Item Int")
                .orElseGet(() -> categoriaRepository.saveAndFlush(new Categoria("Cat Test Item Int")));

        // Buscar o crear Proveedor
        proveedor = proveedorRepository.findByEmail("prov.item.int@test.com")
                .orElseGet(() -> proveedorRepository.saveAndFlush(new Proveedor("Prov Test Item Int", "prov.item.int@test.com")));

        // --- CORRECCIÓN APLICADA AQUÍ ---
        // Buscar o crear Usuario responsable (asegurando que el rol ADMIN existe)
        Rol rolAdmin = rolRepository.findByNombre("ADMIN")
                .orElseGet(() -> { // Usa una lambda para crear y configurar
                    Rol nuevoRol = new Rol(); // 1. Usa el constructor por defecto
                    nuevoRol.setNombre("ADMIN"); // 2. Establece el nombre
                    nuevoRol.setDescripcion("Rol Admin Test"); // 3. Establece la descripción
                    return rolRepository.saveAndFlush(nuevoRol); // 4. Guarda el objeto configurado
                });
        // --- FIN DE LA CORRECCIÓN ---

        usuario = usuarioRepository.findByEmail("admin.item.int@test.com") // Email diferente para evitar conflictos
                .orElseGet(() -> {
                    Usuario u = new Usuario();
                    u.setNombre("Admin Test Loader Item Int");
                    u.setEmail("admin.item.int@test.com");
                    u.setPasswordHash(passwordEncoder.encode("PasswordAdmin123."));
                    u.setRol(rolAdmin);
                    u.setEstado("ACTIVO");
                    return usuarioRepository.saveAndFlush(u);
                });

        // Crear el item de prueba UNA VEZ aquí
        itemCreadoParaTest = crearItemDePrueba();
        assertNotNull(itemCreadoParaTest, "El item de prueba no debe ser nulo después del setup");
        assertNotNull(itemCreadoParaTest.getItemId(), "El ID del item de prueba no debe ser nulo después del setup");

    }

    // Método helper para crear el Item con stock_maximo
    private Item crearItemDePrueba() {
        Producto producto = new Producto();
        // Usar un código que probablemente no exista o añadir nanoTime para asegurar unicidad
        producto.setCodigo("ITEMTESTINT" + System.nanoTime());
        producto.setNombre("Item de Prueba Int");
        producto.setUnidadMedida("Unidad");
        producto.setPrecio(new BigDecimal("99.99"));
        producto.setStockDisponible(50);
        producto.setStockMinimo(5);
        producto.setStockMaximo(250); // <<< --- VALOR AÑADIDO --- >>>
        producto.setBodega(bodega);
        producto.setCategoria(categoria);
        producto.setEstado(estadoActivoItem); // Usar estado correcto para item
        producto.setProveedor(proveedor);
        producto.setUsuario(usuario);
        // Campos específicos de Producto
        producto.setTipoPrenda(Producto.TipoPrenda.CAMISA);
        producto.setTalla(Producto.Talla.M);
        producto.setColor("Azul Test Int");
        producto.setComposicion("Algodon Test Int");
        producto.setFechaFabricacion(new Date(System.currentTimeMillis())); // Usar java.sql.Date

        return itemRepository.saveAndFlush(producto);
    }

    @Test
    @DisplayName("GET /api/items/{id} - Obtener Item Existente")
    @WithMockUser(authorities = {"LEER_ITEMS"})
    void obtenerItemPorId_Exito() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/items/{id}", itemCreadoParaTest.getItemId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.itemId").value(itemCreadoParaTest.getItemId()))
                .andExpect(jsonPath("$.codigo").value(itemCreadoParaTest.getCodigo()))
                .andExpect(jsonPath("$.nombre").value(itemCreadoParaTest.getNombre()))
                .andExpect(jsonPath("$.tipoItem").value("PRODUCTO_TERMINADO")); // Verificar el tipo discriminador
    }

    @Test
    @DisplayName("GET /api/items/{id} - Item No Encontrado")
    @WithMockUser(authorities = {"LEER_ITEMS"})
    void obtenerItemPorId_NoEncontrado() throws Exception {
        Long idInexistente = 99999L;

        mockMvc.perform(MockMvcRequestBuilders.get("/api/items/{id}", idInexistente)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Espera 404
    }

    @Test
    @DisplayName("GET /api/items/{id} - No Autorizado (Sin Autenticar)")
    void obtenerItemPorId_NoAutorizado() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/items/{id}", itemCreadoParaTest.getItemId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized()); // Espera 401
    }

    // Puedes añadir más tests para POST, PUT, DELETE si tienes esos endpoints en ItemController
}