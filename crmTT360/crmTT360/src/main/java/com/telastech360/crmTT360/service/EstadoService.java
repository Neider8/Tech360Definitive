package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.dto.EstadoDTO;
import com.telastech360.crmTT360.entity.Estado;
import com.telastech360.crmTT360.entity.Estado.TipoEstado;
import com.telastech360.crmTT360.exception.DuplicateResourceException;
import com.telastech360.crmTT360.exception.IllegalOperationException;
import com.telastech360.crmTT360.exception.ResourceNotFoundException;
import com.telastech360.crmTT360.exception.UsedStateException; // Importar excepción personalizada
import com.telastech360.crmTT360.mapper.EstadoMapper;
import com.telastech360.crmTT360.repository.EstadoRepository;
// Importar repositorios necesarios para verificar el uso del estado
import com.telastech360.crmTT360.repository.BodegaRepository;
import com.telastech360.crmTT360.repository.ItemRepository;
import com.telastech360.crmTT360.repository.PedidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar la lógica de negocio relacionada con los Estados.
 */
@Service // <-- Asegúrate que la clase se llame EstadoService
public class EstadoService {

    private static final Logger log = LoggerFactory.getLogger(EstadoService.class); // <-- Usar EstadoService.class

    private final EstadoRepository estadoRepository;
    private final EstadoMapper estadoMapper;
    // Inyectar otros repositorios para verificar si el estado está en uso
    private final BodegaRepository bodegaRepository;
    private final ItemRepository itemRepository;
    private final PedidoRepository pedidoRepository;


    @Autowired
    public EstadoService(EstadoRepository estadoRepository,
                         EstadoMapper estadoMapper,
                         BodegaRepository bodegaRepository,
                         ItemRepository itemRepository,
                         PedidoRepository pedidoRepository) {
        this.estadoRepository = estadoRepository;
        this.estadoMapper = estadoMapper;
        this.bodegaRepository = bodegaRepository;
        this.itemRepository = itemRepository;
        this.pedidoRepository = pedidoRepository;
    }

    /**
     * Crea un nuevo estado.
     * Valida unicidad de la combinación tipoEstado y valor.
     * @param estadoDTO DTO con los datos del estado.
     * @return DTO del estado creado.
     * @throws DuplicateResourceException Si ya existe un estado con ese tipo y valor.
     */
    @Transactional
    public EstadoDTO crearEstado(EstadoDTO estadoDTO) {
        log.info("Intentando crear estado con tipo '{}' y valor '{}'", estadoDTO.getTipoEstado(), estadoDTO.getValor());
        if (estadoRepository.existsByTipoEstadoAndValor(estadoDTO.getTipoEstado(), estadoDTO.getValor())) {
            log.warn("Intento de crear estado duplicado: Tipo={}, Valor={}", estadoDTO.getTipoEstado(), estadoDTO.getValor());
            throw new DuplicateResourceException("Ya existe un estado con tipo '" + estadoDTO.getTipoEstado() + "' y valor '" + estadoDTO.getValor() + "'");
        }
        Estado estado = estadoMapper.toEntity(estadoDTO);
        Estado estadoGuardado = estadoRepository.save(estado);
        log.info("Estado Tipo={}, Valor='{}' creado con ID: {}", estadoGuardado.getTipoEstado(), estadoGuardado.getValor(), estadoGuardado.getEstadoId());
        return estadoMapper.toDTO(estadoGuardado);
    }

