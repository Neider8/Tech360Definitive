package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.entity.ClienteInterno;
import com.telastech360.crmTT360.entity.Usuario;
import com.telastech360.crmTT360.exception.DuplicateResourceException;
import com.telastech360.crmTT360.exception.ResourceNotFoundException;
import com.telastech360.crmTT360.exception.IllegalOperationException;
import com.telastech360.crmTT360.repository.ClienteInternoRepository;
import com.telastech360.crmTT360.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ClienteInternoService {

    private final ClienteInternoRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public ClienteInternoService(ClienteInternoRepository clienteRepository,
                                 UsuarioRepository usuarioRepository) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // ========== CRUD BÁSICO ========== //

    @Transactional
    public ClienteInterno crearCliente(ClienteInterno cliente) {
        // Validar código único
        if (clienteRepository.existsByCodigoInterno(cliente.getCodigoInterno())) {
            throw new DuplicateResourceException("El código ya está registrado: " + cliente.getCodigoInterno());
        }

        // Validar responsable existente si se especifica
        if (cliente.getResponsable() != null) {
            Usuario responsable = usuarioRepository.findById(cliente.getResponsable().getUsuarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario responsable no encontrado"));
            cliente.setResponsable(responsable);
        }

        return clienteRepository.save(cliente);
    }

    @Transactional(readOnly = true)
    public ClienteInterno obtenerClientePorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<ClienteInterno> listarTodosLosClientes() {
        return clienteRepository.findAllByOrderByNombreAsc();
    }

    @Transactional
    public ClienteInterno actualizarCliente(Long id, ClienteInterno clienteActualizado) {
        ClienteInterno clienteExistente = obtenerClientePorId(id);

        // Validar código único si cambia
        if (!clienteExistente.getCodigoInterno().equals(clienteActualizado.getCodigoInterno()) &&
                clienteRepository.existsByCodigoInterno(clienteActualizado.getCodigoInterno())) {
            throw new DuplicateResourceException("El código ya está registrado: " + clienteActualizado.getCodigoInterno());
        }

        // Actualizar campos básicos
        clienteExistente.setCodigoInterno(clienteActualizado.getCodigoInterno());
        clienteExistente.setNombre(clienteActualizado.getNombre());
        clienteExistente.setTipo(clienteActualizado.getTipo());
        clienteExistente.setUbicacion(clienteActualizado.getUbicacion());
        clienteExistente.setPresupuestoAnual(clienteActualizado.getPresupuestoAnual());

        // Actualizar responsable si se especifica
        if (clienteActualizado.getResponsable() != null) {
            Usuario responsable = usuarioRepository.findById(clienteActualizado.getResponsable().getUsuarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario responsable no encontrado"));
            clienteExistente.setResponsable(responsable);
        } else {
            clienteExistente.setResponsable(null);
        }

        return clienteRepository.save(clienteExistente);
    }

    @Transactional
    public void eliminarCliente(Long id) {
        ClienteInterno cliente = obtenerClientePorId(id);

        // Validar que no tenga pedidos asociados
        if (clienteRepository.countPedidosByClienteId(id) > 0) {
            throw new IllegalOperationException("No se puede eliminar el cliente porque tiene pedidos asociados");
        }

        clienteRepository.delete(cliente);
    }

    // ========== MÉTODOS ESPECIALIZADOS ========== //

    @Transactional(readOnly = true)
    public List<ClienteInterno> buscarPorNombre(String nombre) {
        return clienteRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Transactional(readOnly = true)
    public List<ClienteInterno> buscarPorTipo(ClienteInterno.TipoCliente tipo) {
        return clienteRepository.findByTipo(tipo);
    }

    @Transactional(readOnly = true)
    public List<ClienteInterno> buscarPorResponsable(Long responsableId) {
        return clienteRepository.findByResponsableId(responsableId);
    }

    @Transactional(readOnly = true)
    public List<ClienteInterno> buscarPorPresupuestoMayorQue(BigDecimal monto) {
        return clienteRepository.findByPresupuestoAnualMayorQue(monto.doubleValue());
    }

    @Transactional(readOnly = true)
    public boolean existeClienteConCodigo(String codigo) {
        return clienteRepository.existsByCodigoInterno(codigo);
    }

    @Transactional(readOnly = true)
    public List<Object[]> obtenerResumenPorTipo() {
        return clienteRepository.getResumenClientesPorTipo();
    }
}