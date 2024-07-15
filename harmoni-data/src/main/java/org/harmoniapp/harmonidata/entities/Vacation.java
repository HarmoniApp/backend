package org.harmoniapp.harmonidata.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.harmoniapp.harmonidata.enums.AbsenceType;
import org.harmoniapp.harmonidata.enums.ContractType;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity
@Table(name = "vacation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vacation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "absence_type")
    private AbsenceType absenceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_type")
    private ContractType contractType;

    @Column(name = "max_available")
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