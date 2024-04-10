package com.ucaldas.mssecurity.Repositories;

import com.ucaldas.mssecurity.Models.Permission;
import com.ucaldas.mssecurity.Models.statistics;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface statisticRepository extends MongoRepository<Permission,String> {
    @Query("{ 'NumberVisits' : ?0}")
    statistics getNumberVisits(String NumberVisits );
}
