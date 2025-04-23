package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.entity.MateriaPrima;
import com.telastech360.crmTT360.entity.Proveedor;
import com.telastech360.crmTT360.entity.Item; // Importar la clase padre Item para TipoItem
import com.telastech360.crmTT360.entity.MateriaPrima.TipoMaterial; // Importar el enum TipoMaterial
import com.telastech360.crmTT360.exception.DuplicateResourceException;
import com.telastech360.crmTT360.exception.ResourceNotFoundException;
import com.telastech360.crmTT360.exception.IllegalOperationException;
import com.telastech360.crmTT360.repository.MateriaPrimaRepository;
import com.telastech360.crmTT360.repository.ItemRepository; // Usar ItemRepository para validaciones de código y pedidos
import com.telastech360.crmTT360.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.math.BigDecimal; // Importar BigDecimal si se usa en métodos
import java.sql.Date; // Importar Date si se usa en métodos

@Service
public class MateriaPrimaService {

    private final MateriaPrimaRepository materiaPrimaRepository;
    private final ItemRepository itemRepository; // Repositorio de la clase padre para validaciones generales de Item
    private final ProveedorRepository proveedorRepository; // Repositorio general de proveedores

    @Autowired
    public MateriaPrimaService(MateriaPrimaRepository materiaPrimaRepository,
                               ItemRepository itemRepository,
                               ProveedorRepository proveedorRepository) {
        this.materiaPrimaRepository = materiaPrimaRepository;
        this.itemRepository = itemRepository;
        this.proveedorRepository = proveedorRepository;
    }

    // ========== CRUD BÁSICO ========== //

