package cl.duoc.demomicroservicio.TestService;

import cl.duoc.demomicroservicio.controller.UsuarioController;
import cl.duoc.demomicroservicio.model.Usuario;
import cl.duoc.demomicroservicio.services.UsuarioService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;




@WebMvcTest(controllers = UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false) // si tienes Spring Security, esto desactiva filtros en los tests web
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    private static Usuario mkUsuario(String rut) {
        Usuario u = new Usuario();
        u.setRut(rut);
        u.setNombre("Juan");
        u.setMail("juan@acme.cl");
        u.setPassword("secret");
        u.setIdrol(1);
        return u;
    }

    // GET /api/v1/Usuarios  -> 200 con lista
    @Test
    void listar_ok() throws Exception {
        when(usuarioService.BuscarTodo()).thenReturn(List.of(mkUsuario("1-9"), mkUsuario("2-7")));

        mockMvc.perform(get("/api/v1/Usuarios"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(2)))
               .andExpect(jsonPath("$[0].rut", is("1-9")));
    }

    // GET /api/v1/Usuarios  -> 204 sin contenido
    @Test
    void listar_noContent() throws Exception {
        when(usuarioService.BuscarTodo()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/Usuarios"))
               .andExpect(status().isNoContent());
    }

    // GET /api/v1/Usuarios/{rut} -> 200 encontrado
    @Test
    void buscarUsuario_ok() throws Exception {
        when(usuarioService.BuscarUnUsuario("1-9")).thenReturn(mkUsuario("1-9"));

        mockMvc.perform(get("/api/v1/Usuarios/1-9"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.rut", is("1-9")));
    }

    // GET /api/v1/Usuarios/{rut} -> 404 no encontrado (service lanza excepción)
    @Test
    void buscarUsuario_notFound() throws Exception {
        when(usuarioService.BuscarUnUsuario("9-9")).thenThrow(new RuntimeException("no existe"));

        mockMvc.perform(get("/api/v1/Usuarios/9-9"))
               .andExpect(status().isNotFound())
               .andExpect(content().string(containsString("No se encuentra el usuario")));
    }

    // POST /api/v1/Usuarios -> 409 si ya existe
    @Test
    void guardar_conflict() throws Exception {
        when(usuarioService.BuscarUnUsuario("1-9")).thenReturn(mkUsuario("1-9"));

        String body = """
          {"rut":"1-9","nombre":"Juan","mail":"juan@acme.cl","password":"x","idrol":1}
        """;

        mockMvc.perform(post("/api/v1/Usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
               .andExpect(status().isConflict())
               .andExpect(content().string(containsString("El rut esta registrado")));
    }

    // POST /api/v1/Usuarios -> 201 creado cuando no existe (service lanza excepción en BuscarUnUsuario)
    @Test
    void guardar_created() throws Exception {
        when(usuarioService.BuscarUnUsuario("2-7")).thenThrow(new RuntimeException("no existe"));
        when(usuarioService.Guardar(any(Usuario.class))).thenReturn(mkUsuario("2-7"));

        String body = """
          {"rut":"2-7","nombre":"Ana","mail":"ana@acme.cl","password":"x","idrol":2}
        """;

        mockMvc.perform(post("/api/v1/Usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
               .andExpect(status().isCreated())
               .andExpect(content().string(containsString("Usuario registrado de manera exitosa")));
    }

    // DELETE /api/v1/Usuarios/{rut} -> 200 eliminado
    @Test
    void eliminar_ok() throws Exception {
        when(usuarioService.BuscarUnUsuario("1-9")).thenReturn(mkUsuario("1-9"));
        Mockito.doNothing().when(usuarioService).Eliminar("1-9");

        mockMvc.perform(delete("/api/v1/Usuarios/1-9"))
               .andExpect(status().isOk())
               .andExpect(content().string(containsString("Usuario eliminado de manera exitosa")));
    }

    // DELETE /api/v1/Usuarios/{rut} -> 404 si no existe
    @Test
    void eliminar_notFound() throws Exception {
        when(usuarioService.BuscarUnUsuario("9-9")).thenThrow(new RuntimeException("no existe"));

        mockMvc.perform(delete("/api/v1/Usuarios/9-9"))
               .andExpect(status().isNotFound())
               .andExpect(content().string(containsString("no existe")));
    }

    // PUT /api/v1/Usuarios/{rut} -> 200 actualizado
    @Test
    void actualizar_ok() throws Exception {
        when(usuarioService.BuscarUnUsuario("1-9")).thenReturn(mkUsuario("1-9"));
        when(usuarioService.Guardar(any(Usuario.class))).thenReturn(mkUsuario("1-9"));

        String body = """
          {"nombre":"Nuevo","mail":"nuevo@acme.cl","password":"y","idrol":3}
        """;

        mockMvc.perform(put("/api/v1/Usuarios/1-9")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.nombre", is("Nuevo")))
               .andExpect(jsonPath("$.idrol", is(3)));
    }

    // PUT /api/v1/Usuarios/{rut} -> 204 si service lanza excepción
    @Test
    void actualizar_noContent() throws Exception {
        when(usuarioService.BuscarUnUsuario("9-9")).thenThrow(new RuntimeException("no existe"));

        String body = """
          {"nombre":"X","mail":"x@acme.cl","password":"z","idrol":1}
        """;

        mockMvc.perform(put("/api/v1/Usuarios/9-9")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
               .andExpect(status().isNoContent());
    }
}