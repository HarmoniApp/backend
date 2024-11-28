package org.harmoniapp.harmonidata.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.proxy.HibernateProxy;

import java.sql.Types;
import java.util.Objects;

@Entity
@Table(name = "archived_shifts", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArchivedShift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_title")
    @NotEmpty(message = "File title cannot be empty")
    private String fileTitle;

    @JdbcTypeCode(Types.BINARY)
    @Column(name = "pdf_data")
    @NotNull(message = "PDF data cannot be null")
    private byte[] pdfData;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ?
                ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ?
                ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        ArchivedShift archivedShift = (ArchivedShift) o;
        return getId() != null && Objects.equals(getId(), archivedShift.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
