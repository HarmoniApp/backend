package org.harmoniapp.harmonidata.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import java.sql.Types;

@Entity
@Table(name = "archived_shifts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArchivedShift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_title", nullable = false)
    private String fileTitle;

    @JdbcTypeCode(Types.BINARY)
    @Column(name = "pdf_data")
    private byte[] pdfData;
}
