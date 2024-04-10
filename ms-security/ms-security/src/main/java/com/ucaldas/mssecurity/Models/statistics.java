package com.ucaldas.mssecurity.Models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document
@Data
public class statistics {
    @Id
    private String _id;
    private int NumberVisits;

    public statistics(int NumberVisits) {
        this.NumberVisits = NumberVisits;
    }

    public String get_id() {
        return _id;
    }
    public int get_NumberVisitis() {
        return NumberVisits;
    }
    public void set_NumberVisits(int NumberVisits) {
        this.NumberVisits = NumberVisits;
    }
}

