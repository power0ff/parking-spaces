package com.jwierzb.parkingspaces.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;


@Entity
@Table(name = "USER")
@FieldDefaults(level = PRIVATE)
@Builder
@AllArgsConstructor
@Data
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Column(name = "ID")
    Long id;

    @NotNull
    @Column(name = "PHONE_NUMBER", nullable = false)
    Integer phoneNumber;


    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    DriverType driverType;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    Currency currency;


    @NotNull
    @Column(name = "FIRST_NAME")
    String firstName;

    @NotNull
    @Column(name = "LAST_NAME")
    String lastName;

    @NotNull
    @Email
    @Column(name="EMAIL", unique = true)
    String email;

    @NotNull
    @JsonIgnore
    @Column(name = "PASSWORD")
    String password;

    @NotNull
    @Column(name = "USER_NAME", unique = true)
    String username;

    @NotNull
    @JsonIgnore
    @Column(name = "ENABLED")
    Boolean enabled;

    @NotNull
    @Column(name = "NON_LOCKED")
    @JsonIgnore
    Boolean nonLocked;

    @NotNull
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "USER_ROLES",
            joinColumns = @JoinColumn(name="USER_ID", referencedColumnName="ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")
    )

    @JsonIgnore
    List<Role> role;

    @NotNull
    @Column(name = "CREATED_DATE", nullable = false, updatable = false)
    @JsonIgnore
    LocalDateTime createdAt;

    @JsonGetter
    String getCreatedAt(){return createdAt.toString();}
    @PrePersist
    void createdAt(){
        this.createdAt = LocalDateTime.now();
    }

    @JsonGetter
    String getCurrencyName() {return currency.getName();}

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return role;
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return username;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return nonLocked;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return enabled;
    }

    public UserEntity(){}
}
