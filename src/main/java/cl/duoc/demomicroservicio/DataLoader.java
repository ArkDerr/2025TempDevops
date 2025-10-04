package cl.duoc.demomicroservicio;

import java.util.Locale;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import cl.duoc.demomicroservicio.model.Usuario;
import cl.duoc.demomicroservicio.services.UsuarioService;
import net.datafaker.Faker;

@Component
public class DataLoader implements CommandLineRunner{

    private final Faker faker = new Faker(new Locale("es", "cl"));
    private final Random random = new Random();

    @Autowired
    private UsuarioService usuarioservice;

    @Override
    public void run(String... args) throws Exception{
       for(int i=0; i < 10; i++){
        Usuario usuarionuevo = new Usuario();
        usuarionuevo.setRut(generarRutFalso());
        usuarionuevo.setIdrol(generarIdRol());
        usuarionuevo.setNombre(faker.name().fullName());
        usuarionuevo.setMail(faker.internet().emailAddress());
        usuarionuevo.setPassword(faker.internet().password());

        usuarioservice.Guardar(usuarionuevo);
        System.out.println("Usuario registrado: "+usuarionuevo.getRut());
       } 

    }

    private String generarRutFalso() {
        int cuerpo = 10000000 + random.nextInt(8999999);
        String dv = calculardv(cuerpo);
        return cuerpo + "-" + dv;
    }

    private String calculardv(int cuerpo) {
        int m = 0, s = 1;
        while (cuerpo != 0) {
            s = (s + cuerpo % 10 * (9 - m++ % 6)) % 11;
            cuerpo /= 10;
        }
        if (s == 0) return "K";
        if (s == 1) return "0";
        return String.valueOf(11 - s);
    }

    private Integer generarIdRol(){
        int numero = random.nextInt(3) + 1;
        return numero;
    }


}
