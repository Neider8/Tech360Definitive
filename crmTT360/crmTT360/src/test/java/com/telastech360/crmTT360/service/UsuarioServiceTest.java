package com.telastech360.crmTT360.service;

import com.telastech360.crmTT360.dto.UsuarioRequestDTO;
import com.telastech360.crmTT360.entity.Rol;
import com.telastech360.crmTT360.entity.Usuario;
import com.telastech360.crmTT360.exception.*;
import com.telastech360.crmTT360.repository.RolRepository;
import com.telastech360.crmTT360.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private RolRepository rolRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private UsuarioService usuarioService;

    private UsuarioRequestDTO usuarioRequestDto;
    private Rol rolAdmin;
    private Rol rolOperario;
    private Usuario usuarioAdmin;
    private Usuario usuarioOperario;

    @BeforeEach
    void setUp() {
        rolAdmin = new Rol(); rolAdmin.setRolId(1L); rolAdmin.setNombre("ADMIN");
        rolOperario = new Rol(); rolOperario.setRolId(3L); rolOperario.setNombre("OPERARIO");

        usuarioRequestDto = new UsuarioRequestDTO(
                "Test Operario",
                "operario@example.com",
                "password123",
                rolOperario.getRolId()
        );

        usuarioAdmin = new Usuario("Admin User", "admin@example.com", "encodedPassAdmin", rolAdmin);
        usuarioAdmin.setUsuarioId(1L);
        usuarioAdmin.setEstado("ACTIVO");

        usuarioOperario = new Usuario("Operario User", "operario@example.com", "encodedPassOperario", rolOperario);
        usuarioOperario.setUsuarioId(2L);
        usuarioOperario.setEstado("ACTIVO");
    }

    @Test
    @DisplayName("Registrar Nuevo Usuario - Éxito")
    void registrarNuevoUsuario_Exito() {
        // Arrange
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(rolRepository.findById(usuarioRequestDto.getRolId())).thenReturn(Optional.of(rolOperario));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        // CORREGIDO: Devolver el usuario pasado como argumento, simulando asignación de ID
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuarioAGuardar = invocation.getArgument(0);
            usuarioAGuardar.setUsuarioId(2L);
            return usuarioAGuardar;
        });

        // Act
        Usuario usuarioCreado = usuarioService.registrarNuevoUsuario(usuarioRequestDto);

        // Assert
        assertNotNull(usuarioCreado);
        assertEquals(usuarioRequestDto.getEmail(), usuarioCreado.getEmail());
        assertEquals(usuarioRequestDto.getNombre(), usuarioCreado.getNombre()); // Debe pasar ahora
        assertEquals("encodedPassword", usuarioCreado.getPasswordHash());
        assertEquals("ACTIVO", usuarioCreado.getEstado());
        assertEquals(rolOperario, usuarioCreado.getRol());

        verify(usuarioRepository).existsByEmail(usuarioRequestDto.getEmail());
        verify(rolRepository).findById(usuarioRequestDto.getRolId());
        verify(passwordEncoder).encode(usuarioRequestDto.getPassword());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Actualizar Usuario - Cambiar Rol del Último Admin (Falla)")
    void actualizarUsuario_CambiarRolUltimoAdmin_DebeLanzarExcepcion() {
        // Arrange
        Long adminId = usuarioAdmin.getUsuarioId();
        UsuarioRequestDTO dtoCambioRol = new UsuarioRequestDTO(usuarioAdmin.getNombre(), usuarioAdmin.getEmail(), null, rolOperario.getRolId());

        when(usuarioRepository.findById(adminId)).thenReturn(Optional.of(usuarioAdmin));
        when(usuarioRepository.countByRol_Nombre("ADMIN")).thenReturn(1L);
        // Eliminado el stubbing innecesario para rolRepository.findById

        // Act & Assert
        IllegalOperationException exception = assertThrows(IllegalOperationException.class, () -> {
            usuarioService.actualizarUsuario(adminId, dtoCambioRol);
        });

        assertEquals("No se puede cambiar el rol del último administrador.", exception.getMessage());

        // Verificar interacciones
        verify(usuarioRepository).findById(adminId);
        verify(usuarioRepository).countByRol_Nombre("ADMIN");
        verify(rolRepository, never()).findById(anyLong()); // Verificación correcta: NUNCA se llama
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    // ... (Mantener el resto de los tests de UsuarioServiceTest como estaban antes) ...
    @Test
    @DisplayName("Registrar Nuevo Usuario - Email Duplicado")
    void registrarNuevoUsuario_EmailDuplicado() {
        when(usuarioRepository.existsByEmail(usuarioRequestDto.getEmail())).thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
            usuarioService.registrarNuevoUsuario(usuarioRequestDto);
        });

        assertEquals("El email ya está registrado: " + usuarioRequestDto.getEmail(), exception.getMessage());
        verify(usuarioRepository).existsByEmail(usuarioRequestDto.getEmail());
        verifyNoInteractions(rolRepository, passwordEncoder);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Registrar Nuevo Usuario - Rol No Encontrado")
    void registrarNuevoUsuario_RolNoEncontrado() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(rolRepository.findById(usuarioRequestDto.getRolId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.registrarNuevoUsuario(usuarioRequestDto);
        });

        assertEquals("Rol no encontrado con ID: " + usuarioRequestDto.getRolId(), exception.getMessage());
        verify(rolRepository).findById(usuarioRequestDto.getRolId());
        verifyNoInteractions(passwordEncoder);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Registrar Nuevo Usuario - Contraseña Inválida (Corta)")
    void registrarNuevoUsuario_PasswordInvalidaCorta() {
        usuarioRequestDto.setPassword("corta");
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(rolRepository.findById(usuarioRequestDto.getRolId())).thenReturn(Optional.of(rolOperario));

        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            usuarioService.registrarNuevoUsuario(usuarioRequestDto);
        });

        assertTrue(exception.getMessage().contains("La contraseña debe tener al menos"));
        verifyNoInteractions(passwordEncoder);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Registrar Nuevo Usuario - Contraseña Nula")
    void registrarNuevoUsuario_PasswordNula() {
        usuarioRequestDto.setPassword(null);
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(rolRepository.findById(usuarioRequestDto.getRolId())).thenReturn(Optional.of(rolOperario));

        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            usuarioService.registrarNuevoUsuario(usuarioRequestDto);
        });

        assertEquals("La contraseña es obligatoria para crear un nuevo usuario.", exception.getMessage());
        verifyNoInteractions(passwordEncoder);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Actualizar Usuario - Éxito (Sin cambio de contraseña)")
    void actualizarUsuario_ExitoSinCambioPassword() {
        Long operarioId = usuarioOperario.getUsuarioId();
        UsuarioRequestDTO dtoActualizar = new UsuarioRequestDTO("Operario Actualizado", "operario.nuevo@example.com", null, rolOperario.getRolId());

        when(usuarioRepository.findById(operarioId)).thenReturn(Optional.of(usuarioOperario));
        when(usuarioRepository.existsByEmail(dtoActualizar.getEmail())).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario usuarioActualizado = usuarioService.actualizarUsuario(operarioId, dtoActualizar);

        assertNotNull(usuarioActualizado);
        assertEquals(dtoActualizar.getNombre(), usuarioActualizado.getNombre());
        assertEquals(dtoActualizar.getEmail(), usuarioActualizado.getEmail());
        assertEquals(usuarioOperario.getPasswordHash(), usuarioActualizado.getPasswordHash());
        assertEquals(rolOperario, usuarioActualizado.getRol());
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository).save(usuarioOperario);
    }

    @Test
    @DisplayName("Actualizar Usuario - Éxito (Con cambio de contraseña)")
    void actualizarUsuario_ExitoConCambioPassword() {
        Long operarioId = usuarioOperario.getUsuarioId();
        String nuevaPassword = "nuevaPasswordSegura123";
        String nuevaPasswordEncoded = "encodedNuevaPassword";
        UsuarioRequestDTO dtoActualizar = new UsuarioRequestDTO("Operario Con Pass Nueva", usuarioOperario.getEmail(), nuevaPassword, rolOperario.getRolId());

        when(usuarioRepository.findById(operarioId)).thenReturn(Optional.of(usuarioOperario));
        when(passwordEncoder.encode(nuevaPassword)).thenReturn(nuevaPasswordEncoded);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario usuarioActualizado = usuarioService.actualizarUsuario(operarioId, dtoActualizar);

        assertNotNull(usuarioActualizado);
        assertEquals(dtoActualizar.getNombre(), usuarioActualizado.getNombre());
        assertEquals(usuarioOperario.getEmail(), usuarioActualizado.getEmail());
        assertEquals(nuevaPasswordEncoded, usuarioActualizado.getPasswordHash());
        assertEquals(rolOperario, usuarioActualizado.getRol());
        verify(passwordEncoder).encode(nuevaPassword);
        verify(usuarioRepository).save(usuarioOperario);
    }

    @Test
    @DisplayName("Actualizar Usuario - Email Duplicado")
    void actualizarUsuario_EmailDuplicado_DebeLanzarExcepcion() {
        Long operarioId = usuarioOperario.getUsuarioId();
        UsuarioRequestDTO dtoEmailDuplicado = new UsuarioRequestDTO("Nuevo Nombre", usuarioAdmin.getEmail(), null, rolOperario.getRolId());

        when(usuarioRepository.findById(operarioId)).thenReturn(Optional.of(usuarioOperario));
        when(usuarioRepository.existsByEmail(usuarioAdmin.getEmail())).thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
            usuarioService.actualizarUsuario(operarioId, dtoEmailDuplicado);
        });

        assertEquals("El email ya está registrado: " + usuarioAdmin.getEmail(), exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Actualizar Usuario - Nuevo Rol No Encontrado")
    void actualizarUsuario_NuevoRolNoEncontrado_DebeLanzarExcepcion() {
        Long operarioId = usuarioOperario.getUsuarioId();
        Long idRolInexistente = 99L;
        UsuarioRequestDTO dtoRolInexistente = new UsuarioRequestDTO(usuarioOperario.getNombre(), usuarioOperario.getEmail(), null, idRolInexistente);

        when(usuarioRepository.findById(operarioId)).thenReturn(Optional.of(usuarioOperario));
        when(rolRepository.findById(idRolInexistente)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.actualizarUsuario(operarioId, dtoRolInexistente);
        });

        assertEquals("Rol no encontrado con ID: " + idRolInexistente, exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }


    @Test
    @DisplayName("Eliminar Usuario - Último Admin (Falla)")
    void eliminarUsuario_UltimoAdmin_DebeLanzarExcepcion() {
        Long adminId = usuarioAdmin.getUsuarioId();
        when(usuarioRepository.findById(adminId)).thenReturn(Optional.of(usuarioAdmin));
        when(usuarioRepository.countByRol_Nombre("ADMIN")).thenReturn(1L);

        IllegalOperationException exception = assertThrows(IllegalOperationException.class, () -> {
            usuarioService.eliminarUsuario(adminId);
        });

        assertEquals("No se puede eliminar el único administrador del sistema.", exception.getMessage());
        verify(usuarioRepository, never()).delete(any(Usuario.class));
    }

    @Test
    @DisplayName("Eliminar Usuario - Éxito (No es último admin)")
    void eliminarUsuario_Exito() {
        Long operarioId = usuarioOperario.getUsuarioId();
        when(usuarioRepository.findById(operarioId)).thenReturn(Optional.of(usuarioOperario));

        assertDoesNotThrow(() -> {
            usuarioService.eliminarUsuario(operarioId);
        });

        verify(usuarioRepository).delete(usuarioOperario);
    }

    @Test
    @DisplayName("Eliminar Usuario - No Encontrado")
    void eliminarUsuario_NoEncontrado_DebeLanzarExcepcion() {
        Long idInexistente = 999L;
        when(usuarioRepository.findById(idInexistente)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.eliminarUsuario(idInexistente);
        });

        assertEquals("Usuario no encontrado con ID: " + idInexistente, exception.getMessage());
        verify(usuarioRepository, never()).delete(any(Usuario.class));
    }

    @Test
    @DisplayName("Cambiar Estado Usuario - Inactivar Último Admin Activo (Falla)")
    void cambiarEstadoUsuario_InactivarUltimoAdmin_DebeLanzarExcepcion() {
        Long adminId = usuarioAdmin.getUsuarioId();
        when(usuarioRepository.findById(adminId)).thenReturn(Optional.of(usuarioAdmin));
        when(usuarioRepository.countByRol_Nombre("ADMIN")).thenReturn(1L);
        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioAdmin)); // Solo él es admin activo

        IllegalOperationException exception = assertThrows(IllegalOperationException.class, () -> {
            usuarioService.cambiarEstadoUsuario(adminId, "INACTIVO");
        });

        assertEquals("No se puede inactivar al único administrador activo del sistema.", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Cambiar Estado Usuario - Estado Inválido")
    void cambiarEstadoUsuario_EstadoInvalido_DebeLanzarExcepcion() {
        Long operarioId = usuarioOperario.getUsuarioId();
        String estadoInvalido = "PENDIENTE";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cambiarEstadoUsuario(operarioId, estadoInvalido);
        });

        assertTrue(exception.getMessage().contains("Estado inválido: PENDIENTE"));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Cambiar Estado Usuario - Éxito")
    void cambiarEstadoUsuario_Exito() {
        Long operarioId = usuarioOperario.getUsuarioId();
        String nuevoEstado = "INACTIVO";
        when(usuarioRepository.findById(operarioId)).thenReturn(Optional.of(usuarioOperario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario usuarioActualizado = usuarioService.cambiarEstadoUsuario(operarioId, nuevoEstado);

        assertNotNull(usuarioActualizado);
        assertEquals(nuevoEstado, usuarioActualizado.getEstado());
        verify(usuarioRepository).save(usuarioOperario);
    }

    @Test
    @DisplayName("Cambiar Estado Usuario - Usuario No Encontrado")
    void cambiarEstadoUsuario_UsuarioNoEncontrado_DebeLanzarExcepcion() {
        Long idInexistente = 999L;
        String nuevoEstado = "ACTIVO";
        when(usuarioRepository.findById(idInexistente)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.cambiarEstadoUsuario(idInexistente, nuevoEstado);
        });

        assertEquals("Usuario no encontrado con ID: " + idInexistente, exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Obtener Usuario por ID - No Encontrado")
    void obtenerUsuarioPorId_NoEncontrado_DebeLanzarExcepcion() {
        Long idInexistente = 999L;
        when(usuarioRepository.findById(idInexistente)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.obtenerUsuarioPorId(idInexistente);
        });
        assertEquals("Usuario no encontrado con ID: " + idInexistente, exception.getMessage());
    }
}