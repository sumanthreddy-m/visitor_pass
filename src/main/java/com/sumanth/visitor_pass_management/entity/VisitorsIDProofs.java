package com.sumanth.visitor_pass_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "VisitorsIDProofs")
@Getter
@Setter
public class VisitorsIDProofs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 100)
    private String IDProofType;

    @Column(length = 100)
    private String IDProofNo;

    @Column(length = 255)
    private String IDProofURL;

    @ManyToOne
    @JoinColumn(name = "RequestID")
    private VisitorPassRequest visitorPassRequest;
}