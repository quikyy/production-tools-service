package com.productiontools.entities.budget;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table( name = "budgetRates" )
public class BudgetRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column
    public int month;

    @Column
    public int year;

    @Column
    public float usd;

    @Column
    public float eur;

    @Column
    public float gbp;

    @Column
    public float cad;
}
