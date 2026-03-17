package cz.zcu.kiv.elexa.mnsck.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "TOURS")
@Getter
@Setter
@NoArgsConstructor
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tour_id;

    private String tour_name;
    private LocalDate start_date;
    private LocalDate end_date;
    private Double price_person;
    private Double price_person_reduced;
    private Integer capacity;

    @ManyToOne
    @JoinColumn(name = "transport_id")
    private Transport transport;

    @ManyToOne
    @JoinColumn(name = "destination_id")
    private Destination destination;

    @ManyToOne
    @JoinColumn(name = "guide_id")
    private Guide guide;
}
