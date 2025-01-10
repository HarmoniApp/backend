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

    @NotBlank(message = "Imię nie może być puste")
    private String firstname;

    @NotBlank(message = "Nazwisko nie może być puste")
    private String surname;

    @Column(unique = true)
    @NotBlank(message = "E-mail jest wymagany")
    @Email(message = "E-mail jest niepoprawny")
    private String email;

    @Column(length = 68)
    @NotBlank(message = "Hasło jest wymagane")
    @Pattern(regexp = "^(\\{bcrypt})?\\$2[aby]\\$\\d*\\$[a-zA-Z0-9/.]{53}$",
            message = "Hasło musi być zaszyfrowane algorytmem bcrypt")
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
    @NotNull(message = "Adres zamieszkania nie może być pusty")
    private Address residence;

    @ManyToOne
    @JoinColumn
    private Address workAddress;

    @ManyToOne
    @JoinColumn
    private User supervisor;

    @Column(name = "phone_number")
    @NotBlank(message = "Numer telefonu jest wymagany")
    private String phoneNumber;

    @Column(name = "employee_id", length = 20, unique = true)
    @NotEmpty(message = "ID pracownika nie może być puste")
    @Size(max = 20, message = "ID pracownika musi mieć mniej niż 20 znaków")
    @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "ID pracownika musi zawierać tylko litery, cyfry i myślniki")
    private String employeeId;

    @ColumnDefault("default.jpg")
    @NotBlank(message = "Zdjęcie nie może być puste")
    @Pattern(regexp = "^[^<>:\"/\\\\|?*]+(\\\\.[A-Za-z0-9]{1,5})?$", message = "Zdjęcie musi być plikiem graficznym")
    private String photo;

    @Column(name = "failed_login_attempts")
    @ColumnDefault("0")
    @NotNull(message = "Liczba nieudanych prób logowania nie może być pusta")
    @PositiveOrZero(message = "Liczba nieudanych prób logowania musi być nieujemna")
    private Integer failedLoginAttempts;

    @Column(name = "password_expiration_date")
    @ColumnDefault("CURRENT_DATE - INTERVAL '1 day'")
    @NotNull(message = "Wygaśnięcie hasła nie może być puste")
    private LocalDate passwordExpirationDate;

    @Column(name = "is_active")
    @ColumnDefault("true")
    @NotNull(message = "Status konta nie może być pusty")
    private Boolean isActive;

    @Column(name = "available_absence_days")
    @NotNull(message = "Liczba dostępnych dni nieobecności nie może być pusta")
    @PositiveOrZero(message = "Liczba dostępnych dni nieobecności musi być nieujemna")
    private Integer availableAbsenceDays;

    @Column(name = "unused_absence_days")
    @ColumnDefault("0")
    @NotNull(message = "Liczba niewykorzystanych dni nieobecności nie może być pusta")
    @PositiveOrZero(message = "Licba niewykorzystanych dni nieobecności musi być nieujemna")
    private Integer unusedAbsenceDays;

    @Column(name = "unused_absence_expiration")
    private LocalDate unusedAbsenceExpiration;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @UniqueElements(message = "Role muszą być unikalne")
    private Set<Role> roles;

    @ManyToMany
    @JoinTable(
            name = "user_language",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "language_id"))
    @UniqueElements(message = "Języki muszą być unikalne")
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
