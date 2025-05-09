package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.dto.MateriaPrimaDTO; // Importar DTO
import com.telastech360.crmTT360.entity.*;
import com.telastech360.crmTT360.exception.*; // Importar excepciones
import com.telastech360.crmTT360.mapper.MateriaPrimaMapper; // Importar Mapper
import com.telastech360.crmTT360.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors; // Para operaciones de stream
import java.math.BigDecimal; // Importar si se usa para cálculos
import java.sql.Date; // Importar si se usa para fechas

/**
 * Servicio para gestionar la lógica de negocio relacionada con la Materia Prima.
 * Extiende la lógica de Items y añade validaciones/operaciones específicas.
 */
@Service
public class MateriaPrimaService {

    private static final Logger log = LoggerFactory.getLogger(MateriaPrimaService.class);

    private final MateriaPrimaRepository materiaPrimaRepository;
    private final ItemRepository itemRepository;
    private final ProveedorRepository proveedorRepository;
    private final BodegaRepository bodegaRepository;
    private final CategoriaRepository categoriaRepository;
    private final EstadoRepository estadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MateriaPrimaMapper materiaPrimaMapper; // Inyectar Mapper

    @Autowired
    public MateriaPrimaService(MateriaPrimaRepository materiaPrimaRepository,
                               ItemRepository itemRepository,
                               ProveedorRepository proveedorRepository,
                               BodegaRepository bodegaRepository,
                               CategoriaRepository categoriaRepository,
                               EstadoRepository estadoRepository,
                               UsuarioRepository usuarioRepository,
                               MateriaPrimaMapper materiaPrimaMapper) { // Inyectar Mapper
        this.materiaPrimaRepository = materiaPrimaRepository;
        this.itemRepository = itemRepository;
        this.proveedorRepository = proveedorRepository;
        this.bodegaRepository = bodegaRepository;
        this.categoriaRepository = categoriaRepository;
        this.estadoRepository = estadoRepository;
        this.usuarioRepository = usuarioRepository;
        this.materiaPrimaMapper = materiaPrimaMapper; // Asignar Mapper
    }

