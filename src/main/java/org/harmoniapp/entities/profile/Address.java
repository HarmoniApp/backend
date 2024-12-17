package org.harmoniapp.entities.profile;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity
@Table(name = "address", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "zip_code", length = 10)
    @NotEmpty(message = "Zip code cannot be empty")
    @Size(min = 5, max = 10, message = "Zip code must be between 5 and 10 characters")
    @Pattern(regexp = "^[0-9\\-]+$", message = "Zip code must contain only digits and optional dashes")
    private String zipCode;

    @Column(length = 50)
    @NotEmpty(message = "City cannot be empty")
    @Size(max = 50, message = "City must be less than or equal to 50 characters")
    @Pattern(regexp = "^[a-zA-Z -]+$", message = "City must contain only letters, spaces and dashes")
    private String city;

    @Column(length = 100)
    @NotEmpty(message = "Street cannot be empty")
    @Size(max = 100, message = "Street must be less than or equal to 100 characters")
    @Pattern(regexp = "^[a-zA-Z ,-]+$", message = "Street must contain only letters, spaces, commas and dashes")
    private String street;

    @Column(name = "building_number", length = 10)
    @NotEmpty(message = "Building number cannot be empty")
    @Size(min = 1, max = 10, message = "Building number must be between 1 and 10 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Building number must contain only alphanumeric characters")
    private String buildingNumber;

    @Size(max = 10, message = "Apartment number must be less than or equal to 10 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Apartment number must contain only alphanumeric characters")
    private String apartment;

    @Column(name = "department_name", length = 100)
    @Size(max = 100, message = "Department name must be less than or equal to 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "Department name must contain only letters, numbers, and spaces")
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
