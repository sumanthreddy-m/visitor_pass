package com.cognizant.visitor_pass_management.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import com.sumanth.visitor_pass_management.controller.VisitorPassController;
import com.sumanth.visitor_pass_management.dto.UpdatePassRequestDTO;
import com.sumanth.visitor_pass_management.dto.VisitorPassRequestDTO;
import com.sumanth.visitor_pass_management.dto.VisitorTypeDTO;
import com.sumanth.visitor_pass_management.entity.VisitorPassRequest;
import com.sumanth.visitor_pass_management.entity.VisitorType;
import com.sumanth.visitor_pass_management.enums.RequestStatus;
import com.sumanth.visitor_pass_management.service.VisitorPassService;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VisitorPassControllerTest {

    @InjectMocks
    private VisitorPassController visitorPassController;

    @Mock
    private VisitorPassService visitorPassService;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void testGetRequestsByLocation() {
        // Arrange
        String location = "Test Location";
        Authentication authentication = mock(Authentication.class);
        VisitorPassRequest visitorPassRequest = new VisitorPassRequest();
        VisitorPassRequestDTO visitorPassRequestDTO = new VisitorPassRequestDTO();

        List<VisitorPassRequest> requests = List.of(visitorPassRequest);
        List<VisitorPassRequestDTO> requestDTOs = List.of(visitorPassRequestDTO);

        when(visitorPassService.getRequestsByLocation(location)).thenReturn(requests);
        when(modelMapper.map(visitorPassRequest, VisitorPassRequestDTO.class)).thenReturn(visitorPassRequestDTO);

        // Act
        ResponseEntity<List<VisitorPassRequestDTO>> response = visitorPassController.getRequestsByLocation(location, authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(requestDTOs, response.getBody());
    }

    @Test
    void testGetVisitorTypes() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        VisitorType visitorType = new VisitorType(1, "Type1");
        VisitorTypeDTO visitorTypeDTO = new VisitorTypeDTO();
        visitorTypeDTO.setId(1);
        visitorTypeDTO.setType("Type1");

        List<VisitorType> visitorTypes = List.of(visitorType);
        List<VisitorTypeDTO> visitorTypeDTOS = List.of(visitorTypeDTO);

        when(visitorPassService.getAllVisitorTypes()).thenReturn(visitorTypes);
        when(modelMapper.map(visitorType, VisitorTypeDTO.class)).thenReturn(visitorTypeDTO);

        // Act
        ResponseEntity<List<VisitorTypeDTO>> response = visitorPassController.getVisitorTypes(authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(visitorTypeDTOS, response.getBody());
    }

    @Test
    void testCreateVisitorPass() throws Exception {
        // Arrange
        String raisedByEmployee = "testEmployee";
        int visitorTypeId = 1;
        String purposeOfVisit = "Test Visit";
        String visitDateStr = "2024-04-26T10:00:00.000Z"; // ISO 8601 format
        String location = "Test Location";
        String visitorName = "Test Visitor";
        int visitorAge = 30;
        String comingFrom = "Test City";

        // Mock MultipartFile
        MultipartFile[] idProofs = new MultipartFile[]{
                new MockMultipartFile("idProof1.jpg", "idProof1.jpg", "image/jpeg", "some-image".getBytes()),
                new MockMultipartFile("idProof2.pdf", "idProof2.pdf", "application/pdf", "some-pdf".getBytes())
        };
        String[] idProofNumbers = new String[]{"ID12345", "ID67890"};
        String[] idProofTypes = new String[]{"Passport", "Driver's License"};

        VisitorType visitorType = new VisitorType(visitorTypeId);
        VisitorPassRequest visitorPassRequest = new VisitorPassRequest();
        visitorPassRequest.setRaisedByEmployee(raisedByEmployee);
        visitorPassRequest.setVisitorType(visitorType);
        visitorPassRequest.setPurposeOfVisit(purposeOfVisit);
        visitorPassRequest.setVisitDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(visitDateStr));
        visitorPassRequest.setLocation(location);
        visitorPassRequest.setVisitorName(visitorName);
        visitorPassRequest.setVisitorAge(visitorAge);
        visitorPassRequest.setComingFrom(comingFrom);

        VisitorPassRequestDTO visitorPassRequestDTO = new VisitorPassRequestDTO();
        visitorPassRequestDTO.setRaisedByEmployee(raisedByEmployee);
        visitorPassRequestDTO.setVisitorTypeId(visitorTypeId);
        visitorPassRequestDTO.setPurposeOfVisit(purposeOfVisit);
        visitorPassRequestDTO.setVisitDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(visitDateStr));
        visitorPassRequestDTO.setLocation(location);
        visitorPassRequestDTO.setVisitorName(visitorName);
        visitorPassRequestDTO.setVisitorAge(visitorAge);
        visitorPassRequestDTO.setComingFrom(comingFrom);

        when(modelMapper.map(any(VisitorPassRequest.class), eq(VisitorPassRequestDTO.class))).thenReturn(visitorPassRequestDTO);
        when(visitorPassService.addVisitorRequest(any(VisitorPassRequest.class))).thenReturn(visitorPassRequest);

        // Act
        ResponseEntity<?> response = visitorPassController.createVisitorPass(
                raisedByEmployee, visitorTypeId, purposeOfVisit, visitDateStr, location, visitorName, visitorAge, comingFrom, idProofs, idProofNumbers, idProofTypes
        );

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(visitorPassRequestDTO, response.getBody());
    }
    
    
    @Test
    void testApproveRejectRequest() {
        // Arrange
        int requestId = 1;
        RequestStatus status = RequestStatus.approved;
        String reason = "Approved";
        Authentication authentication = mock(Authentication.class); // Mock Authentication

        UpdatePassRequestDTO updatePassRequestDTO = new UpdatePassRequestDTO();
        updatePassRequestDTO.setRequestId(requestId);
        updatePassRequestDTO.setStatus(status);
        updatePassRequestDTO.setReason(reason);

        // Act
        ResponseEntity<Void> response = visitorPassController.approveRejectRequest(updatePassRequestDTO, authentication); // Add authentication

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetVisitorPassById() throws Exception {
        // Arrange
        int requestId = 1;
        VisitorPassRequest visitorPassRequest = new VisitorPassRequest();
        VisitorPassRequestDTO visitorPassRequestDTO = new VisitorPassRequestDTO();
        Authentication authentication = mock(Authentication.class);

        when(visitorPassService.getVisitorPassRequestById(requestId)).thenReturn(Optional.of(visitorPassRequest));
        when(modelMapper.map(visitorPassRequest, VisitorPassRequestDTO.class)).thenReturn(visitorPassRequestDTO);

        // Act
        ResponseEntity<VisitorPassRequestDTO> response = visitorPassController.getVisitorPassById(requestId, authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(visitorPassRequestDTO, response.getBody());
    }
}