package com.ucaldas.mssecurity.Controllers;

import com.ucaldas.mssecurity.Models.Role;
import com.ucaldas.mssecurity.Models.User;
import com.ucaldas.mssecurity.Repositories.RoleRepository;
import com.ucaldas.mssecurity.Repositories.UserRepository;
import com.ucaldas.mssecurity.services.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private UserRepository theUserRepository;
    @Autowired
    private EncryptionService theEncryptionService;
    @Autowired
    private RoleRepository theRoleRepository;
    @GetMapping("")
    public List<User> findAll(){
        return this.theUserRepository.findAll();
    }
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public User create(@RequestBody User theNewUser){
        theNewUser.setPassword(theEncryptionService.convertSHA256(theNewUser.getPassword()));
        return this.theUserRepository.save(theNewUser);
    }
    @GetMapping("{id}")
    public User findById(@PathVariable String id) {
        User theUser = this.theUserRepository
                .findById(id)
                .orElse(null);
        return theUser;
    }

    @PutMapping("{id}")
    public User update(@PathVariable String id, @RequestBody User theNewUser) {
        User theActualUser = this.theUserRepository
                .findById(id)
                .orElse(null);
        if (theActualUser != null) {
            theActualUser.setName(theNewUser.getName());
            theActualUser.setEmail(theNewUser.getEmail());
            theActualUser.setPassword(theEncryptionService.convertSHA256(theNewUser.getPassword()));
            return this.theUserRepository.save(theActualUser);
        } else {
            return null;
        }
    }
    public String generarContrasenaAleatoria() {
        // Generar una contraseña aleatoria usando UUID
        return UUID.randomUUID().toString().substring(0, 8); // Puedes ajustar la longitud de la contraseña
    }


    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        User theUser = this.theUserRepository
                .findById(id)
                .orElse(null);
        if (theUser != null) {
            this.theUserRepository.delete(theUser);
        }
    }

    @PutMapping("{userId}/role/{roleId}")
    public User matchRole(@PathVariable String userId,@PathVariable String roleId) {
        User theActualUser = this.theUserRepository
                .findById(userId)
                .orElse(null);
        Role theActualRole=this.theRoleRepository
                .findById(roleId)
                .orElse(null);

        if (theActualUser != null && theActualRole!=null) {
            theActualUser.setRole(theActualRole);
            return this.theUserRepository.save(theActualUser);
        } else {
            return null;
        }
    }

    @PutMapping("{userId}/unmatch-role/{roleId}")
    public User unMatchRole(@PathVariable String userId,@PathVariable String roleId) {
        User theActualUser = this.theUserRepository
                .findById(userId)
                .orElse(null);
        Role theActualRole=this.theRoleRepository
                .findById(roleId)
                .orElse(null);

        if (theActualUser != null
                && theActualRole!=null
                && theActualUser.getRole().get_id().equals(roleId)) {
            theActualUser.setRole(null);
            return this.theUserRepository.save(theActualUser);
        } else {
            return null;
        }
    }
    @PutMapping("{id}/reset-password")
public User RecoveryPassword(@PathVariable String id){
    User theActualUser = this.theUserRepository.findById(id).orElse(null);
    if (theActualUser != null) {
        // Generar una nueva contraseña aleatoria
        String nuevaContrasena = generarContrasenaAleatoria();
        // Actualizar la contraseña del usuario en la base de datos
        theActualUser.setPassword(theEncryptionService.convertSHA256(nuevaContrasena));
        this.theUserRepository.save(theActualUser);
        return theActualUser;
    
    }else{
        return null;
    }
}

}
