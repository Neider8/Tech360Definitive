package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.entity.Proveedor;
import com.telastech360.crmTT360.exception.DuplicateResourceException;
import com.telastech360.crmTT360.exception.ResourceNotFoundException;
import com.telastech360.crmTT360.repository.ProveedorRepository;
import com.telastech360.crmTT360.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public ProveedorService(ProveedorRepository proveedorRepository, ItemRepository itemRepository) {
        this.proveedorRepository = proveedorRepository;
        this.itemRepository = itemRepository;
    }

    // ========== MÉTODO NUEVO AGREGADO ========== //
    @Transactional(readOnly = true)
    public List<Proveedor> buscarProveedoresPorNombreYUbicacion(String nombre, String ubicacion) {
        return proveedorRepository.findByNombreAndDireccionContainingIgnoreCase(nombre, ubicacion);
    }

    // Resto de métodos existentes sin cambios...
    @Transactional
    public Proveedor crearProveedor(Proveedor proveedor) {
        if (proveedorRepository.existsByEmail(proveedor.getEmail())) {
            throw new DuplicateResourceException("El email ya está registrado: " + proveedor.getEmail());
        }

        if (proveedor.getTelefono() != null && !proveedor.getTelefono().isEmpty()
                && proveedorRepository.findByTelefono(proveedor.getTelefono()).isPresent()) {
            throw new DuplicateResourceException("El teléfono ya está registrado: " + proveedor.getTelefono());
        }

        return proveedorRepository.save(proveedor);
    }

    @Transactional(readOnly = true)
    public Proveedor obtenerProveedorPorId(Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Proveedor> listarTodosLosProveedores() {
        return proveedorRepository.findAllByOrderByNombreAsc();
    }

    @Transactional
    public Proveedor actualizarProveedor(Long id, Proveedor proveedorActualizado) {
        Proveedor proveedorExistente = obtenerProveedorPorId(id);

        if (!proveedorExistente.getEmail().equals(proveedorActualizado.getEmail()) &&
                proveedorRepository.existsByEmail(proveedorActualizado.getEmail())) {
            throw new DuplicateResourceException("El email ya está registrado: " + proveedorActualizado.getEmail());
        }

        if (proveedorActualizado.getTelefono() != null &&
                !proveedorActualizado.getTelefono().equals(proveedorExistente.getTelefono()) &&
                proveedorRepository.findByTelefono(proveedorActualizado.getTelefono()).isPresent()) {
            throw new DuplicateResourceException("El teléfono ya está registrado: " + proveedorActualizado.getTelefono());
        }

        proveedorExistente.setNombre(proveedorActualizado.getNombre());
        proveedorExistente.setEmail(proveedorActualizado.getEmail());
        proveedorExistente.setTelefono(proveedorActualizado.getTelefono());
        proveedorExistente.setDireccion(proveedorActualizado.getDireccion());

        return proveedorRepository.save(proveedorExistente);
    }

    @Transactional
    public void eliminarProveedor(Long id) {
        Proveedor proveedor = obtenerProveedorPorId(id);

        if (itemRepository.countByProveedor(proveedor) > 0) {
            throw new IllegalStateException("No se puede eliminar el proveedor porque tiene items asociados");
        }

        proveedorRepository.delete(proveedor);
    }

    @Transactional(readOnly = true)
    public List<Proveedor> buscarProveedores() {
        return listarTodosLosProveedores();
    }

    @Transactional(readOnly = true)
    public List<Proveedor> buscarProveedoresPorNombre(String nombre) {
        return proveedorRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Transactional(readOnly = true)
    public List<Proveedor> buscarProveedoresPorUbicacion(String ubicacion) {
        return proveedorRepository.buscarPorUbicacion(ubicacion);
    }

    @Transactional(readOnly = true)
    public List<Object[]> listarProveedoresConProductos() {
        return proveedorRepository.findProveedoresWithProductCount();
    }

    @Transactional(readOnly = true)
    public Optional<Proveedor> buscarPorEmail(String email) {
        return proveedorRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<Proveedor> buscarPorTelefono(String telefono) {
        return proveedorRepository.findByTelefono(telefono);
    }
}