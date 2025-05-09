package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.entity.Categoria;
import com.telastech360.crmTT360.entity.Item;
import com.telastech360.crmTT360.entity.Item.TipoItem; // Importar enum
import com.telastech360.crmTT360.exception.DuplicateResourceException;
import com.telastech360.crmTT360.exception.ResourceNotFoundException;
// import com.telastech360.crmTT360.exception.IllegalOperationException; // Ya no se usa aquí directamente
import com.telastech360.crmTT360.exception.ResourceInUseException; // <-- Importar la nueva excepción
import com.telastech360.crmTT360.repository.CategoriaRepository;
import com.telastech360.crmTT360.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para gestionar la lógica de negocio relacionada con las Categorías de items.
 */
@Service
public class CategoriaService {

    private static final Logger log = LoggerFactory.getLogger(CategoriaService.class);

    private final CategoriaRepository categoriaRepository;
    private final ItemRepository itemRepository; // Para verificar items asociados

    /**
     * Constructor para inyección de dependencias.
     * @param categoriaRepository Repositorio para Categorías.
     * @param itemRepository Repositorio para Items (verificar asociación).
     */
    @Autowired
    public CategoriaService(CategoriaRepository categoriaRepository,
                            ItemRepository itemRepository) {
        this.categoriaRepository = categoriaRepository;
        this.itemRepository = itemRepository;
    }

    /**
     * Crea una nueva categoría.
     * Valida que el nombre sea único (ignorando mayúsculas/minúsculas).
     * @param categoria Entidad Categoria con los datos a crear.
     * @return La entidad Categoria creada y guardada.
     * @throws DuplicateResourceException Si el nombre ya existe.
     */
    @Transactional
    public Categoria crearCategoria(Categoria categoria) {
        log.info("Intentando crear categoría con nombre: '{}'", categoria.getNombre());
        // Usar findByNombreIgnoreCase para validación case-insensitive
        if (categoriaRepository.findByNombreIgnoreCase(categoria.getNombre()).isPresent()) {
            log.warn("Intento de crear categoría con nombre duplicado: {}", categoria.getNombre());
            throw new DuplicateResourceException("Ya existe una categoría con el nombre: " + categoria.getNombre());
        }
        Categoria categoriaGuardada = categoriaRepository.save(categoria);
        log.info("Categoría '{}' creada exitosamente con ID: {}", categoriaGuardada.getNombre(), categoriaGuardada.getCategoriaId());
        return categoriaGuardada;
    }

