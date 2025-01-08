package org.harmoniapp.entities.absence;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.harmoniapp.entities.user.User;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "absence", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Absence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "\"start\"")
    @NotNull(message = "Data początkowa nie może być pusta")
    private LocalDate start;

    @Column(name = "\"end\"")
    @NotNull(message = "Data końcowa nie może być pusta")
    private LocalDate end;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull(message = "Użytkownik nie może być pusty")
    private User user;

    @ManyToOne
    @JoinColumn(name = "absence_type_id")
    @NotNull(message = "Typ nieobecności nie może być pusty")
    private AbsenceType absenceType;

    @ManyToOne
    @JoinColumn(name = "status_id")
    @NotNull(message = "Status nie może być pusty")
    private Status status;

    @Column(updatable = false)
    @NotNull(message = "Data złożenia nie może być pusta")
    @PastOrPresent(message = "Data złożenia musi być w przeszłości lub teraźniejszości")
    private LocalDate submission;

    @NotNull(message = "Data aktualizacji nie może być pusta")
    @PastOrPresent(message = "Data aktualizacji musi być w przeszłości lub teraźniejszości")
    @UpdateTimestamp
    private LocalDate updated;

    @Column(name = "working_days")
    @NotNull(message = "Licza dni roboczych nie może być pusta")
    @PositiveOrZero(message = "Liczba dni roboczych musi być większa lub równa 0")
    private Long workingDays;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ?
                ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ?
                ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Absence absence = (Absence) o;
        return getId() != null && Objects.equals(getId(), absence.getId());
    }


    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
