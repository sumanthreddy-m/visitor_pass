package com.sumanth.visitor_pass_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sumanth.visitor_pass_management.entity.VisitorsIDProofs;

@Repository
public interface VisitorsIDProofsRepository extends JpaRepository<VisitorsIDProofs, Integer> {
}