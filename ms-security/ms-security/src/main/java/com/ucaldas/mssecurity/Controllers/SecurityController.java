package com.ucaldas.mssecurity.Controllers;

import com.ucaldas.mssecurity.Models.User;
import com.ucaldas.mssecurity.Repositories.UserRepository;
import com.ucaldas.mssecurity.services.EncryptionService;
import com.ucaldas.mssecurity.services.JwtService;
import com.ucaldas.mssecurity.services.NotificationsService;

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
    public String login(@RequestBody User theNewUser, final HttpServletResponse response) throws IOException, java.io.IOException {
        String token = "";
        User theActualUser = this.theUserRepository.getUserByEmail(theNewUser.getEmail());
        if (theActualUser != null && theActualUser.getPassword().equals(theEncryptionService.convertSHA256(theNewUser.getPassword()))) {
            token = theJwtService.generateToken(theActualUser);
            notificationsService.sendCodeByEmail(theActualUser, token);
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
        return token;
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

    public String generarContrasenaAleatoria() {
        return UUID.randomUUID().toString().substring(0, 8); 
    }
    
}
