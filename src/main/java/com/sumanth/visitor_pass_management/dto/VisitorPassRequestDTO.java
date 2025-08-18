package com.sumanth.visitor_pass_management.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

import com.sumanth.visitor_pass_management.enums.RequestStatus;

@Data
public class VisitorPassRequestDTO {
    private int id;
    private String raisedByEmployee;
    private Date requestRaisedOn;
    private int visitorTypeId;
    private String purposeOfVisit;
    private RequestStatus requestStatus;
    private String requestProcessedByEmployee;
    private Date requestProcessedOn;
    private Date visitDate;
    private String cancellationReason;
    private String location;
    private String visitorName;
    private int visitorAge;
    private String comingFrom;
    private List<VisitorsIDProofsDTO> idProofs;
}