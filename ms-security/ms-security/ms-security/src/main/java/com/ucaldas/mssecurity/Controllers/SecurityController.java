package com.ucaldas.mssecurity.Controllers;

import com.ucaldas.mssecurity.Models.User;
import com.ucaldas.mssecurity.Repositories.UserRepository;
import com.ucaldas.mssecurity.services.EncryptionService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ucaldas.mssecurity.services.JwtService;

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
}
