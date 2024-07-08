package org.harmoniapp.harmonidata.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "\"predefine_shift\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PredefineShift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Temporal(TemporalType.TIME)
    private LocalTime start;

    @Temporal(TemporalType.TIME)
    @Column(name = "\"end\"")
    private LocalTime end;
}

