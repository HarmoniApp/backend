package org.harmoniapp.harmonidata.entities;


import lombok.Data;
import org.harmoniapp.harmonidata.enums.Language;

import java.io.Serializable;

@Data
public class UserLanguageId implements Serializable {
    private User user;
    private Language language;
}
