package cl.duoc.demomicroservicio.services;

import java.util.List;

import cl.duoc.demomicroservicio.model.Usuario;

import org.springframework.stereotype.Service;

import cl.duoc.demomicroservicio.repository.UsuarioRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class UsuarioService {

    private UsuarioRepository usuariorepository;

    public UsuarioService(UsuarioRepository usuariorepository){
        this.usuariorepository = usuariorepository;
    }

    public List<Usuario> BuscarTodo(){    
        return usuariorepository.findAll();
    }

    public Usuario BuscarUnUsuario(String rut){
        return usuariorepository.findById(rut).get();
    }

    public Usuario Guardar(Usuario usuario){
        return usuariorepository.save(usuario);
    }

    public void Eliminar(String rut){
        usuariorepository.deleteById(rut);
    }


}
