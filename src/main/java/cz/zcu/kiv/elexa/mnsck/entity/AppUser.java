package cz.zcu.kiv.elexa.mnsck.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entita pro uživatele aplikace. Slouží jako základ pro různé typy uživatelů (např. zákazníci, zaměstnanci) díky dědičnosti.
 * Obsahuje základní informace o uživateli, jako jsou jméno, příjmení, email a heslo (uložené jako hash). Email je unikátní a nesmí být null. Dědičnost je nastavena na JOINED, což znamená, že každá podtřída bude mít svou vlastní tabulku, která bude spojena s tabulkou AppUser pomocí cizího klíče.
 *
 * @author Tomáš Elexa
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter @NoArgsConstructor
public class AppUser {

    /**
     * ID uživatele, generované automaticky. Slouží jako primární klíč v databázi.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Jméno uživatele.
     */
    private String name;
    /**
     * Příjmení uživatele.
     */
    private String surname;

    /**
     * Email uživatele, který musí být unikátní a nesmí být null. Slouží jako identifikátor pro přihlášení a komunikaci s uživatelem.
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Hashované heslo uživatele.
     */
    private String password; // hashed
}
