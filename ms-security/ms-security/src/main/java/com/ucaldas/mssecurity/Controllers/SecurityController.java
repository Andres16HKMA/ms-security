package com.ucaldas.mssecurity.Controllers;
import com.azure.communication.email.*;
import com.azure.communication.email.models.*;
import com.azure.core.util.polling.PollResponse;
import com.azure.core.util.polling.SyncPoller;
import com.ucaldas.mssecurity.Models.User;
import com.ucaldas.mssecurity.Repositories.UserRepository;
import com.ucaldas.mssecurity.services.EncryptionService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ucaldas.mssecurity.services.JwtService;
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

    @PostMapping("/login")
    public String login(@RequestBody User theNewUser, final HttpServletResponse response)throws IOException {
        String token = "";
        User theActualUser = this.theUserRepository.getUserByEmail(theNewUser.getEmail());
        if (theActualUser != null && theActualUser.getPassword().equals(theEncryptionService.convertSHA256(theNewUser.getPassword()))) {
            token = theJwtService.generateToken(theActualUser);
        }else{
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
        return token;
    }
    @PutMapping("/reset-password")
    public User resetpassword(@RequestBody User theUser, final HttpServletResponse response) throws IOException {
        User theActualUser = this.theUserRepository.getUserByEmail(theUser.getEmail());
        if (theActualUser != null) {
            // Genera una nueva contraseña aleatoria
            String nuevaContrasena = generarContrasenaAleatoria();
            // Actualiza la contraseña del usuario en la base de datos
            if (theActualUser.getEmail()!=null){
                theActualUser.setPassword(theEncryptionService.convertSHA256(nuevaContrasena));
                this.theUserRepository.save(theActualUser);
                SendEmail(theActualUser.getEmail(), nuevaContrasena);
                return theActualUser;
            }else{
                return null;
            }
        } else {
            return null;
        }
    }
    public String generarContrasenaAleatoria() {
        // Generar una contraseña aleatoria usando UUID
        return UUID.randomUUID().toString().substring(0, 8); // Puedes ajustar la longitud de la contraseña
    }
    private void SendEmail(String email, String nuevaContrasena){
                // Envía la nueva contraseña por correo electrónico
                String connectionString = "endpoint=https://hkma-notificaciones.unitedstates.communication.azure.com/;accesskey=1Hvrk2Kl5lFn5O/5oYX/60Rz1zduUGVSnCG+7GQ4MeWl8XgJ5Es0sdn6fb67EkyH7rC1Poahjv9dtVj9xqB6DQ==";
                EmailClient emailClient = new EmailClientBuilder().connectionString(connectionString).buildClient();
        
                EmailAddress toAddress = new EmailAddress(email);
                
                EmailMessage emailMessage = new EmailMessage()
                    .setSenderAddress("DoNotReply@7ebed90e-32ff-41b7-968b-99da1740422d.azurecomm.net")
                    .setToRecipients(toAddress)
                    .setSubject(nuevaContrasena)
                    .setBodyPlainText("Esta es la nueva contraseña."+ nuevaContrasena);
        
                SyncPoller<EmailSendResult, EmailSendResult> poller = emailClient.beginSend(emailMessage, null);
                PollResponse<EmailSendResult> result = poller.waitForCompletion();
    }
}
