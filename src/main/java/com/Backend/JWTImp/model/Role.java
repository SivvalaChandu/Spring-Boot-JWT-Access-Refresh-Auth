package com.Backend.JWTImp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "roles")
public class Role {

    @Id
    private String id;
    private String name;
}