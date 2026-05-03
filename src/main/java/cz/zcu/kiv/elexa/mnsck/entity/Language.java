package cz.zcu.kiv.elexa.mnsck.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

/**
 * Entita reprezentující jazyk (Language) v systému.
 * 
 * @author Tomáš Elexa
 */
@Entity
@Table(name = "LANGUAGE")
@Getter
@Setter
@NoArgsConstructor
public class Language {
    /**
     * ID jazyka, generované automaticky a slouží jako primární klíč v databázi.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long language_id;

    /**
     * Název jazyka.
     */
    @NonNull
    public String lang_name;
}