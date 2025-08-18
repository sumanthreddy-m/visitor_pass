package com.sumanth.visitor_pass_management.dto;

import com.sumanth.visitor_pass_management.enums.RequestStatus;

import lombok.Data;

@Data
public class UpdatePassRequestDTO {
    private int requestId;
    private RequestStatus status;
    private String reason;
}