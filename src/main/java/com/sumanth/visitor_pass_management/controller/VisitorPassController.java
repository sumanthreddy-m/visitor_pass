package com.sumanth.visitor_pass_management.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.sumanth.visitor_pass_management.dto.UpdatePassRequestDTO;
import com.sumanth.visitor_pass_management.dto.VisitorPassRequestDTO;
import com.sumanth.visitor_pass_management.dto.VisitorTypeDTO;
import com.sumanth.visitor_pass_management.entity.VisitorPassRequest;
import com.sumanth.visitor_pass_management.entity.VisitorType;
import com.sumanth.visitor_pass_management.entity.VisitorsIDProofs;
import com.sumanth.visitor_pass_management.service.MaximumPassRequestLimitReachedException;
import com.sumanth.visitor_pass_management.service.VisitorPassService;
import com.sumanth.visitor_pass_management.util.FileUploadUtil;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/passrequests")
@SecurityRequirement(name = "Bearer Authentication")
public class VisitorPassController {

    @Autowired
    private VisitorPassService visitorPassService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/{location}")
    @PreAuthorize("hasAuthority('SECURITY HEAD')")
    public ResponseEntity<List<VisitorPassRequestDTO>> getRequestsByLocation(@PathVariable String location, Authentication authentication) {
        List<VisitorPassRequest> requests = visitorPassService.getRequestsByLocation(location);
        List<VisitorPassRequestDTO> requestDTOs = requests.stream()
                .map(request -> modelMapper.map(request, VisitorPassRequestDTO.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(requestDTOs, HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<?> createVisitorPass(
            @RequestParam("raisedByEmployee") String raisedByEmployee,
            @RequestParam("visitorTypeId") int visitorTypeId,
            @RequestParam("purposeOfVisit") String purposeOfVisit,
            @RequestParam("visitDate") String visitDateStr,
            @RequestParam("location") String location,
            @RequestParam("visitorName") String visitorName,
            @RequestParam("visitorAge") int visitorAge,
            @RequestParam("comingFrom") String comingFrom,
            @RequestParam(value = "idProofs", required = false) MultipartFile[] idProofs,
            @RequestParam(value = "IDProofNo", required = false) String[] idProofNumbers,
            @RequestParam(value = "IDProofType", required = false) String[] idProofTypes
    ) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date visitDate = formatter.parse(visitDateStr);

            VisitorPassRequest visitorPassRequest = new VisitorPassRequest();
            visitorPassRequest.setRaisedByEmployee(raisedByEmployee);
            visitorPassRequest.setVisitorType(new com.sumanth.visitor_pass_management.entity.VisitorType(visitorTypeId));
            visitorPassRequest.setPurposeOfVisit(purposeOfVisit);
            visitorPassRequest.setVisitDate(visitDate);
            visitorPassRequest.setLocation(location);
            visitorPassRequest.setVisitorName(visitorName);
            visitorPassRequest.setVisitorAge(visitorAge);
            visitorPassRequest.setComingFrom(comingFrom);

            List<VisitorsIDProofs> visitorIDProofsList = new ArrayList<>();
            if (idProofs != null && idProofs.length > 0) {
                for (int i = 0; i < idProofs.length; i++) {
                    if (idProofs[i] != null && !idProofs[i].isEmpty() && idProofNumbers != null && idProofTypes != null && i < idProofNumbers.length && i < idProofTypes.length && idProofNumbers[i] != null && idProofTypes[i] != null) {
                        String fileName = FileUploadUtil.saveFile("id-proofs/", idProofNumbers[i], idProofs[i]);
                        VisitorsIDProofs idProof = new VisitorsIDProofs();
                        idProof.setIDProofURL("id-proofs/" + fileName);
                        idProof.setIDProofNo(idProofNumbers[i]);
                        idProof.setIDProofType(idProofTypes[i]);
                        visitorIDProofsList.add(idProof);
                    }
                }
                visitorPassRequest.setIdProofs(visitorIDProofsList);
            }

            VisitorPassRequest createdRequest = visitorPassService.addVisitorRequest(visitorPassRequest);
            VisitorPassRequestDTO createdRequestDTO = modelMapper.map(createdRequest, VisitorPassRequestDTO.class);
            return new ResponseEntity<>(createdRequestDTO, HttpStatus.CREATED);

        } catch (IllegalArgumentException iae) {
            return new ResponseEntity<>(iae.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (org.springframework.dao.DataIntegrityViolationException dive) {
            return new ResponseEntity<>("Foreign key constraint violation. Please ensure visitor type ID exists.", HttpStatus.BAD_REQUEST);
        } catch (MaximumPassRequestLimitReachedException mprle) {
            return new ResponseEntity<>(mprle.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IOException ioe) {
            return new ResponseEntity<>("Error uploading file: " + ioe.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ParseException pe) {
            return new ResponseEntity<>("Invalid date format", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating visitor pass: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/visitortypes")
    public ResponseEntity<List<VisitorTypeDTO>> getVisitorTypes(Authentication authentication) {
        List<VisitorType> visitorTypes = visitorPassService.getAllVisitorTypes();
        List<VisitorTypeDTO> visitorTypeDTOS = visitorTypes.stream().map(visitorType -> modelMapper.map(visitorType,VisitorTypeDTO.class)).collect(Collectors.toList());
        return new ResponseEntity<>(visitorTypeDTOS, HttpStatus.OK);
    }

    @PostMapping("/approvereject")
    @PreAuthorize("hasAuthority('SECURITY HEAD')")
    public ResponseEntity<Void> approveRejectRequest(@RequestBody UpdatePassRequestDTO updatePassRequestDTO, Authentication authentication) {
        try {
            visitorPassService.approveRejectRequest(updatePassRequestDTO);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException iae){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, iae.getMessage(), iae);
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing request approval/rejection", e);
        }
    }

    @GetMapping("/view/{requestid}")
    public ResponseEntity<VisitorPassRequestDTO> getVisitorPassById(@PathVariable int requestid, Authentication authentication) {
        try {
            VisitorPassRequest visitorPassRequest = visitorPassService.getVisitorPassRequestById(requestid).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Request not found"));
            VisitorPassRequestDTO visitorPassRequestDTO = modelMapper.map(visitorPassRequest,VisitorPassRequestDTO.class);
            return new ResponseEntity<>(visitorPassRequestDTO, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching request by ID", e);
        }
    }
    @GetMapping("/byEmployee/{employeeName}")
    public ResponseEntity<List<VisitorPassRequestDTO>> getRequestsByEmployee(@PathVariable String employeeName, Authentication authentication){
        List<VisitorPassRequest> visitorPassRequests = visitorPassService.getRequestsByEmployee(employeeName);
        List<VisitorPassRequestDTO> visitorPassRequestDTOS = visitorPassRequests.stream().map(visitorPassRequest -> modelMapper.map(visitorPassRequest,VisitorPassRequestDTO.class)).collect(Collectors.toList());
        return new ResponseEntity<>(visitorPassRequestDTOS,HttpStatus.OK);
    }
}