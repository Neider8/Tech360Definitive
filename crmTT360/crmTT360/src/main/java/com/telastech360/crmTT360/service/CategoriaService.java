package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.entity.Categoria;
import com.telastech360.crmTT360.entity.Item;
import com.telastech360.crmTT360.exception.DuplicateResourceException;
import com.telastech360.crmTT360.exception.ResourceNotFoundException;
import com.telastech360.crmTT360.exception.IllegalOperationException;
import com.telastech360.crmTT360.repository.CategoriaRepository;
import com.telastech360.crmTT360.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public CategoriaService(CategoriaRepository categoriaRepository,
                            ItemRepository itemRepository) {
        this.categoriaRepository = categoriaRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    public Categoria crearCategoria(Categoria categoria) {
        if (categoriaRepository.existsByNombre(categoria.getNombre())) {
            throw new DuplicateResourceException("Ya existe una categoría con el nombre: " + categoria.getNombre());
        }
        return categoriaRepository.save(categoria);
    }

    public Categoria obtenerCategoriaPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
    }

    public List<Categoria> listarTodasLasCategorias() {
        return categoriaRepository.findAllByOrderByNombreAsc();
    }

    @Transactional
    public Categoria actualizarCategoria(Long id, Categoria categoriaActualizada) {
        Categoria categoriaExistente = obtenerCategoriaPorId(id);

        if (!categoriaExistente.getNombre().equals(categoriaActualizada.getNombre())) {
            if (categoriaRepository.existsByNombre(categoriaActualizada.getNombre())) {
                throw new DuplicateResourceException("Ya existe una categoría con el nombre: " + categoriaActualizada.getNombre());
            }
            categoriaExistente.setNombre(categoriaActualizada.getNombre());
        }

        categoriaExistente.setDescripcion(categoriaActualizada.getDescripcion());
        return categoriaRepository.save(categoriaExistente);
    }

    @Transactional
    public void eliminarCategoria(Long id) {
        Categoria categoria = obtenerCategoriaPorId(id);
        List<Item> items = itemRepository.findByCategoria(categoria);
        if (!items.isEmpty()) {
            throw new IllegalOperationException("No se puede eliminar la categoría porque tiene " + items.size() + " items asociados");
        }
        categoriaRepository.delete(categoria);
    }

    public List<Categoria> buscarCategoriasPorNombre(String nombre) {
        return categoriaRepository.findByNombreContaining(nombre);
    }

    public List<Categoria> buscarCategoriasPorDescripcion(String descripcion) {
        return categoriaRepository.buscarPorDescripcion(descripcion);
    }

    public List<Object[]> listarCategoriasConConteoItems() {
        return categoriaRepository.findCategoriasWithItemCount();
    }

    public List<Categoria> listarCategoriasPorTipoItem(Item.TipoItem tipoItem) {
        return categoriaRepository.findByTipoItem(tipoItem);
    }

    public boolean existeCategoriaConNombre(String nombre) {
        return categoriaRepository.existsByNombre(nombre);
    }
}