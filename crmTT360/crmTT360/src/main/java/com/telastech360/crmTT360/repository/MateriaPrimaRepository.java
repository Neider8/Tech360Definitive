package com.telastech360.crmTT360.repository;

import com.telastech360.crmTT360.entity.MateriaPrima;
import com.telastech360.crmTT360.entity.MateriaPrima.TipoMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MateriaPrimaRepository extends JpaRepository<MateriaPrima, Long> {

    @Query("SELECT m FROM MateriaPrima m ORDER BY m.nombre ASC")
    List<MateriaPrima> findAllByOrderByNombreAsc();

    List<MateriaPrima> findByTipoMaterial(TipoMaterial tipo);

    @Query("SELECT m FROM MateriaPrima m WHERE m.proveedorTela.id = :proveedorId")
    List<MateriaPrima> findByProveedorId(Long proveedorId);

    @Query("SELECT m FROM MateriaPrima m WHERE m.codigo = :codigo")
    MateriaPrima findByCodigo(String codigo);
}