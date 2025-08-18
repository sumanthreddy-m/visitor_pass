package com.sumanth.visitor_pass_management.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class VisitorsIDProofsId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "id_proof_type")
    private String IDProofType;

    @Column(name = "id_proof_no")
    private String IDProofNo;

    public VisitorsIDProofsId() {
    }

    public VisitorsIDProofsId(String IDProofType, String IDProofNo) {
        this.IDProofType = IDProofType;
        this.IDProofNo = IDProofNo;
    }

    public String getIDProofType() {
        return IDProofType;
    }

    public void setIDProofType(String IDProofType) {
        this.IDProofType = IDProofType;
    }

    public String getIDProofNo() {
        return IDProofNo;
    }

    public void setIDProofNo(String IDProofNo) {
        this.IDProofNo = IDProofNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VisitorsIDProofsId that = (VisitorsIDProofsId) o;
        return Objects.equals(IDProofType, that.IDProofType) && Objects.equals(IDProofNo, that.IDProofNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(IDProofType, IDProofNo);
    }
}