package com.sumanth.visitor_pass_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sumanth.visitor_pass_management.entity.VisitorPassRequest;
import com.sumanth.visitor_pass_management.enums.RequestStatus;

import java.util.Date;
import java.util.List;

@Repository
public interface VisitorPassRequestRepository extends JpaRepository<VisitorPassRequest, Integer> {
    List<VisitorPassRequest> findByLocationAndRequestStatus(String location, RequestStatus requestStatus);

    // New method to find requests by raisedByEmployee
    List<VisitorPassRequest> findByRaisedByEmployee(String raisedByEmployee);

    // New method to count family member requests in a given month
    @Query("SELECT COUNT(v) FROM VisitorPassRequest v WHERE v.raisedByEmployee = :employee AND MONTH(v.requestRaisedOn) = MONTH(:date) AND YEAR(v.requestRaisedOn) = YEAR(:date) AND v.visitorType.type IN ('Parents', 'Child', 'Sibling', 'Spouse')")
    int countFamilyMemberRequestsInMonth(@Param("employee") String employee, @Param("date") Date date);

    // New method to find requests by raisedByEmployee and date range.
    @Query("SELECT v FROM VisitorPassRequest v WHERE v.raisedByEmployee = :employee AND v.requestRaisedOn BETWEEN :startDate AND :endDate")
    List<VisitorPassRequest> findRequestsByEmployeeAndDateRange(@Param("employee")String employee, @Param("startDate")Date startDate, @Param("endDate") Date endDate);
}