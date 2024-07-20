package org.harmoniapp.harmonidata.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "user")
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
    @JoinColumn(name = "contract_type_id")
    private ContractType contractType;

    @Temporal(TemporalType.DATE)
    @Column(name="contract_signature")
    private Date contractSignature;

    @Temporal(TemporalType.DATE)
    @Column(name = "contract_expiration")
    private Date contractExpiration;

    @ManyToOne
    @JoinColumn(name="residence_id")
    private Address residence;

    @ManyToOne
    @JoinColumn(name = "work_address_id")
    private Address workAddress;

    @ManyToOne
    @JoinColumn(name = "supervisor_id")
    private User supervisor;

    @Column(name="phone_number")
    private String phoneNumber;

    @Column(name = "employee_id")
    private String employeeId;

    @ManyToMany
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
