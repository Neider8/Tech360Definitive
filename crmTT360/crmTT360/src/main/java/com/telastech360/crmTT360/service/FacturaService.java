package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.dto.FacturaDTO;
import com.telastech360.crmTT360.entity.Factura;
import com.telastech360.crmTT360.entity.Pedido; // Necesario si se valida/asigna Pedido
import com.telastech360.crmTT360.exception.ResourceNotFoundException;
import com.telastech360.crmTT360.mapper.FacturaMapper;
import com.telastech360.crmTT360.repository.FacturaRepository;
import com.telastech360.crmTT360.repository.PedidoRepository; // Necesario si se valida/asigna Pedido
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar la lógica de negocio relacionada con las Facturas.
 * Convierte entre DTOs y Entidades usando {@link FacturaMapper}.
 */
@Service
public class FacturaService {

    private static final Logger log = LoggerFactory.getLogger(FacturaService.class);

    private final FacturaRepository facturaRepository;
    private final FacturaMapper facturaMapper; // Inyectar Mapper
    private final PedidoRepository pedidoRepository; // Inyectar si se necesita validar/asignar Pedido

    /**
     * Constructor para inyección de dependencias.
     * @param facturaRepository Repositorio para Facturas.
     * @param facturaMapper Mapper para convertir entre Factura y FacturaDTO.
     * @param pedidoRepository Repositorio para Pedidos (opcional, para validación/asignación).
     */
    @Autowired
    public FacturaService(FacturaRepository facturaRepository,
                          FacturaMapper facturaMapper,
                          PedidoRepository pedidoRepository) { // Inyectar PedidoRepository
        this.facturaRepository = facturaRepository;
        this.facturaMapper = facturaMapper; // Asignar Mapper
        this.pedidoRepository = pedidoRepository; // Asignar PedidoRepository
    }

    /**
     * Crea una nueva factura a partir de un DTO.
     * Se necesita refactorizar para asignar el Pedido correctamente.
     * @param facturaDTO DTO con los datos de la factura a crear. Necesita incluir pedidoId.
     * @return El DTO de la factura creada.
     * @throws ResourceNotFoundException Si el Pedido asociado no existe.
     */
    @Transactional
    public FacturaDTO crearFactura(FacturaDTO facturaDTO) {
        log.info("Intentando crear nueva factura...");
        // ** IMPORTANTE: El DTO necesita incluir pedidoId para poder asociarlo **
        // Long pedidoId = facturaDTO.getPedidoId(); // Asumiendo que existe este getter en FacturaDTO
        Long pedidoId = 1L; // Placeholder - ¡¡REEMPLAZAR CON ID REAL DEL DTO!!
        if (pedidoId == null) {
            log.error("Fallo al crear factura: pedidoId es nulo en el DTO.");
            throw new IllegalArgumentException("El ID del pedido es obligatorio para crear una factura.");
        }

        log.debug("Buscando pedido con ID: {}", pedidoId);
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> {
                    log.error("Pedido con ID {} no encontrado al crear factura.", pedidoId);
                    return new ResourceNotFoundException("Pedido no encontrado con ID: " + pedidoId);
                });
        log.debug("Pedido ID {} encontrado.", pedidoId);

        Factura factura = facturaMapper.toEntity(facturaDTO);
        factura.setPedido(pedido); // Asignar la entidad Pedido encontrada
        // Establecer estado de pago inicial si no viene en el DTO
        if (!factura.isEstadoPago()) { // Asumiendo getter isEstadoPago
            factura.setEstadoPago(false);
            log.debug("Estableciendo estadoPago a false por defecto.");
        }


