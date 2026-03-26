package cz.zcu.kiv.elexa.mnsck.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "GUIDE")
@Getter
@Setter
@NoArgsConstructor
public class Guide {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long guide_id;

    private String name;
    private String surname;

    @ManyToOne
    @JoinColumn(name = "language_id_1")
    private Language language1;

    @ManyToOne
    @JoinColumn(name = "language_id_2")
    private Language language2;
}