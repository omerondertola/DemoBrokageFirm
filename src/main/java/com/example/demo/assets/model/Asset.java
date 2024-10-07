package com.example.demo.assets.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Entity
@Table(name = "assets",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = { "customerId", "assetName" })
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    @PositiveOrZero
    private long customerId;

    @NotNull
    private String assetName;

    @PositiveOrZero
    private double size;

    @PositiveOrZero
    private double usableSize;

}