    @Transactional
    public MateriaPrima crearMateriaPrima(MateriaPrima materiaPrima) {
        // Validar código único utilizando el ItemRepository
        if (itemRepository.existsByCodigo(materiaPrima.getCodigo())) {
            throw new DuplicateResourceException("Ya existe un ítem con el código: " + materiaPrima.getCodigo());
        }

        // Validar y cargar la entidad ProveedorTela si se proporciona
        if (materiaPrima.getProveedorTela() != null && materiaPrima.getProveedorTela().getProveedorId() != null) {
            Proveedor proveedor = proveedorRepository.findById(materiaPrima.getProveedorTela().getProveedorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Proveedor de tela no encontrado con ID: " + materiaPrima.getProveedorTela().getProveedorId()));
            materiaPrima.setProveedorTela(proveedor);
        } else {
            materiaPrima.setProveedorTela(null); // Asegurar que sea null si no se proporciona ID
        }

        // El tipo de item (MATERIA_PRIMA) ya se establece en el constructor de MateriaPrima

        // Guardar la materia prima
        return materiaPrimaRepository.save(materiaPrima);
    }

    @Transactional(readOnly = true)
    public MateriaPrima obtenerMateriaPrimaPorId(Long id) {
        // Buscar en el repositorio de MateriaPrima
        return materiaPrimaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Materia prima no encontrada con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<MateriaPrima> listarTodasLasMateriasPrimas() {
        // Listar todas las materias primas, ordenadas por nombre (requiere método en MateriaPrimaRepository)
        return materiaPrimaRepository.findAllByOrderByNombreAsc();
    }

    @Transactional
    public MateriaPrima actualizarMateriaPrima(Long id, MateriaPrima materiaPrimaActualizada) {
        // Obtener la materia prima existente
        MateriaPrima materiaPrimaExistente = obtenerMateriaPrimaPorId(id);

        // Validar código único si cambia (utilizando ItemRepository)
        if (!materiaPrimaExistente.getCodigo().equals(materiaPrimaActualizada.getCodigo()) &&
                itemRepository.existsByCodigo(materiaPrimaActualizada.getCodigo())) {
            throw new DuplicateResourceException("El código ya está registrado: " + materiaPrimaActualizada.getCodigo());
        }

        // Actualizar campos heredados de Item (usando los métodos correctos del padre)
        materiaPrimaExistente.setCodigo(materiaPrimaActualizada.getCodigo());
        materiaPrimaExistente.setNombre(materiaPrimaActualizada.getNombre());
        materiaPrimaExistente.setDescripcion(materiaPrimaActualizada.getDescripcion());
        materiaPrimaExistente.setUnidadMedida(materiaPrimaActualizada.getUnidadMedida());
        materiaPrimaExistente.setPrecio(materiaPrimaActualizada.getPrecio()); // Corregido: usar getPrecio() y setPrecio()
        materiaPrimaExistente.setStockDisponible(materiaPrimaActualizada.getStockDisponible()); // Corregido: usar getStockDisponible() y setStockDisponible()
        materiaPrimaExistente.setStockMinimo(materiaPrimaActualizada.getStockMinimo());
        materiaPrimaExistente.setStockMaximo(materiaPrimaActualizada.getStockMaximo()); // stockMaximo es válido en Item/MateriaPrima
        materiaPrimaExistente.setFechaVencimiento(materiaPrimaActualizada.getFechaVencimiento());

        // Actualizar campos específicos de MateriaPrima
        materiaPrimaExistente.setTipoMaterial(materiaPrimaActualizada.getTipoMaterial());
        materiaPrimaExistente.setAnchoRollo(materiaPrimaActualizada.getAnchoRollo());
        materiaPrimaExistente.setPesoMetro(materiaPrimaActualizada.getPesoMetro());

        // Validar y cargar la entidad ProveedorTela si se proporciona en la actualización
        if (materiaPrimaActualizada.getProveedorTela() != null && materiaPrimaActualizada.getProveedorTela().getProveedorId() != null) {
            Proveedor proveedor = proveedorRepository.findById(materiaPrimaActualizada.getProveedorTela().getProveedorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Proveedor de tela no encontrado con ID: " + materiaPrimaActualizada.getProveedorTela().getProveedorId()));
            materiaPrimaExistente.setProveedorTela(proveedor);
        } else {
            materiaPrimaExistente.setProveedorTela(null);
        }


        // El tipo de item (MATERIA_PRIMA) no se actualiza

        // Guardar la materia prima actualizada
        return materiaPrimaRepository.save(materiaPrimaExistente);
    }

    @Transactional
    public void eliminarMateriaPrima(Long id) {
        // Obtener la materia prima por ID
        MateriaPrima materiaPrima = obtenerMateriaPrimaPorId(id);

        // Validar que no esté en pedidos activos (utilizando ItemRepository)
        if (itemRepository.existeEnPedidosActivos(id)) { // Método existeEnPedidosActivos debe estar en ItemRepository
            throw new IllegalOperationException("No se puede eliminar la materia prima porque está en pedidos activos");
        }

        // Eliminar la entidad
        materiaPrimaRepository.delete(materiaPrima);
    }

    // ========== MÉTODOS ESPECIALIZADOS (Requieren implementación en MateriaPrimaRepository o ItemRepository) ========== //

    // Implementar en MateriaPrimaRepository: List<MateriaPrima> findByTipoMaterial(TipoMaterial tipoMaterial);
    @Transactional(readOnly = true)
    public List<MateriaPrima> buscarPorTipoMaterial(TipoMaterial tipoMaterial) {
        // return materiaPrimaRepository.findByTipoMaterial(tipoMaterial);
        return null; // Retorno temporal, implementar método en repositorio
    }

    // Implementar en ItemRepository o MateriaPrimaRepository: List<MateriaPrima> findByProveedor(Proveedor proveedor); o findByProveedorId(Long proveedorId);
    @Transactional(readOnly = true)
    public List<MateriaPrima> buscarPorProveedor(Long proveedorId) {
        // return materiaPrimaRepository.findByProveedorId(proveedorId);
        // O si necesitas buscar por el proveedor general heredado de Item:
        // Proveedor proveedor = proveedorRepository.findById(proveedorId)
        //         .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + proveedorId));
        // return itemRepository.findByProveedor(proveedor); // Requiere método findByProveedor en ItemRepository
        return null; // Retorno temporal, implementar método en repositorio
    }

    // Implementar en ItemRepository o MateriaPrimaRepository: List<MateriaPrima> findByFechaVencimientoBefore(Date fecha);
    @Transactional(readOnly = true)
    public List<MateriaPrima> buscarMateriasPrimasPorVencer(java.sql.Date fechaLimite) {
        // return materiaPrimaRepository.findByFechaVencimientoBefore(fechaLimite);
        // O si necesitas buscar en todos los Items con fecha de vencimiento:
        // return itemRepository.findByFechaVencimientoBeforeAndTipoItem(fechaLimite, Item.TipoItem.MATERIA_PRIMA); // Requiere método en ItemRepository
        return null; // Retorno temporal, implementar método en repositorio
    }

    // Implementar en MateriaPrimaRepository: @Query("SELECT mp.tipoMaterial, SUM(mp.stockDisponible) FROM MateriaPrima mp GROUP BY mp.tipoMaterial") List<Object[]> getResumenStockPorTipoMaterial();
    @Transactional(readOnly = true)
    public List<Object[]> obtenerResumenStockPorTipoMaterial() {
        // return materiaPrimaRepository.getResumenStockPorTipoMaterial();
        return null; // Retorno temporal, implementar método en repositorio
    }

    // Puedes agregar métodos para buscar por anchoRollo, pesoMetro si es necesario.
    // Por ejemplo: @Transactional(readOnly = true) List<MateriaPrima> findByAnchoRolloGreaterThan(BigDecimal anchoMinimo);
}