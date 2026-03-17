package cz.zcu.kiv.elexa.mnsck.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name = "LANGUAGE")
@Getter
@Setter
@NoArgsConstructor
public class Language {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long language_id;

    @NonNull
    public String lang_name;
}