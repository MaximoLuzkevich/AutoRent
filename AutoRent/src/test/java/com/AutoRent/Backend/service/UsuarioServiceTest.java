package com.AutoRent.Backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.AutoRent.Backend.dto.usuario.AuthRespuestaDto;
import com.AutoRent.Backend.dto.usuario.LoginDto;
import com.AutoRent.Backend.dto.usuario.RegistroUsuarioDto;
import com.AutoRent.Backend.exception.DatoDuplicadoException;
import com.AutoRent.Backend.exception.LoginRequeridoException;
import com.AutoRent.Backend.model.Rol;
import com.AutoRent.Backend.model.Usuario;
import com.AutoRent.Backend.model.enums.NombreRol;
import com.AutoRent.Backend.repository.UsuarioRepository;
import com.AutoRent.Backend.security.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolService rolService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void iniciarSesionConPasswordIncorrectaLanzaLoginRequerido() {
        Usuario usuario = new Usuario();
        usuario.setEmail("cliente@test.com");
        usuario.setPassword("hash");
        usuario.setActivo(true);

        when(usuarioRepository.findByEmailIgnoreCaseConRoles("cliente@test.com"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("incorrecta", "hash")).thenReturn(false);

        LoginDto dto = new LoginDto("cliente@test.com", "incorrecta");

        assertThrows(LoginRequeridoException.class, () -> usuarioService.iniciarSesion(dto));
    }

    @Test
    void iniciarSesionCorrectoDevuelveTokenYUsuario() {
        Rol rolCliente = new Rol(1, NombreRol.CLIENTE);

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setNombre("Cliente Test");
        usuario.setEmail("cliente@test.com");
        usuario.setPassword("hash");
        usuario.setActivo(true);
        usuario.getRoles().add(rolCliente);

        LoginDto dto = new LoginDto("cliente@test.com", "123456");

        when(usuarioRepository.findByEmailIgnoreCaseConRoles("cliente@test.com"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("123456", "hash")).thenReturn(true);
        when(jwtService.generarToken(usuario)).thenReturn("token-test");

        AuthRespuestaDto respuesta = usuarioService.iniciarSesion(dto);

        assertEquals("token-test", respuesta.getToken());
        assertEquals("Bearer", respuesta.getTipoToken());
        assertEquals("cliente@test.com", respuesta.getUsuario().getEmail());
    }

    @Test
    void registrarUsuarioConEmailDuplicadoLanzaDatoDuplicado() {
        RegistroUsuarioDto dto = new RegistroUsuarioDto(
                "Cliente Test",
                "cliente@test.com",
                "123456",
                "1122334455"
        );

        when(usuarioRepository.existsByEmailIgnoreCase("cliente@test.com")).thenReturn(true);

        assertThrows(DatoDuplicadoException.class, () -> usuarioService.registrarUsuario(dto));
    }
}
