package org.harmoniapp.harmonidata.entities;

import lombok.*;
import org.harmoniapp.harmonidata.enums.Language;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserLanguageId implements Serializable {
    private Long user;
    private Language language;
}