    /**
     * Obtiene un estado por su ID.
     * @param id ID del estado.
     * @return DTO del estado encontrado.
     * @throws ResourceNotFoundException Si no se encuentra el estado.
     */
    @Transactional(readOnly = true)
    public EstadoDTO obtenerEstadoPorId(Long id) {
        log.info("Buscando estado por ID: {}", id);
        Estado estado = estadoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Estado con ID {} no encontrado.", id);
                    return new ResourceNotFoundException("Estado no encontrado con ID: " + id);
                });
        log.debug("Estado encontrado: Tipo={}, Valor='{}'", estado.getTipoEstado(), estado.getValor());
        return estadoMapper.toDTO(estado);
    }

    /**
     * Lista todos los estados registrados.
     * @return Lista de DTOs de todos los estados.
     */
    @Transactional(readOnly = true)
    public List<EstadoDTO> listarTodosLosEstados() {
        log.info("Listando todos los estados...");
        List<Estado> estados = estadoRepository.findAll();
        log.debug("Se encontraron {} estados.", estados.size());
        return estados.stream()
                .map(estadoMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza un estado existente.
     * Valida unicidad de la combinación tipoEstado y valor si cambia.
     * @param id ID del estado a actualizar.
     * @param estadoDTO DTO con los datos actualizados.
     * @return DTO del estado actualizado.
     * @throws ResourceNotFoundException Si el estado a actualizar no existe.
     * @throws DuplicateResourceException Si la nueva combinación tipo/valor ya existe en otro estado.
     */
    @Transactional
    public EstadoDTO actualizarEstado(Long id, EstadoDTO estadoDTO) {
        log.info("Intentando actualizar estado ID: {}", id);
        Estado estadoExistente = estadoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Estado ID {} no encontrado para actualizar.", id);
                    return new ResourceNotFoundException("Estado no encontrado con ID: " + id);
                });

        // Verificar si la combinación tipo/valor cambió y si la nueva combinación ya existe
        if (!(estadoExistente.getTipoEstado() == estadoDTO.getTipoEstado() && estadoExistente.getValor().equalsIgnoreCase(estadoDTO.getValor()))) {
            log.debug("La combinación tipo/valor cambió para estado ID {}. Verificando disponibilidad...", id);
            if (estadoRepository.existsByTipoEstadoAndValor(estadoDTO.getTipoEstado(), estadoDTO.getValor())) {
                log.warn("Conflicto al actualizar estado ID {}: La combinación Tipo={}, Valor='{}' ya existe.", id, estadoDTO.getTipoEstado(), estadoDTO.getValor());
                throw new DuplicateResourceException("Ya existe un estado con tipo '" + estadoDTO.getTipoEstado() + "' y valor '" + estadoDTO.getValor() + "'");
            }
            log.debug("Nueva combinación Tipo={}, Valor='{}' disponible.", estadoDTO.getTipoEstado(), estadoDTO.getValor());
        }

        // Actualizar la entidad existente
        estadoMapper.updateEntityFromDTO(estadoDTO, estadoExistente);
        log.debug("Campos de estado ID {} actualizados desde DTO.", id);

        Estado estadoActualizado = estadoRepository.save(estadoExistente);
        log.info("Estado ID {} actualizado exitosamente a Tipo={}, Valor='{}'.", id, estadoActualizado.getTipoEstado(), estadoActualizado.getValor());
        return estadoMapper.toDTO(estadoActualizado);
    }

    /**
     * Elimina un estado por su ID.
     * Verifica si el estado está siendo utilizado por alguna otra entidad antes de eliminar.
     * @param id ID del estado a eliminar.
     * @throws ResourceNotFoundException Si el estado no existe.
     * @throws UsedStateException Si el estado está en uso.
     */
    @Transactional
    public void eliminarEstado(Long id) {
        log.info("Intentando eliminar estado con ID: {}", id);
        Estado estado = estadoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Estado ID {} no encontrado para eliminar.", id);
                    return new ResourceNotFoundException("Estado no encontrado con ID: " + id);
                });

        // Verificar si el estado está en uso
        verificarUsoEstado(id, estado.getValor());

        estadoRepository.delete(estado);
        log.info("Estado ID {} (Tipo={}, Valor='{}') eliminado exitosamente.", id, estado.getTipoEstado(), estado.getValor());
    }

    /**
     * Verifica si un estado específico está siendo utilizado por otras entidades.
     * Lanza UsedStateException si está en uso.
     * @param estadoId ID del estado a verificar.
     * @param estadoValor Valor del estado (para mensajes de error).
     * @throws UsedStateException Si el estado está en uso.
     */
    private void verificarUsoEstado(Long estadoId, String estadoValor) {
        log.debug("Verificando uso del estado ID: {}", estadoId);
        boolean enUso = false;
        String entidadEnUso = "";

        if (bodegaRepository.existsByEstadoId(estadoId)) {
            enUso = true;
            entidadEnUso = "Bodegas";
        } else if (itemRepository.existsByEstadoId(estadoId)) {
            enUso = true;
            entidadEnUso = "Items";
        } else if (pedidoRepository.existsByEstadoId(estadoId)) {
            enUso = true;
            entidadEnUso = "Pedidos";
        }
        // Añadir más verificaciones si otras entidades usan Estado

        if (enUso) {
            log.warn("Intento de eliminar estado ID {} ('{}') que está en uso por {}.", estadoId, estadoValor, entidadEnUso);
            throw new UsedStateException("No se puede eliminar el estado '" + estadoValor + "' (ID: " + estadoId + ") porque está en uso por " + entidadEnUso + ".");
        }
        log.debug("Estado ID {} no está en uso.", estadoId);
    }


    /**
     * Lista los estados filtrados por un tipo específico.
     * @param tipo El TipoEstado a filtrar.
     * @return Lista de entidades Estado.
     */
    @Transactional(readOnly = true)
    public List<Estado> listarEstadosPorTipo(TipoEstado tipo) {
        log.info("Listando estados por tipo: {}", tipo);
        List<Estado> estados = estadoRepository.findByTipoEstadoOrderByValorAsc(tipo);
        log.debug("Se encontraron {} estados del tipo {}", estados.size(), tipo);
        return estados; // Devuelve entidades, el controlador mapea a DTO
    }

    /**
     * Lista todos los posibles valores del enum TipoEstado.
     * @return Lista de enums TipoEstado.
     */
    @Transactional(readOnly = true)
    public List<TipoEstado> listarTiposEstadoDisponibles() {
        log.info("Listando todos los tipos de estado disponibles...");
        // Se puede obtener directamente del Enum
        List<TipoEstado> tipos = Arrays.asList(TipoEstado.values());
        log.debug("Tipos de estado disponibles: {}", tipos);
        return tipos;
    }

    /**
     * Busca estados cuyo campo 'valor' contenga el texto proporcionado.
     * @param valor Texto a buscar (case-insensitive).
     * @return Lista de entidades Estado.
     */
    @Transactional(readOnly = true)
    public List<Estado> buscarEstadosPorValor(String valor) {
        log.info("Buscando estados por valor que contenga: '{}'", valor);
        List<Estado> estados = estadoRepository.findByValorContaining(valor); // Asume método en repo (puede necesitar IgnoreCase)
        log.debug("Búsqueda por valor '{}' encontró {} estados.", valor, estados.size());
        return estados; // Devuelve entidades, el controlador mapea a DTO
    }
}