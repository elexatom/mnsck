package cz.zcu.kiv.elexa.mnsck.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Staff extends AppUser {

    private String role;
    private String office_number;
}
