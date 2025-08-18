package com.sumanth.visitor_pass_management.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "VisitorTypes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitorType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 50)
    private String type;

    @JsonCreator
    public VisitorType(int id) {
        this.id = id;
    }
}