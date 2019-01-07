package com.jwierzb.parkingspaces.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import static lombok.AccessLevel.*;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Proxy;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.LinkedHashSet;

@Entity
@Table(name = "ROLE")
@FieldDefaults(level = PRIVATE)
@Data
@Proxy(lazy = false)
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @JsonIgnore
    Integer id;

    @NotNull
    @Column(name="ROLE_NAME", unique = true)
    String name;

    @Override
    public String getAuthority() {
        return "ROLE_"+name;
    }


}
