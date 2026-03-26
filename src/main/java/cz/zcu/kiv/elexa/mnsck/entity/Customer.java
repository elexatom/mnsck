package cz.zcu.kiv.elexa.mnsck.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Customer extends AppUser {

    private String phone_number;
    private String bank_account_number;
    private Boolean is_vip;
}
