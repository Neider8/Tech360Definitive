package com.telastech360.crmTT360.repository;

import com.telastech360.crmTT360.entity.Categoria;
import com.telastech360.crmTT360.entity.Item.TipoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    // Búsqueda exacta por nombre (case-sensitive)
    Optional<Categoria> findByNombre(String nombre);

    // Búsqueda por nombre ignorando mayúsculas/minúsculas
    Optional<Categoria> findByNombreIgnoreCase(String nombre);

    // Búsqueda por fragmento de nombre
    List<Categoria> findByNombreContaining(String fragmento);

    // Búsqueda por descripción (JPQL personalizado)
    @Query("SELECT c FROM Categoria c WHERE c.descripcion LIKE %:texto%")
    List<Categoria> buscarPorDescripcion(String texto);

    // Verificación de existencia por nombre
    boolean existsByNombre(String nombre);

    // Obtener categorías con conteo de productos asociados
    @Query("SELECT c, COUNT(i) as totalItems FROM Categoria c LEFT JOIN c.items i GROUP BY c")
    List<Object[]> findCategoriasWithItemCount();

    // Obtener categorías ordenadas por nombre
    List<Categoria> findAllByOrderByNombreAsc();

    // Búsqueda de categorías con productos de un tipo específico
    @Query("SELECT DISTINCT c FROM Categoria c JOIN c.items i WHERE i.tipoItem = :tipoItem")
    List<Categoria> findByTipoItem(TipoItem tipoItem);
}