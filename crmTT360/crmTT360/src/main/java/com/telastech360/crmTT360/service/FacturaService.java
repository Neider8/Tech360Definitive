package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.entity.Factura;
import com.telastech360.crmTT360.exception.ResourceNotFoundException;
import com.telastech360.crmTT360.repository.FacturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import com.telastech360.crmTT360.dto.FacturaDTO;
import com.telastech360.crmTT360.mapper.FacturaMapper;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;

    @Autowired
    public FacturaService(FacturaRepository facturaRepository) {
        this.facturaRepository = facturaRepository;
    }

    @Transactional
    public FacturaDTO crearFactura(FacturaDTO facturaDTO) {
        Factura factura = FacturaMapper.toEntity(facturaDTO);
        Factura nuevaFactura = facturaRepository.save(factura);
        return FacturaMapper.toDTO(nuevaFactura);
    }

    @Transactional(readOnly = true)
    public List<FacturaDTO> listarTodasLasFacturas() {
        List<Factura> facturas = facturaRepository.findAll();
        return facturas.stream()
                .map(FacturaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FacturaDTO obtenerFacturaPorId(Long id) {
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada con ID: " + id));
        return FacturaMapper.toDTO(factura);
    }

    @Transactional
    public FacturaDTO actualizarFactura(Long id, FacturaDTO facturaDTO) {
        Factura facturaExistente = facturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada con ID: " + id));

        FacturaMapper.updateEntityFromDTO(facturaDTO, facturaExistente);
        Factura facturaActualizada = facturaRepository.save(facturaExistente);
        return FacturaMapper.toDTO(facturaActualizada);
    }

    @Transactional
    public void eliminarFactura(Long id) {
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada con ID: " + id));
        facturaRepository.delete(factura);
    }

    // ========== MÉTODOS ADICIONALES ========== //

    @Transactional(readOnly = true)
    public List<Factura> buscarPorTipoMovimiento(Factura.TipoMovimiento tipoMovimiento) {
        return facturaRepository.findByTipoMovimiento(tipoMovimiento);
    }

    @Transactional(readOnly = true)
    public List<Factura> buscarPorRangoFechas(Timestamp inicio, Timestamp fin) {
        return facturaRepository.findByFechaBetween(inicio, fin);
    }

    @Transactional(readOnly = true)
    public List<Factura> buscarFacturasPendientesPago() {
        return facturaRepository.findFacturasPendientesDePago();
    }

    @Transactional(readOnly = true)
    public List<Object[]> calcularTotalFacturadoPorDia(Timestamp inicio, Timestamp fin) {
        return facturaRepository.getTotalFacturadoPorDia(inicio, fin);
    }

    @Transactional(readOnly = true)
    public List<Factura> buscarFacturasConTotalMayorA(BigDecimal montoMinimo) {
        return facturaRepository.findFacturasConTotalMayorA(montoMinimo);
    }
}