    /**
     * Crea una nueva Materia Prima a partir de un DTO.
     * @param materiaPrimaDto DTO con la información de la materia prima a crear.
     * @return La entidad MateriaPrima creada y guardada.
     * @throws DuplicateResourceException Si el código ya existe.
     * @throws ResourceNotFoundException Si alguna entidad relacionada no existe.
     * @throws InvalidDataException Si el tipo de material es inválido.
     */
    @Transactional
    public MateriaPrima crearMateriaPrima(MateriaPrimaDTO materiaPrimaDto) { // Firma cambiada a DTO
        log.info("Intentando crear materia prima con código: {}", materiaPrimaDto.getCodigo());
        if (itemRepository.existsByCodigo(materiaPrimaDto.getCodigo())) {
            log.warn("Código de ítem duplicado: {}", materiaPrimaDto.getCodigo());
            throw new DuplicateResourceException("Ya existe un ítem con el código: " + materiaPrimaDto.getCodigo());
        }

        // Cargar relaciones base
        log.debug("Buscando relaciones base para MP DTO código: {}", materiaPrimaDto.getCodigo());
        Bodega bodega = bodegaRepository.findById(materiaPrimaDto.getBodegaId())
                .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada con ID: " + materiaPrimaDto.getBodegaId()));
        Categoria categoria = categoriaRepository.findById(materiaPrimaDto.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + materiaPrimaDto.getCategoriaId()));
        Estado estado = estadoRepository.findById(materiaPrimaDto.getEstadoId())
                .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado con ID: " + materiaPrimaDto.getEstadoId()));
        Proveedor proveedorGeneral = proveedorRepository.findById(materiaPrimaDto.getProveedorId())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor general no encontrado con ID: " + materiaPrimaDto.getProveedorId()));
        Usuario usuario = usuarioRepository.findById(materiaPrimaDto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + materiaPrimaDto.getUsuarioId()));
        log.debug("Relaciones base encontradas para MP DTO código: {}", materiaPrimaDto.getCodigo());

        // Cargar Proveedor de Tela (opcional)
        Proveedor proveedorTela = null;
        if (materiaPrimaDto.getProveedorTelaId() != null) {
            log.debug("Buscando proveedor de tela con ID: {}", materiaPrimaDto.getProveedorTelaId());
            proveedorTela = proveedorRepository.findById(materiaPrimaDto.getProveedorTelaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Proveedor de tela no encontrado con ID: " + materiaPrimaDto.getProveedorTelaId()));
            log.debug("Proveedor de tela '{}' encontrado.", proveedorTela.getNombre());
        }

        // Mapear DTO a Entidad
        MateriaPrima materiaPrima = materiaPrimaMapper.toEntity(
                materiaPrimaDto, bodega, categoria, estado, proveedorGeneral, usuario, proveedorTela
        );
        // El tipo de Item se setea en el mapper/constructor

        MateriaPrima materiaPrimaGuardada = materiaPrimaRepository.save(materiaPrima);
        log.info("Materia prima '{}' (Código: {}) creada exitosamente con ID: {}",
                materiaPrimaGuardada.getNombre(), materiaPrimaGuardada.getCodigo(), materiaPrimaGuardada.getItemId());
        return materiaPrimaGuardada;
    }

    @Transactional(readOnly = true)
    public MateriaPrima obtenerMateriaPrimaPorId(Long id) {
        log.info("Buscando materia prima por ID: {}", id);
        return materiaPrimaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Materia prima con ID {} no encontrada.", id);
                    return new ResourceNotFoundException("Materia prima no encontrada con ID: " + id);
                });
    }

    @Transactional(readOnly = true)
    public List<MateriaPrima> listarTodasLasMateriasPrimas() {
        log.info("Listando todas las materias primas...");
        List<MateriaPrima> materiasPrimas = materiaPrimaRepository.findAllByOrderByNombreAsc();
        log.debug("Se encontraron {} materias primas.", materiasPrimas.size());
        return materiasPrimas;
    }

    /**
     * Actualiza una materia prima existente a partir de un DTO.
     * @param id ID de la materia prima a actualizar.
     * @param materiaPrimaDto DTO con los nuevos datos.
     * @return La entidad MateriaPrima actualizada.
     * @throws ResourceNotFoundException Si la materia prima o alguna relación no existen.
     * @throws DuplicateResourceException Si el nuevo código ya está en uso.
     * @throws InvalidDataException Si el tipo de material es inválido.
     */
    @Transactional
    public MateriaPrima actualizarMateriaPrima(Long id, MateriaPrimaDTO materiaPrimaDto) { // Firma cambiada a DTO
        log.info("Intentando actualizar materia prima con ID: {}", id);
        MateriaPrima materiaPrimaExistente = obtenerMateriaPrimaPorId(id);

        // Validar código único si cambia
        if (!materiaPrimaExistente.getCodigo().equalsIgnoreCase(materiaPrimaDto.getCodigo())) {
            log.debug("Código cambiado para MP ID {}. Verificando '{}'...", id, materiaPrimaDto.getCodigo());
            if (itemRepository.existsByCodigo(materiaPrimaDto.getCodigo())) {
                log.warn("Conflicto código al actualizar MP ID {}: '{}' ya existe.", id, materiaPrimaDto.getCodigo());
                throw new DuplicateResourceException("El código ya está registrado: " + materiaPrimaDto.getCodigo());
            }
            log.debug("Código '{}' disponible.", materiaPrimaDto.getCodigo());
            // El mapper se encargará de actualizar el código en la entidad existente
        }

        // Cargar relaciones base actualizadas
        log.debug("Buscando relaciones base actualizadas para MP ID: {}", id);
        Bodega bodega = bodegaRepository.findById(materiaPrimaDto.getBodegaId())
                .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada con ID: " + materiaPrimaDto.getBodegaId()));
        Categoria categoria = categoriaRepository.findById(materiaPrimaDto.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + materiaPrimaDto.getCategoriaId()));
        Estado estado = estadoRepository.findById(materiaPrimaDto.getEstadoId())
                .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado con ID: " + materiaPrimaDto.getEstadoId()));
        Proveedor proveedorGeneral = proveedorRepository.findById(materiaPrimaDto.getProveedorId())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor general no encontrado con ID: " + materiaPrimaDto.getProveedorId()));
        Usuario usuario = usuarioRepository.findById(materiaPrimaDto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + materiaPrimaDto.getUsuarioId()));
        log.debug("Relaciones base actualizadas encontradas para MP ID: {}", id);

        // Cargar Proveedor de Tela actualizado (opcional)
        Proveedor proveedorTela = null;
        if (materiaPrimaDto.getProveedorTelaId() != null) {
            log.debug("Buscando proveedor de tela actualizado con ID: {}", materiaPrimaDto.getProveedorTelaId());
            proveedorTela = proveedorRepository.findById(materiaPrimaDto.getProveedorTelaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Proveedor de tela no encontrado con ID: " + materiaPrimaDto.getProveedorTelaId()));
            log.debug("Proveedor de tela actualizado '{}' encontrado.", proveedorTela.getNombre());
        }

        // Usar el mapper para actualizar la entidad existente
        materiaPrimaMapper.updateEntityFromDTO(
                materiaPrimaDto, materiaPrimaExistente, bodega, categoria, estado, proveedorGeneral, usuario, proveedorTela
        );
        log.debug("Entidad MateriaPrima ID {} actualizada desde DTO.", id);

        MateriaPrima materiaPrimaGuardada = materiaPrimaRepository.save(materiaPrimaExistente);
        log.info("Materia prima ID {} actualizada exitosamente.", id);
        return materiaPrimaGuardada;
    }

    @Transactional
    public void eliminarMateriaPrima(Long id) {
        log.info("Intentando eliminar materia prima con ID: {}", id);
        MateriaPrima materiaPrima = obtenerMateriaPrimaPorId(id);

        if (itemRepository.existeEnPedidosActivos(id)) {
            log.warn("Intento de eliminar MP ID {} ('{}') en pedidos activos.", id, materiaPrima.getNombre());
            throw new IllegalOperationException("No se puede eliminar la materia prima '" + materiaPrima.getNombre() + "' porque está en pedidos activos.");
        }
        log.debug("MP ID {} no está en pedidos activos.", id);

        materiaPrimaRepository.delete(materiaPrima);
        log.info("Materia prima ID {} ('{}') eliminada exitosamente.", id, materiaPrima.getNombre());
    }

    @Transactional(readOnly = true)
    public List<MateriaPrima> buscarPorTipoMaterial(MateriaPrima.TipoMaterial tipoMaterial) {
        log.info("Buscando materias primas por tipo: {}", tipoMaterial);
        List<MateriaPrima> resultado = materiaPrimaRepository.findByTipoMaterial(tipoMaterial);
        log.debug("Se encontraron {} materias primas del tipo {}", resultado.size(), tipoMaterial);
        return resultado;
    }

    @Transactional(readOnly = true)
    public List<MateriaPrima> buscarPorProveedor(Long proveedorId) {
        log.info("Buscando materias primas por proveedor general ID: {}", proveedorId);
        Proveedor proveedor = proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + proveedorId));

        List<Item> itemsDelProveedor = itemRepository.findByProveedor(proveedor); // Busca Items por entidad Proveedor

        List<MateriaPrima> resultado = itemsDelProveedor.stream()
                .filter(item -> item instanceof MateriaPrima) // Filtra solo las que son MateriaPrima
                .map(item -> (MateriaPrima) item) // Castea a MateriaPrima
                .collect(Collectors.toList()); // Recolecta en una lista

        log.debug("Se encontraron {} materias primas para proveedor ID {}", resultado.size(), proveedorId);
        return resultado;
    }


    @Transactional(readOnly = true)
    public List<MateriaPrima> buscarMateriasPrimasPorVencer(Date fechaLimite) { // Usar java.sql.Date si el repo lo necesita
        log.info("Buscando materias primas por vencer antes de: {}", fechaLimite);
        List<Item> itemsPorVencer = itemRepository.findItemsPorVencer(new Date(System.currentTimeMillis()), fechaLimite);
        List<MateriaPrima> resultado = itemsPorVencer.stream()
                .filter(item -> item instanceof MateriaPrima)
                .map(item -> (MateriaPrima) item)
                .collect(Collectors.toList());
        log.debug("Se encontraron {} materias primas por vencer.", resultado.size());
        return resultado;
    }

    @Transactional(readOnly = true)
    public List<Object[]> obtenerResumenStockPorTipoMaterial() {
        log.info("Obteniendo resumen de stock por tipo de material...");
        // Solución alternativa usando stream (si no tienes query específica)
        List<Object[]> resultado = itemRepository.findAll().stream()
                .filter(item -> item instanceof MateriaPrima)
                .map(item -> (MateriaPrima) item)
                .collect(Collectors.groupingBy(MateriaPrima::getTipoMaterial,
                        Collectors.summingLong(item -> (long)item.getStockDisponible()))) // Asegurar que stock sea Long si summingLong espera Long
                .entrySet().stream()
                .map(entry -> new Object[]{entry.getKey(), entry.getValue()})
                .collect(Collectors.toList());
        log.debug("Resumen de stock por tipo obtenido con {} entradas.", resultado.size());
        return resultado;
    }
}