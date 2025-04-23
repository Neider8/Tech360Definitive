package com.telastech360.crmTT360.repository;

import com.telastech360.crmTT360.entity.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstadoRepository extends JpaRepository<Estado, Long> {

    // Búsqueda por tipo y valor exacto (case-sensitive)
    @Query("SELECT e FROM Estado e WHERE e.tipoEstado = :tipo AND e.valor = :valor")
    Optional<Estado> findByTipoAndValor(Estado.TipoEstado tipo, String valor);

    // Búsqueda por tipo de estado
    List<Estado> findByTipoEstado(Estado.TipoEstado tipo);

    // Búsqueda por valor (contains)
    List<Estado> findByValorContaining(String valor);

    // Búsqueda de estados por tipo ordenados
    List<Estado> findByTipoEstadoOrderByValorAsc(Estado.TipoEstado tipo);

    // Verificación de existencia por tipo y valor
    boolean existsByTipoEstadoAndValor(Estado.TipoEstado tipo, String valor);

    // Obtener valores únicos de tipoEstado
    @Query("SELECT DISTINCT e.tipoEstado FROM Estado e")
    List<Estado.TipoEstado> findDistinctTipos();

    // Método para obtener estados comúnmente usados en pedidos
    @Query("SELECT e FROM Estado e WHERE e.tipoEstado = 'PEDIDO' ORDER BY e.valor ASC")
    List<Estado> findEstadosPedido();

    // Método para obtener estados comúnmente usados en items
    @Query("SELECT e FROM Estado e WHERE e.tipoEstado = 'ITEM' ORDER BY e.valor ASC")
    List<Estado> findEstadosItem();
}