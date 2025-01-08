package org.harmoniapp.entities.profile;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity
@Table(name = "address", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "zip_code", length = 10)
    @NotEmpty(message = "Kod pocztowy nie może być pusty")
    @Size(min = 5, max = 10, message = "Kod pocztowy musi mieć od 5 do 10 znaków")
    @Pattern(regexp = "^[0-9\\-]+$", message = "Kod pocztowy musi zawierać tylko cyfry i myślniki")
    private String zipCode;

    @Column(length = 50)
    @NotEmpty(message = "Miasto nie może być puste")
    @Size(max = 50, message = "Miasto musi mieć mniej niż 50 znaków")
    @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ'\\-\\s]+$", message = "Miasto musi zawierać tylko litery, spacje, myślniki i apostrofy")
    private String city;

    @Column(length = 100)
    @NotEmpty(message = "Ulica nie może być pusta")
    @Size(max = 100, message = "Ulica musi mieć mniej niż 100 znaków")
    @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ',\\-\\s]+$", message = "Ulica musi zawierać tylko litery, spacje, myślniki, przecinki i apostrofy")
    private String street;

    @Column(name = "building_number", length = 10)
    @NotEmpty(message = "Numer budynku nie może być pusty")
    @Size(min = 1, max = 10, message = "Numer budynku musi mieć od 1 do 10 znaków")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Numer budynku musi zawierać tylko litery i cyfry")
    private String buildingNumber;

    @Size(max = 10, message = "Numer mieszkania musi mieć mniej niż 10 znaków")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Numer mieszkania musi zawierać tylko litery i cyfry")
    private String apartment;

    @Column(name = "department_name", length = 100)
    @Size(max = 100, message = "Nazwa oddziału musi mieć mniej niż 100 znaków")
    @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ'\\-\\s]+$", message = "Nazwa oddziału musi zawierać tylko litery, spacje, myślniki i apostrofy")
    private String departmentName;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ?
                ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ?
                ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Address address = (Address) o;
        return getId() != null && Objects.equals(getId(), address.getId());
    }


    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
