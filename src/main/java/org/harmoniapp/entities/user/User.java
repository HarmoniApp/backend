package org.harmoniapp.entities.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.harmoniapp.entities.profile.Address;
import org.harmoniapp.entities.profile.ContractType;
import org.harmoniapp.entities.profile.Language;
import org.harmoniapp.entities.profile.Role;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.validator.constraints.UniqueElements;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "user", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Username is required")
    private String firstname;

    @NotEmpty(message = "Surname is required")
    private String surname;

    @Column(unique = true)
    @NotEmpty(message = "E-mail is required")
    @Email(message = "E-mail is invalid")
    private String email;

    @Column(length = 68)
    @NotEmpty(message = "Password is required")
    @Pattern(regexp = "^(\\{bcrypt})?\\$2[aby]\\$\\d*\\$[a-zA-Z0-9/.]{53}$",
            message = "Password must be a valid hash")
    private String password;

    @ManyToOne
    @JoinColumn
    private ContractType contractType;

    @Column(name = "contract_signature")
    private LocalDate contractSignature;

    @Column(name = "contract_expiration")
    private LocalDate contractExpiration;

    @ManyToOne
    @JoinColumn
    @NotNull(message = "Residence cannot be null")
    private Address residence;

    @ManyToOne
    @JoinColumn
    private Address workAddress;

    @ManyToOne
    @JoinColumn
    private User supervisor;

    @Column(name = "phone_number", unique = true)
    @NotEmpty(message = "Phone number is required")
    private String phoneNumber;

    @Column(name = "employee_id", length = 20, unique = true)
    @NotEmpty(message = "Employee ID is required")
    @Size(max = 20, message = "Employee ID must be less than or equal to 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "Employee ID must contain only letters, numbers, and dashes")
    private String employeeId;

    @ColumnDefault("default.jpg")
    @NotNull(message = "Photo cannot be null")
    @Pattern(regexp = "^[^<>:\"/\\\\|?*]+(\\\\.[A-Za-z0-9]{1,5})?$", message = "Photo must be a valid file name")
    private String photo;

    @Column(name = "failed_login_attempts")
    @ColumnDefault("0")
    @NotNull(message = "Failed login attempts cannot be null")
    @PositiveOrZero(message = "Failed login attempts must be zero or a positive number")
    private Integer failedLoginAttempts;

    @Column(name = "password_expiration_date")
    @ColumnDefault("CURRENT_DATE - INTERVAL '1 day'")
    @NotNull(message = "Password expiration date cannot be null")
    private LocalDate passwordExpirationDate;

    @Column(name = "is_active")
    @ColumnDefault("true")
    @NotNull(message = "Is active cannot be null")
    private Boolean isActive;

    @Column(name = "available_absence_days")
    @NotNull(message = "Available absence days cannot be null")
    @PositiveOrZero(message = "Available absence days must be zero or a positive number")
    private Integer availableAbsenceDays;

    @Column(name = "unused_absence_days")
    @ColumnDefault("0")
    @NotNull(message = "Unused absence days cannot be null")
    @PositiveOrZero(message = "Unused absence days must be zero or a positive number")
    private Integer unusedAbsenceDays;

    @Column(name = "unused_absence_expiration")
    private LocalDate unusedAbsenceExpiration;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @UniqueElements(message = "Roles must be unique")
    private Set<Role> roles;

    @ManyToMany
    @JoinTable(
            name = "user_language",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "language_id"))
    @UniqueElements(message = "Languages must be unique")
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
