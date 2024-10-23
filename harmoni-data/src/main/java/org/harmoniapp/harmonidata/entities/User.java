package org.harmoniapp.harmonidata.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "user", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstname;
    private String surname;
    private String email; //TODO: Later add some validation
    private String password;

    @ManyToOne
    @JoinColumn
    private ContractType contractType;

    @Column(name="contract_signature")
    private LocalDate contractSignature;

    @Column(name = "contract_expiration")
    private LocalDate contractExpiration;

    @ManyToOne
    @JoinColumn
    private Address residence;

    @ManyToOne
    @JoinColumn
    private Address workAddress;

    @ManyToOne
    @JoinColumn
    private User supervisor;

    @Column(name="phone_number")
    private String phoneNumber;

    @Column(name = "employee_id")
    private String employeeId;

    private String photo;

    @Column(name = "last_password_change")
    private LocalDateTime lastPasswordChange;

    @Column(name = "is_password_generated")
    private boolean isPasswordGenerated;

    @Column(name = "is_active")
    private boolean isActive;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @ManyToMany
    @JoinTable(
            name = "user_language",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "language_id"))
    private Set<Language> languages;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ?
                ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ?
                ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
