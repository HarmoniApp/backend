package org.harmoniapp.entities.profile;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity
@Table(name = "contract_type", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContractType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotBlank(message = "Nazwa umowy nie może być pusta")
    @Pattern(regexp = "^[A-Za-zĀ-ɏØ-öø-ÿ'\\-\\s]+$", message = "Nazwa umowy może zawierać tylko litery, spacje, myślniki i apostrofy")
    private String name;

    @Column(name = "absence_days")
    @ColumnDefault("0")
    @PositiveOrZero(message = "Liczba dni nieobecności musi być nieujemna")
    private Integer absenceDays;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ?
                ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ?
                ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        ContractType contractType = (ContractType) o;
        return getId() != null && Objects.equals(getId(), contractType.getId());
    }


    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
