package com.ucaldas.mssecurity.Controllers;

import com.ucaldas.mssecurity.Models.User;
import com.ucaldas.mssecurity.Repositories.UserRepository;
import com.ucaldas.mssecurity.services.EncryptionService;
import com.ucaldas.mssecurity.services.JwtService;
import com.ucaldas.mssecurity.services.NotificationsService;
import java.util.Random;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("api/public/security")
public class SecurityController {
    @Autowired
    private UserRepository theUserRepository;
    @Autowired
    private EncryptionService theEncryptionService;
    @Autowired
    private JwtService theJwtService;
    @Autowired
    private NotificationsService notificationsService; 

    @PostMapping("/login")
    public User login(@RequestBody User theNewUser, final HttpServletResponse response) throws IOException, java.io.IOException {
        String token = "";
        String tok = "";
        User theActualUser = this.theUserRepository.getUserByEmail(theNewUser.getEmail());
        
        if (theActualUser != null && theActualUser.getPassword().equals(theEncryptionService.convertSHA256(theNewUser.getPassword()))) {
            token = theJwtService.generateToken(theActualUser);
            tok=generadorCodigoSecondAF();
            theActualUser.setToken(tok);
            this.theUserRepository.save(theActualUser);

            notificationsService.sendCodeByEmail(theActualUser, tok);
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
        return theActualUser;
    }
    @PutMapping("/second-Factor")
    public User secondFactor(
        @RequestBody User thUser, 
        final HttpServletResponse response
    ) throws IOException, java.io.IOException {
        User theActualUser = this.theUserRepository.getUserByEmail(thUser.getEmail());
    
        // Verifica si el usuario y la contraseña son válidos
        if (theActualUser != null && theActualUser.getToken() != null && 
        theActualUser.getToken().equals(thUser.getToken())) {
                // Si coinciden, retorna éxito
            return theActualUser;

        } else {
            String secondAuthentication = generadorCodigoSecondAF();
            if (theActualUser!=null) {
                theActualUser.setToken(secondAuthentication); // Actualiza el token
                this.theUserRepository.save(theActualUser); // Guarda el cambio
                notificationsService.sendCodeByEmail(theActualUser, secondAuthentication); // Envía el código 
            }
                       
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "token incorrecto");
            return theActualUser; // No se necesita retorno adicional
        }
    }

    @PostMapping("/reset-password")
    public String recoveryPassword(@RequestBody User theUser, final HttpServletResponse response) throws IOException, java.io.IOException {
        User theActualUser = this.theUserRepository.getUserByEmail(theUser.getEmail());
        if (theActualUser != null) {
            String nuevaContrasena = generarContrasenaAleatoria();
            // Actualiza la contraseña del usuario en la base de datos
            theActualUser.setPassword(theEncryptionService.convertSHA256(nuevaContrasena));
            this.theUserRepository.save(theActualUser);
            // Envía el correo electrónico de restablecimiento de contraseña usando la instancia de NotificationsService
            notificationsService.sendPasswordResetEmail(theActualUser, nuevaContrasena);
            return nuevaContrasena;
        } else {
            return null;
        }   
    }


    public static String generarContrasenaAleatoria() {
        return UUID.randomUUID().toString().substring(0, 8); 
    }
    public static String generadorCodigoSecondAF(){
        Random random = new Random();
        int codigo = 100001 + random.nextInt(900000);
        String token = Integer.toString(codigo);
        return token;
    }
    
}
