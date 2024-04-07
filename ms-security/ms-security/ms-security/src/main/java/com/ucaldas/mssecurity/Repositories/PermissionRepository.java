package com.ucaldas.mssecurity.Repositories;

import com.ucaldas.mssecurity.Models.Permission;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PermissionRepository extends MongoRepository<Permission,String>{
}