    /**
     * Obtiene una categoría por su ID.
     * @param id ID de la categoría a buscar.
     * @return La entidad Categoria encontrada.
     * @throws ResourceNotFoundException si la categoría no existe.
     */
    @Transactional(readOnly = true)
    public Categoria obtenerCategoriaPorId(Long id) {
        log.info("Buscando categoría por ID: {}", id);
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Categoría con ID {} no encontrada.", id);
                    return new ResourceNotFoundException("Categoría no encontrada con ID: " + id);
                });
        log.debug("Categoría encontrada: {} (ID: {})", categoria.getNombre(), id);
        return categoria;
    }

    /**
     * Lista todas las categorías registradas, ordenadas por nombre.
     * @return Lista de entidades Categoria.
     */
    @Transactional(readOnly = true)
    public List<Categoria> listarTodasLasCategorias() {
        log.info("Listando todas las categorías...");
        List<Categoria> categorias = categoriaRepository.findAllByOrderByNombreAsc();
        log.debug("Se encontraron {} categorías.", categorias.size());
        return categorias;
    }

    /**
     * Actualiza una categoría existente.
     * Valida que el nuevo nombre no entre en conflicto con otra categoría.
     * @param id ID de la categoría a actualizar.
     * @param categoriaActualizada Entidad Categoria con los datos actualizados.
     * @return La entidad Categoria actualizada.
     * @throws ResourceNotFoundException si la categoría con el ID dado no existe.
     * @throws DuplicateResourceException si el nuevo nombre ya está en uso por otra categoría.
     */
    @Transactional
    public Categoria actualizarCategoria(Long id, Categoria categoriaActualizada) {
        log.info("Intentando actualizar categoría con ID: {}", id);
        Categoria categoriaExistente = obtenerCategoriaPorId(id); // Valida existencia

        // Validar nombre único si cambia (case-insensitive)
        if (!categoriaExistente.getNombre().equalsIgnoreCase(categoriaActualizada.getNombre())) {
            log.debug("El nombre de la categoría ID {} ha cambiado a '{}'. Verificando disponibilidad...", id, categoriaActualizada.getNombre());
            if (categoriaRepository.findByNombreIgnoreCase(categoriaActualizada.getNombre()).isPresent()) {
                log.warn("Conflicto: El nombre '{}' ya está en uso por otra categoría.", categoriaActualizada.getNombre());
                throw new DuplicateResourceException("Ya existe una categoría con el nombre: " + categoriaActualizada.getNombre());
            }
            log.debug("Nombre '{}' disponible.", categoriaActualizada.getNombre());
            categoriaExistente.setNombre(categoriaActualizada.getNombre());
        }

        categoriaExistente.setDescripcion(categoriaActualizada.getDescripcion());
        log.debug("Descripción actualizada para categoría ID {}.", id);

        Categoria categoriaGuardada = categoriaRepository.save(categoriaExistente);
        log.info("Categoría ID {} actualizada exitosamente.", id);
        return categoriaGuardada;
    }

    /**
     * Elimina una categoría por su ID.
     * Verifica que la categoría no esté asociada a ningún item antes de eliminar.
     * @param id ID de la categoría a eliminar.
     * @throws ResourceNotFoundException si la categoría no existe.
     * @throws ResourceInUseException si la categoría tiene items asociados.
     */
    @Transactional
    public void eliminarCategoria(Long id) {
        log.info("Intentando eliminar categoría con ID: {}", id);
        Categoria categoria = obtenerCategoriaPorId(id); // Valida existencia

        // Validar que no esté en uso por Items
        List<Item> items = itemRepository.findByCategoria(categoria); // Asume que existe este método en ItemRepository
        if (!items.isEmpty()) {
            log.warn("Intento de eliminar categoría ID {} ('{}') que está asociada a {} item(s).", id, categoria.getNombre(), items.size());
            // --- CAMBIO A LA NUEVA EXCEPCIÓN ---
            throw new ResourceInUseException("No se puede eliminar la categoría '" + categoria.getNombre() + "' porque tiene " + items.size() + " items asociados.");
        }
        log.debug("La categoría ID {} ('{}') no tiene items asociados.", id, categoria.getNombre());

        categoriaRepository.delete(categoria);
        log.info("Categoría ID {} ('{}') eliminada exitosamente.", id, categoria.getNombre());
    }

    // ========== MÉTODOS ADICIONALES ========== //

    /**
     * Busca categorías cuyo nombre contenga el fragmento dado (case-sensitive).
     * @param nombre Fragmento del nombre a buscar.
     * @return Lista de entidades Categoria coincidentes.
     */
    @Transactional(readOnly = true)
    public List<Categoria> buscarCategoriasPorNombre(String nombre) {
        log.info("Buscando categorías por nombre que contenga: '{}'", nombre);
        List<Categoria> categorias = categoriaRepository.findByNombreContaining(nombre);
        log.debug("Búsqueda por nombre '{}' encontró {} categorías.", nombre, categorias.size());
        return categorias;
    }

    /**
     * Busca categorías cuya descripción contenga el texto dado (case-insensitive).
     * @param descripcion Texto a buscar en la descripción.
     * @return Lista de entidades Categoria coincidentes.
     */
    @Transactional(readOnly = true)
    public List<Categoria> buscarCategoriasPorDescripcion(String descripcion) {
        log.info("Buscando categorías por descripción que contenga: '{}'", descripcion);
        List<Categoria> categorias = categoriaRepository.buscarPorDescripcion(descripcion); // JPQL con LIKE
        log.debug("Búsqueda por descripción '{}' encontró {} categorías.", descripcion, categorias.size());
        return categorias;
    }

    /**
     * Lista las categorías junto con el número de items asociados a cada una.
     * @return Lista de Object[], donde cada array contiene [Categoria (entidad), Long (conteo)].
     */
    @Transactional(readOnly = true)
    public List<Object[]> listarCategoriasConConteoItems() {
        log.info("Listando categorías con conteo de items asociados...");
        List<Object[]> resultado = categoriaRepository.findCategoriasWithItemCount();
        log.debug("Se obtuvo conteo de items para {} categorías.", resultado.size());
        return resultado;
    }

    /**
     * Busca categorías que contienen items de un tipo específico (MATERIA_PRIMA o PRODUCTO_TERMINADO).
     * @param tipoItem El tipo de item a filtrar.
     * @return Lista de entidades Categoria únicas que cumplen el criterio.
     */
    @Transactional(readOnly = true)
    public List<Categoria> listarCategoriasPorTipoItem(TipoItem tipoItem) {
        log.info("Buscando categorías que contengan items del tipo: {}", tipoItem);
        List<Categoria> categorias = categoriaRepository.findByTipoItem(tipoItem); // Asume método en repo
        log.debug("Se encontraron {} categorías con items del tipo {}.", categorias.size(), tipoItem);
        return categorias;
    }

    /**
     * Verifica si existe una categoría con el nombre dado (case-insensitive).
     * @param nombre Nombre de la categoría a verificar.
     * @return true si existe, false en caso contrario.
     */
    @Transactional(readOnly = true)
    public boolean existeCategoriaConNombre(String nombre) {
        log.debug("Verificando existencia de categoría con nombre: '{}'", nombre);
        // Usar findByNombreIgnoreCase para chequeo case-insensitive
        return categoriaRepository.findByNombreIgnoreCase(nombre).isPresent();
    }
}