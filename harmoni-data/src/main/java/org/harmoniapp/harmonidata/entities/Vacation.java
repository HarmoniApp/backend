package org.harmoniapp.harmonidata.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity
@Table(name = "vacation", schema = "public",
        uniqueConstraints = @UniqueConstraint(columnNames = {"absence_type", "contract_type"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vacation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "absence_type")
    @NotNull(message = "Absence type is required")
    private AbsenceType absenceType;

    @ManyToOne
    @JoinColumn(name = "contract_type")
    @NotNull(message = "Contract type is required")
    private ContractType contractType;

    @Column(name = "max_available")
    @NotNull(message = "Max available is required")
    @Positive(message = "Max available must be a positive number")
    private Integer maxAvailable;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ?
                ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ?
                ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Vacation vacation = (Vacation) o;
        return getId() != null && Objects.equals(getId(), vacation.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}