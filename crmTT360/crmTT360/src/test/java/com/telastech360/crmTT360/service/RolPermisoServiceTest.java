package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.entity.Permiso;
import com.telastech360.crmTT360.entity.Rol;
import com.telastech360.crmTT360.entity.RolPermisoId;
import com.telastech360.crmTT360.exception.DuplicateResourceException;
import com.telastech360.crmTT360.exception.ResourceNotFoundException;
import com.telastech360.crmTT360.repository.PermisoRepository;
import com.telastech360.crmTT360.repository.RolPermisoRepository;
import com.telastech360.crmTT360.repository.RolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para RolPermisoService.
 */
@ExtendWith(MockitoExtension.class)
class RolPermisoServiceTest {

    @Mock private RolPermisoRepository rolPermisoRepository;
    @Mock private RolRepository rolRepository;
    @Mock private PermisoRepository permisoRepository;

    @InjectMocks
    private RolPermisoService rolPermisoService;

    private Rol rolAdmin;
    private Permiso permisoLeer;
    private Permiso permisoCrear;
    private RolPermisoId rolPermisoId;

    @BeforeEach
    void setUp() {
        rolAdmin = new Rol();
        rolAdmin.setRolId(1L);
        rolAdmin.setNombre("ADMIN");

        permisoLeer = new Permiso();
        permisoLeer.setPermisoId(10L);
        permisoLeer.setNombre("LEER_USUARIOS");

        permisoCrear = new Permiso();
        permisoCrear.setPermisoId(11L);
        permisoCrear.setNombre("CREAR_USUARIOS");

        rolPermisoId = new RolPermisoId(rolAdmin.getRolId(), permisoLeer.getPermisoId());
    }

    @Test
    @DisplayName("Asignar Permiso - Éxito")
    void asignarPermiso_Exito() {
        // Arrange
        when(rolRepository.findById(rolAdmin.getRolId())).thenReturn(Optional.of(rolAdmin));
        when(permisoRepository.findById(permisoCrear.getPermisoId())).thenReturn(Optional.of(permisoCrear));
        // Simula que la relación NO existe aún
        when(rolPermisoRepository.existsById_RolIdAndId_PermisoId(rolAdmin.getRolId(), permisoCrear.getPermisoId())).thenReturn(false);
        // Simula el guardado del rol (que ahora contendría el permiso)
        when(rolRepository.save(any(Rol.class))).thenReturn(rolAdmin);

        // Act
        assertDoesNotThrow(() -> {
            rolPermisoService.asignarPermiso(rolAdmin.getRolId(), permisoCrear.getPermisoId());
        });

        // Assert
        assertTrue(rolAdmin.getPermisos().contains(permisoCrear)); // Verifica que se añadió a la colección
        verify(rolRepository).findById(rolAdmin.getRolId());
        verify(permisoRepository).findById(permisoCrear.getPermisoId());
        verify(rolPermisoRepository).existsById_RolIdAndId_PermisoId(rolAdmin.getRolId(), permisoCrear.getPermisoId());
        verify(rolRepository).save(rolAdmin); // Verifica que se guardó el rol
    }

    @Test
    @DisplayName("Asignar Permiso - Rol No Encontrado")
    void asignarPermiso_RolNoEncontrado() {
        // Arrange
        Long idRolInexistente = 99L;
        when(rolRepository.findById(idRolInexistente)).thenReturn(Optional.empty()); // Rol no existe

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            rolPermisoService.asignarPermiso(idRolInexistente, permisoLeer.getPermisoId());
        });

        assertEquals("Rol no encontrado con ID: " + idRolInexistente, exception.getMessage());
        verify(rolRepository).findById(idRolInexistente);
        verifyNoInteractions(permisoRepository, rolPermisoRepository);
        verify(rolRepository, never()).save(any(Rol.class));
    }

    @Test
    @DisplayName("Asignar Permiso - Permiso No Encontrado")
    void asignarPermiso_PermisoNoEncontrado() {
        // Arrange
        Long idPermisoInexistente = 999L;
        when(rolRepository.findById(rolAdmin.getRolId())).thenReturn(Optional.of(rolAdmin));
        when(permisoRepository.findById(idPermisoInexistente)).thenReturn(Optional.empty()); // Permiso no existe

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            rolPermisoService.asignarPermiso(rolAdmin.getRolId(), idPermisoInexistente);
        });

        assertEquals("Permiso no encontrado con ID: " + idPermisoInexistente, exception.getMessage());
        verify(rolRepository).findById(rolAdmin.getRolId());
        verify(permisoRepository).findById(idPermisoInexistente);
        verifyNoInteractions(rolPermisoRepository);
        verify(rolRepository, never()).save(any(Rol.class));
    }

    @Test
    @DisplayName("Asignar Permiso - Ya Asignado")
    void asignarPermiso_YaAsignado() {
        // Arrange
        rolAdmin.addPermiso(permisoLeer); // Simular que ya está asignado
        when(rolRepository.findById(rolAdmin.getRolId())).thenReturn(Optional.of(rolAdmin));
        when(permisoRepository.findById(permisoLeer.getPermisoId())).thenReturn(Optional.of(permisoLeer));
        // Simula que la relación SÍ existe
        when(rolPermisoRepository.existsById_RolIdAndId_PermisoId(rolAdmin.getRolId(), permisoLeer.getPermisoId())).thenReturn(true);

        // Act & Assert
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
            rolPermisoService.asignarPermiso(rolAdmin.getRolId(), permisoLeer.getPermisoId());
        });

        assertTrue(exception.getMessage().contains("ya está asignado al rol"));

        verify(rolRepository).findById(rolAdmin.getRolId());
        verify(permisoRepository).findById(permisoLeer.getPermisoId());
        verify(rolPermisoRepository).existsById_RolIdAndId_PermisoId(rolAdmin.getRolId(), permisoLeer.getPermisoId());
        verify(rolRepository, never()).save(any(Rol.class)); // No se debe guardar si ya existe
    }
}