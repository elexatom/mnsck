package cz.zcu.kiv.elexa.mnsck.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entita reprezentující zákazníka. Zákazník je specifickým typem uživatele (AppUser). 
 * 
 * @author Tomáš Elexa
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Customer extends AppUser {

    /**
     * Telefonní číslo zákazníka.
     */
    private String phone_number;

    /**
     * Číslo bankovního účtu zákazníka.
     */
    private String bank_account_number;

    /**
     * Indikátor, zda je zákazník VIP zákazníkem.
     */
    private Boolean is_vip;
}
