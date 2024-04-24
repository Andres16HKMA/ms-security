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
    @PutMapping("/second-Factor")
    public String SecondFactor(@RequestBody User thUser, final HttpServletResponse response) throws IOException, java.io.IOException{
        String secondautentification = ""; 
        User theActualUser = this.theUserRepository.getUserByEmail(thUser.getEmail());
        if (theActualUser !=null && theActualUser.getPassword().equals(theEncryptionService.convertSHA256(thUser.getPassword()))){
            if (theActualUser.getToken()==thUser.getToken()){
                return "Ha ingresado de forma satisfactoria";
            }else{
                if (theActualUser.getToken() != null) {
                    response.getWriter().write("Código ya enviado");            }
                }
                secondautentification = generadorCodigoSecondAF();
                theActualUser.setToken(secondautentification);
                notificationsService.sendCodeByEmail(theActualUser, secondautentification);
                response.getWriter().write("se ha enviado un codigo a su correo");            
        }else{
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
        return secondautentification;
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
        int codigo = 100000 + random.nextInt(900000);
        String token = Integer.toString(codigo);
        return token;
    }
    
}
