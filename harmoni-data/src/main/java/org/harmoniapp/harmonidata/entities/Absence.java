package org.harmoniapp.harmonidata.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "absence", schema = "public",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"start", "end", "user_id", "absence_type_id", "submission"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Absence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "\"start\"")
    @NotNull(message = "Start date cannot be null")
    private LocalDate start;

    @Column(name = "\"end\"")
    @NotNull(message = "End date cannot be null")
    private LocalDate end;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull(message = "User cannot be null")
    private User user;

    @ManyToOne
    @JoinColumn(name = "absence_type_id")
    @NotNull(message = "Absence type cannot be null")
    private AbsenceType absenceType;

    @ManyToOne
    @JoinColumn(name = "status_id")
    @NotNull(message = "Status cannot be null")
    private Status status;

    @Column(updatable = false)
    @NotNull(message = "Submission date cannot be null")
    @PastOrPresent(message = "Submission date must be in the past or present")
    private LocalDate submission;

    @NotNull(message = "Updated date cannot be null")
    @PastOrPresent(message = "Updated date must be in the past or present")
    private LocalDate updated;

    @Column(name = "working_days")
    @NotNull(message = "Working days cannot be null")
    @PositiveOrZero(message = "Working days must be a positive number or zero")
    private Long workingDays;

    @NotNull(message = "Archived cannot be null")
    @ColumnDefault("false")
    private Boolean archived;

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
