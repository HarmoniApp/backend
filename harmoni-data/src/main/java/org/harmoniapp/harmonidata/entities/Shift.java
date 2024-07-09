package org.harmoniapp.harmonidata.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.harmoniapp.harmonidata.enums.Role;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "\"shift\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime start;

    @Column(name = "\"end\"", columnDefinition = "TIMESTAMP")
    private LocalDateTime end;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private Role role;
}
