package org.harmoniapp.harmonidata.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.harmoniapp.harmonidata.enums.ContractType;
import org.hibernate.proxy.HibernateProxy;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "\"user\"")
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

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_type")
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserLanguage> languages;

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
