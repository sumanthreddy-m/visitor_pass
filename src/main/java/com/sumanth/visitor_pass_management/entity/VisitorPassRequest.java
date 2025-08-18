 package com.sumanth.visitor_pass_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

import com.sumanth.visitor_pass_management.enums.RequestStatus;

@Entity
@Table(name = "VisitorPassRequests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitorPassRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 100)
    private String raisedByEmployee;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date requestRaisedOn;

    @ManyToOne
    @JoinColumn(name = "visitorTypeID")
    private VisitorType visitorType;

    @Column(nullable = false, length = 255)
    private String purposeOfVisit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus requestStatus;

    @Column(length = 100)
    private String requestProcessedByEmployee;

    @Temporal(TemporalType.TIMESTAMP)
    private Date requestProcessedOn;

    @Column
    private Date visitDate;

    @Column(length = 255)
    private String cancellationReason;

    @Column(length = 255)
    private String location;

    @Column(length = 100)
    private String visitorName;

    @Column
    private int visitorAge;

    @Column(length = 255)
    private String comingFrom;

    @OneToMany(mappedBy = "visitorPassRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VisitorsIDProofs> idProofs;
}