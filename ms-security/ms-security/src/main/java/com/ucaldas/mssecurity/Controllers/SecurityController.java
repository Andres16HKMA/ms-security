package com.ucaldas.mssecurity.Controllers;
import com.ucaldas.mssecurity.Models.User;
import com.ucaldas.mssecurity.Repositories.UserRepository;
import com.ucaldas.mssecurity.services.EncryptionService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ucaldas.mssecurity.services.JwtService;
import com.ucaldas.mssecurity.services.NotificationsService;
import java.util.UUID;
import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("/security")
public class SecurityController {
    @Autowired
    private UserRepository theUserRepository;
    @Autowired
    private EncryptionService theEncryptionService;
    @Autowired
    private JwtService theJwtService;
    @Autowired
    private NotificationsService theNotificationsService;
    @PostMapping("/login")
    public String login(@RequestBody User theNewUser, final HttpServletResponse response) throws IOException {
        String token = "";
        User theActualUser = this.theUserRepository.getUserByEmail(theNewUser.getEmail());
        
        if (theActualUser != null && theActualUser.getPassword().equals(theEncryptionService.convertSHA256(theNewUser.getPassword()))) {
            // Verificar si el usuario tiene un token
            if (theActualUser.getToken() == null || theActualUser.getToken().isEmpty()) {
                // Si no tiene token, generar uno nuevo y guardarlo en la base de datos
                token = theJwtService.generateToken(theActualUser);
                theActualUser.setToken(token);
                this.theUserRepository.save(theActualUser);
                theNotificationsService.sendCodeByEmail(theActualUser, token);
                return "Se ha enviado un token por correo electrónico. Por favor, ingrese el token para iniciar sesión.";
            } else {
                // Si ya tiene un token, retornar un mensaje indicando que se requiere el token para iniciar sesión
                return "Se requiere un token para iniciar sesión. Por favor, ingrese el token.";
            }
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return "Credenciales inválidas.";
        }
    }
    @PostMapping("/reset-password")
    public User resetpassword(@RequestBody User theUser, final HttpServletResponse response) throws IOException {
        User theActualUser = this.theUserRepository.getUserByEmail(theUser.getEmail());
        if (theActualUser != null) {
            // Genera una nueva contraseña aleatoria
            String nuevaContrasena = generarContrasenaAleatoria();
            // Actualiza la contraseña del usuario en la base de datos
            if (theActualUser.getEmail()!=null){
                theActualUser.setPassword(theEncryptionService.convertSHA256(nuevaContrasena));
                this.theUserRepository.save(theActualUser);
                theNotificationsService.sendPasswordResetEmail(theActualUser, nuevaContrasena);
                return theActualUser;
            }else{
                return null;
            }
        } else {
            return null;
        }
    }
    @PostMapping("/second-factor-authentication")
    public String secondFactorAutentification(@RequestBody User theNewUser, final HttpServletResponse response) throws IOException {
        // Verificar si el token ingresado es igual al token enviado al usuario
        String enteredToken = theNewUser.getToken(); // Supongo que el token ingresado está en el campo 'token' del objeto User
        
        // Verificar si el token ingresado es igual al token del usuario almacenado en la base de datos o en algún lugar seguro
        User theUser = theUserRepository.getUserByEmail(theNewUser.getEmail());
        String userToken = theUser != null ? theUser.getToken() : null;
        
        if (userToken != null && enteredToken != null && userToken.equals(enteredToken)) {
            // Si los tokens son iguales, permitir tanto el inicio de sesión como la recuperación de contraseña
            return "Autenticación de segundo factor exitosa. Se permitirá tanto el inicio de sesión como la recuperación de contraseña.";
        } else {
            // Si los tokens no son iguales, negar el acceso
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Autenticación de segundo factor fallida. Token incorrecto.");
            return "Autenticación de segundo factor fallida. Token incorrecto.";
        }
    }
    
    public String generarContrasenaAleatoria() {
        // Generar una contraseña aleatoria usando UUID
        return UUID.randomUUID().toString().substring(0, 8); // Puedes ajustar la longitud de la contraseña

}
}
