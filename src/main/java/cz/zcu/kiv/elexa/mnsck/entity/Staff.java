package cz.zcu.kiv.elexa.mnsck.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entita reprezentující zaměstnance (Staff) v systému. Zaměstnanec je specifickým typem uživatele (AppUser) a obsahuje informace o své roli a kanceláři.
 *
 * @author Tomáš Elexa
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Staff extends AppUser {

    /**
     * Role zaměstnance.
     */
    private String role;

    /**
     * Číslo kanceláře zaměstnance.
     */
    private String office_number;
}
