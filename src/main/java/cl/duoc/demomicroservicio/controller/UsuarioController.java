package cl.duoc.demomicroservicio.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.demomicroservicio.model.Usuario;
import cl.duoc.demomicroservicio.services.UsuarioService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@CrossOrigin(
  origins = {"http://localhost:8000", "http://127.0.0.1:8000", "http://localhost"},
  allowedHeaders = {"X-API-KEY", "Content-Type", "Authorization", "X-Requested-With"},
  methods = {RequestMethod.GET, RequestMethod.OPTIONS},
  maxAge = 3600
)

@RestController
@RequestMapping("/api/v1/Usuarios")
public class UsuarioController {


    @Autowired
    private UsuarioService usuarioservice;

    //Endpoint get para obtener todos los usuarios
    @GetMapping
    public ResponseEntity<List<Usuario>> Listar(){
        List<Usuario> usuarios = usuarioservice.BuscarTodo();
        if(usuarios.isEmpty()){
            return ResponseEntity.noContent().build();
        }else{
            return ResponseEntity.ok(usuarios);
        }
    }

    @GetMapping("/{rut}")
    public ResponseEntity<?> BuscarUsuario(@PathVariable String rut){
        try {
            Usuario usuario = usuarioservice.BuscarUnUsuario(rut);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encuentra el usuario");
        }
    }
    
    @PostMapping
    public ResponseEntity<String> guardar(@RequestBody Usuario usuario){
        try {
            Usuario usuarioaCrear = usuarioservice.BuscarUnUsuario(usuario.getRut());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El rut esta registrado");
        } catch (Exception e) {
            Usuario usuarioNuevo = usuarioservice.Guardar(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado de manera exitosa");
        } 
    }

    @DeleteMapping("/{rut}")
    public ResponseEntity<?> eliminar(@PathVariable String rut){
        try {
            Usuario usuarioaEliminar = usuarioservice.BuscarUnUsuario(rut);
            usuarioservice.Eliminar(rut);
            return ResponseEntity.status(HttpStatus.OK).body("Usuario eliminado de manera exitosa");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario que desea eliminar no existe");
        }
    }
    
    @PutMapping("/{rut}")
    public ResponseEntity<Usuario> actualizar(@PathVariable String rut, @RequestBody Usuario usuario){
        try {
            Usuario usuarioEditar = usuarioservice.BuscarUnUsuario(rut);
            usuarioEditar.setRut(rut);
            usuarioEditar.setNombre(usuario.getNombre());
            usuarioEditar.setMail(usuario.getMail());
            usuarioEditar.setPassword(usuario.getPassword());
            usuarioEditar.setIdrol(usuario.getIdrol());

            usuarioservice.Guardar(usuarioEditar);
            return ResponseEntity.ok(usuarioEditar);

        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

}
