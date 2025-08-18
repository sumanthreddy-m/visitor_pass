package com.sumanth.visitor_pass_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sumanth.visitor_pass_management.dto.UpdatePassRequestDTO;
import com.sumanth.visitor_pass_management.entity.VisitorPassRequest;
import com.sumanth.visitor_pass_management.entity.VisitorType;
import com.sumanth.visitor_pass_management.enums.RequestStatus;
import com.sumanth.visitor_pass_management.repository.VisitorPassRequestRepository;
import com.sumanth.visitor_pass_management.repository.VisitorTypeRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class VisitorPassService {

    @Autowired
    private VisitorPassRequestRepository visitorPassRequestRepository;

    @Autowired
    private VisitorTypeRepository visitorTypeRepository;

    public List<VisitorPassRequest> getRequestsByLocation(String location) {
        return visitorPassRequestRepository.findByLocationAndRequestStatus(location, RequestStatus.approved);
    }

    public VisitorPassRequest addVisitorRequest(VisitorPassRequest visitorPassRequest) throws MaximumPassRequestLimitReachedException {
        // Fetch the VisitorType entity
        Optional<VisitorType> visitorTypeOptional = visitorTypeRepository.findById(visitorPassRequest.getVisitorType().getId());
        if (visitorTypeOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid visitor type ID provided.");
        }
        VisitorType visitorType = visitorTypeOptional.get();
        visitorPassRequest.setVisitorType(visitorType); // Set the entity in the request

        // Business Rule: Request must be raised 1 week in advance
        LocalDate visitLocalDate = visitorPassRequest.getVisitDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (visitLocalDate.isBefore(LocalDate.now().plusWeeks(1))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Visitor request must be raised at least one week in advance.");
        }

        // Business Rule: Family member requests only on weekends
        if (visitorPassRequest.getVisitorType().getType().matches("Parents|Child|Sibling|Spouse")) {
            LocalDate requestLocalDate = visitorPassRequest.getVisitDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (!(requestLocalDate.getDayOfWeek() == DayOfWeek.SATURDAY || requestLocalDate.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Family member visits can only be scheduled on weekends.");
            }
            // Business Rule: max 2 requests per month for family members
            Date today = new Date();
            int familyRequestsThisMonth = visitorPassRequestRepository.countFamilyMemberRequestsInMonth(visitorPassRequest.getRaisedByEmployee(), today);
            if (familyRequestsThisMonth >= 2) {
                throw new MaximumPassRequestLimitReachedException("Maximum Pass Request Limit Reached for family members for this month");
            }
        }

        visitorPassRequest.setRequestRaisedOn(new Date()); // Set request raised on current date
        visitorPassRequest.setRequestStatus(RequestStatus.pending);
        return visitorPassRequestRepository.save(visitorPassRequest);
    }

    public void updateVisitorRequest(VisitorPassRequest visitorPassRequest) {
        visitorPassRequestRepository.save(visitorPassRequest);
    }

    public Optional<VisitorPassRequest> getVisitorPassRequestById(int id) {
        Optional<VisitorPassRequest> visitorPassRequest = visitorPassRequestRepository.findById(id);
        if (visitorPassRequest.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found with id: " + id);
        }
        return visitorPassRequest;
    }

    public List<VisitorType> getAllVisitorTypes() {
        return visitorTypeRepository.findAll();
    }

    public void approveRejectRequest(UpdatePassRequestDTO updatePassRequestDTO) {
        Optional<VisitorPassRequest> optionalVisitorPassRequest = visitorPassRequestRepository.findById(updatePassRequestDTO.getRequestId());
        if (optionalVisitorPassRequest.isPresent()) {
            VisitorPassRequest visitorPassRequest = optionalVisitorPassRequest.get();
            visitorPassRequest.setRequestStatus(updatePassRequestDTO.getStatus());
            visitorPassRequest.setCancellationReason(updatePassRequestDTO.getReason());
            if (updatePassRequestDTO.getStatus().equals(RequestStatus.rejected) && (updatePassRequestDTO.getReason() == null || updatePassRequestDTO.getReason().isEmpty())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rejection reason cannot be empty");
            }
            visitorPassRequest.setRequestProcessedOn(new Date());
            visitorPassRequestRepository.save(visitorPassRequest);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found with id: " + updatePassRequestDTO.getRequestId());
        }
    }

    public boolean existsVisitorTypeById(int visitorTypeId) {
        return visitorTypeRepository.existsById(visitorTypeId);
    }

    public List<VisitorPassRequest> getRequestsByEmployee(String raisedByEmployee) {
        List<VisitorPassRequest> requests = visitorPassRequestRepository.findByRaisedByEmployee(raisedByEmployee);
        if (requests.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No requests found for employee: " + raisedByEmployee);
        }
        return requests;
    }
}
