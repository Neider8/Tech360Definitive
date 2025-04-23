package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.entity.Estado;
import com.telastech360.crmTT360.entity.Estado.TipoEstado;
import com.telastech360.crmTT360.exception.DuplicateResourceException;
import com.telastech360.crmTT360.exception.ResourceNotFoundException;
import com.telastech360.crmTT360.exception.UsedStateException;
import com.telastech360.crmTT360.repository.EstadoRepository;
import com.telastech360.crmTT360.repository.BodegaRepository;
import com.telastech360.crmTT360.repository.ItemRepository;
import com.telastech360.crmTT360.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import com.telastech360.crmTT360.dto.EstadoDTO;
import com.telastech360.crmTT360.mapper.EstadoMapper;

@Service
public class EstadoService {

    private final EstadoRepository estadoRepository;
    private final BodegaRepository bodegaRepository;
    private final ItemRepository itemRepository;
    private final PedidoRepository pedidoRepository;

    @Autowired
    public EstadoService(EstadoRepository estadoRepository,
                         BodegaRepository bodegaRepository,
                         ItemRepository itemRepository,
                         PedidoRepository pedidoRepository) {
        this.estadoRepository = estadoRepository;
        this.bodegaRepository = bodegaRepository;
        this.itemRepository = itemRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional
    public EstadoDTO crearEstado(EstadoDTO estadoDTO) {
        // Verificar si ya existe un estado con el mismo tipo y valor
        if (estadoRepository.existsByTipoEstadoAndValor(estadoDTO.getTipoEstado(), estadoDTO.getValor())) {
            throw new DuplicateResourceException("Ya existe un estado con el mismo tipo y valor");
        }
        Estado estado = EstadoMapper.toEntity(estadoDTO);
        Estado nuevoEstado = estadoRepository.save(estado);
        return EstadoMapper.toDTO(nuevoEstado);
    }

    @Transactional(readOnly = true)
    public List<EstadoDTO> listarTodosLosEstados() {
        List<Estado> estados = estadoRepository.findAll();
        return estados.stream()
                .map(EstadoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EstadoDTO obtenerEstadoPorId(Long id) {
        Estado estado = estadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado con ID: " + id));
        return EstadoMapper.toDTO(estado);
    }

    @Transactional
    public EstadoDTO actualizarEstado(Long id, EstadoDTO estadoDTO) {
        Estado estadoExistente = estadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado con ID: " + id));

        // Verificar si ya existe un estado con el mismo tipo y valor (excluyendo el estado actual)
        if (!estadoExistente.getTipoEstado().equals(estadoDTO.getTipoEstado()) || !estadoExistente.getValor().equals(estadoDTO.getValor())) {
            if (estadoRepository.existsByTipoEstadoAndValor(estadoDTO.getTipoEstado(), estadoDTO.getValor())) {
                throw new DuplicateResourceException("Ya existe un estado con el mismo tipo y valor");
            }
        }

        EstadoMapper.updateEntityFromDTO(estadoDTO, estadoExistente);
        Estado estadoActualizado = estadoRepository.save(estadoExistente);
        return EstadoMapper.toDTO(estadoActualizado);
    }

    @Transactional
    public void eliminarEstado(Long id) {
        Estado estado = estadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado con ID: " + id));

        // Verificación de uso corregida:
        boolean enUso = bodegaRepository.existsByEstadoId(estado.getEstadoId()) ||
                itemRepository.existsByEstadoId(estado.getEstadoId()) ||
                pedidoRepository.existsByEstadoId(estado.getEstadoId());

        if (enUso) {
            throw new UsedStateException("No se puede eliminar el estado porque está en uso");
        }

        estadoRepository.delete(estado);
    }

    public List<Estado> listarEstadosPedido() {
        return estadoRepository.findEstadosPedido();
    }

    public List<Estado> listarEstadosItem() {
        return estadoRepository.findEstadosItem();
    }

    public List<TipoEstado> listarTiposEstadoDisponibles() {
        return estadoRepository.findDistinctTipos();
    }

    public List<Estado> buscarEstadosPorValor(String valor) {
        return estadoRepository.findByValorContaining(valor);
    }

    public boolean existeEstadoConTipoYValor(TipoEstado tipo, String valor) {
        return estadoRepository.existsByTipoEstadoAndValor(tipo, valor);
    }
}