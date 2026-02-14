package com.helinok.pzbad_registration.Models;
import com.helinok.pzbad_registration.Enums.PartnershipStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@ToString
@Getter
@Setter
@Table(name = "users")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "team_name")
    private String teamName;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate = LocalDateTime.now();

    @Column(name = "is_active")
    private boolean isActive = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Collection<Role> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserTournament> userTournaments;

    @OneToMany(mappedBy = "player1", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Partnership> initiatedPartnerships = new HashSet<>();

    @OneToMany(mappedBy = "player2", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Partnership> receivedPartnerships = new HashSet<>();

    @OneToMany(mappedBy = "creatorOfTournament", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Tournament> createdTournaments = new ArrayList<>();


    public Collection<GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }


    public String getFullName() {
        return firstName + " " + lastName;
    }

    public Set<Partnership> getAllPartnerships() {
        Set<Partnership> allPartnerships = new HashSet<>();
        allPartnerships.addAll(initiatedPartnerships);
        allPartnerships.addAll(receivedPartnerships);
        return allPartnerships;
    }
}
