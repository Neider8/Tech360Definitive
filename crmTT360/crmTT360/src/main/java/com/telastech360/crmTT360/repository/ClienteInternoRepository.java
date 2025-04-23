package com.telastech360.crmTT360.repository;

import com.telastech360.crmTT360.entity.ClienteInterno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteInternoRepository extends JpaRepository<ClienteInterno, Long> {

    // Métodos básicos de consulta
    boolean existsByCodigoInterno(String codigoInterno);

    List<ClienteInterno> findAllByOrderByNombreAsc();

    List<ClienteInterno> findByNombreContainingIgnoreCase(String nombre);

    List<ClienteInterno> findByTipo(ClienteInterno.TipoCliente tipo);

    // Consultas personalizadas
    @Query("SELECT c FROM ClienteInterno c WHERE c.responsable.id = :responsableId")
    List<ClienteInterno> findByResponsableId(Long responsableId);

    @Query("SELECT c FROM ClienteInterno c WHERE c.presupuestoAnual >= :monto")
    List<ClienteInterno> findByPresupuestoAnualMayorQue(Double monto);

    @Query("SELECT c.tipo, COUNT(c), AVG(c.presupuestoAnual) FROM ClienteInterno c GROUP BY c.tipo")
    List<Object[]> getResumenClientesPorTipo();

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.cliente.clienteId = :clienteId")
    long countPedidosByClienteId(Long clienteId);

    @Query("SELECT c FROM ClienteInterno c WHERE " +
            "(:codigo IS NULL OR c.codigoInterno LIKE %:codigo%) AND " +
            "(:nombre IS NULL OR c.nombre LIKE %:nombre%) AND " +
            "(:tipo IS NULL OR c.tipo = :tipo) AND " +
            "(:responsableId IS NULL OR c.responsable.id = :responsableId)")
    List<ClienteInterno> busquedaAvanzada(String codigo, String nombre,
                                          ClienteInterno.TipoCliente tipo,
                                          Long responsableId);
}