        Factura nuevaFactura = facturaRepository.save(factura);
        log.info("Factura creada exitosamente con ID: {} para Pedido ID: {}", nuevaFactura.getFacturaId(), pedidoId);
        return facturaMapper.toDTO(nuevaFactura);
    }

    /**
     * Lista todas las facturas registradas.
     * @return Lista de FacturaDTO.
     */
    @Transactional(readOnly = true)
    public List<FacturaDTO> listarTodasLasFacturas() {
        log.info("Listando todas las facturas...");
        List<Factura> facturas = facturaRepository.findAll();
        log.debug("Se encontraron {} facturas.", facturas.size());
        return facturas.stream()
                .map(facturaMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una factura por su ID.
     * @param id ID de la factura a buscar.
     * @return El FacturaDTO correspondiente.
     * @throws ResourceNotFoundException si la factura no existe.
     */
    @Transactional(readOnly = true)
    public FacturaDTO obtenerFacturaPorId(Long id) {
        log.info("Buscando factura por ID: {}", id);
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Factura con ID {} no encontrada.", id);
                    return new ResourceNotFoundException("Factura no encontrada con ID: " + id);
                });
        log.debug("Factura encontrada: ID {}", id);
        return facturaMapper.toDTO(factura);
    }

    /**
     * Actualiza una factura existente.
     * Permite modificar tipo, total, fecha y estado de pago. No permite cambiar el pedido asociado.
     * @param id ID de la factura a actualizar.
     * @param facturaDTO DTO con los datos actualizados.
     * @return El FacturaDTO actualizado.
     * @throws ResourceNotFoundException si la factura no existe.
     */
    @Transactional
    public FacturaDTO actualizarFactura(Long id, FacturaDTO facturaDTO) {
        log.info("Intentando actualizar factura con ID: {}", id);
        Factura facturaExistente = facturaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Factura ID {} no encontrada para actualizar.", id);
                    return new ResourceNotFoundException("Factura no encontrada con ID: " + id);
                });
        log.debug("Factura ID {} encontrada para actualizar.", id);

        // Usar mapper para actualizar campos permitidos (sin cambiar pedido)
        facturaMapper.updateEntityFromDTO(facturaDTO, facturaExistente);
        log.debug("Campos de factura ID {} actualizados desde DTO.", id);

        Factura facturaActualizada = facturaRepository.save(facturaExistente);
        log.info("Factura ID {} actualizada exitosamente.", id);
        return facturaMapper.toDTO(facturaActualizada);
    }

    /**
     * Elimina una factura por su ID.
     * @param id ID de la factura a eliminar.
     * @throws ResourceNotFoundException si la factura no existe.
     */
    @Transactional
    public void eliminarFactura(Long id) {
        log.info("Intentando eliminar factura con ID: {}", id);
        // Validar existencia antes de eliminar
        if (!facturaRepository.existsById(id)) {
            log.warn("Intento de eliminar factura inexistente con ID: {}", id);
            throw new ResourceNotFoundException("Factura no encontrada con ID: " + id);
        }
        log.debug("Factura ID {} encontrada. Procediendo a eliminar.", id);
        facturaRepository.deleteById(id);
        log.info("Factura ID {} eliminada exitosamente.", id);
    }

    // ========== MÉTODOS ADICIONALES ========== //

    /**
     * Busca facturas por tipo de movimiento (VENTA o COMPRA).
     * @param tipoMovimiento El tipo a filtrar.
     * @return Lista de entidades Factura.
     */
    @Transactional(readOnly = true)
    public List<Factura> buscarPorTipoMovimiento(Factura.TipoMovimiento tipoMovimiento) {
        log.info("Buscando facturas por tipo de movimiento: {}", tipoMovimiento);
        List<Factura> facturas = facturaRepository.findByTipoMovimiento(tipoMovimiento);
        log.debug("Se encontraron {} facturas del tipo {}", facturas.size(), tipoMovimiento);
        return facturas; // Devuelve entidades, el controlador las mapeará a DTO
    }

    /**
     * Busca facturas dentro de un rango de fechas de creación.
     * @param inicio Timestamp de inicio del rango.
     * @param fin Timestamp de fin del rango.
     * @return Lista de entidades Factura.
     */
    @Transactional(readOnly = true)
    public List<Factura> buscarPorRangoFechas(Timestamp inicio, Timestamp fin) {
        log.info("Buscando facturas entre {} y {}", inicio, fin);
        List<Factura> facturas = facturaRepository.findByFechaBetween(inicio, fin); // Asume método en repo
        log.debug("Se encontraron {} facturas en el rango de fechas.", facturas.size());
        return facturas; // Devuelve entidades
    }

    /**
     * Busca facturas que están marcadas como pendientes de pago (estadoPago = false).
     * @return Lista de entidades Factura pendientes.
     */
    @Transactional(readOnly = true)
    public List<Factura> buscarFacturasPendientesPago() {
        log.info("Buscando facturas pendientes de pago...");
        List<Factura> facturas = facturaRepository.findFacturasPendientesDePago(); // Asume método en repo
        log.debug("Se encontraron {} facturas pendientes de pago.", facturas.size());
        return facturas; // Devuelve entidades
    }

    /**
     * Calcula el total facturado agrupado por día dentro de un rango de fechas.
     * @param inicio Timestamp de inicio del rango.
     * @param fin Timestamp de fin del rango.
     * @return Lista de Object[], donde cada array contiene [Date (o String), BigDecimal (total)].
     */
    @Transactional(readOnly = true)
    public List<Object[]> calcularTotalFacturadoPorDia(Timestamp inicio, Timestamp fin) {
        log.info("Calculando total facturado por día entre {} y {}", inicio, fin);
        List<Object[]> resultado = facturaRepository.getTotalFacturadoPorDia(inicio, fin); // Asume método en repo
        log.debug("Cálculo de total por día completado, {} resultados.", resultado.size());
        return resultado; // Devuelve Object[], el controlador lo expone
    }

    /**
     * Busca facturas cuyo monto total sea mayor a un valor mínimo especificado.
     * @param montoMinimo El monto total mínimo.
     * @return Lista de entidades Factura que cumplen el criterio.
     */
    @Transactional(readOnly = true)
    public List<Factura> buscarFacturasConTotalMayorA(BigDecimal montoMinimo) {
        log.info("Buscando facturas con total mayor a {}", montoMinimo);
        List<Factura> facturas = facturaRepository.findFacturasConTotalMayorA(montoMinimo); // Asume método en repo
        log.debug("Se encontraron {} facturas con total mayor a {}", facturas.size(), montoMinimo);
        return facturas; // Devuelve entidades
    }
}