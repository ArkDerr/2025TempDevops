package cl.duoc.demomicroservicio.TestService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.Test;

import cl.duoc.demomicroservicio.model.Usuario;
import cl.duoc.demomicroservicio.repository.UsuarioRepository;
import cl.duoc.demomicroservicio.services.UsuarioService;

public class TestUsuarioService {

    @Mock
    private UsuarioRepository usuariorepository;

    @InjectMocks
    private UsuarioService usuarioservice;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testBuscarTodo(){

        List<Usuario> lista = new ArrayList<>();

        Usuario user1 = new Usuario();
        Usuario user2 = new Usuario();

        user1.setPassword("123456");
        user1.setRut("12.345.678-9");
        user1.setNombre("Alan Brito");
        user1.setMail("alan.brito@mail.com");

        user1.setPassword("123456");
        user2.setRut("13.254.987-6");
        user1.setNombre("Maria Dolores");
        user1.setMail("maria.dolores@mail.com");

        lista.add(user1);
        lista.add(user2);

        when(usuariorepository.findAll()).thenReturn(lista);

        List<Usuario> resultadoBusqueda = usuarioservice.BuscarTodo();

        assertEquals(2, resultadoBusqueda.size());
        verify(usuariorepository, times(1)).findAll();
    }

    @Test
    public void testBuscarUnUsuario(){
        Usuario user1 = new Usuario();
        user1.setPassword("123456");
        user1.setRut("12.345.678-9");
        user1.setNombre("Alan Brito");
        user1.setMail("alan.brito@mail.com");

        when(usuariorepository.findById("12.345.678-9")).thenReturn(Optional.of(user1));

        Usuario usuarioBuscado = usuarioservice.BuscarUnUsuario("12.345.678-9");
        assertEquals("12.345.678-9", usuarioBuscado.getRut());
        verify(usuariorepository, times(1)).findById("12.345.678-9");
    }

    @Test
    public void testGuardarUsuario(){
        Usuario user1 = new Usuario();
        user1.setPassword("123456");
        user1.setRut("12.345.678-9");
        user1.setNombre("Alan Brito");
        user1.setMail("alan.brito@mail.com");

        when(usuariorepository.save(user1)).thenReturn(user1);

        Usuario usuarioGuardado = usuarioservice.Guardar(user1);

        assertEquals("12.345.678-9", usuarioGuardado.getRut());
        verify(usuariorepository, times(1)).save(user1);

    }

    @Test
    public void testeliminarUsuario(){
        String rut = "12.345.678-9";
        doNothing().when(usuariorepository).deleteById(rut);

        usuarioservice.Eliminar(rut);

        verify(usuariorepository,times(1)).deleteById(rut);
    }

}
