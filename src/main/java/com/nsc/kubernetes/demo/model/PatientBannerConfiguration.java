package com.nsc.kubernetes.demo.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PATIENT_BANNER_CONFIGURATION")
public class PatientBannerConfiguration {
    @Id
    //@GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name = "PATIENT_BANNER_ID")
    private String patientBannerId;

    @Column(name = "ORGANIZATION_ID")
    private String organizationId;

    @Column(name = "UNIT_ID", unique = true)
    private String unitId;

    @Column(name = "IS_DEFAULT")
    private boolean isDefault;

    @ElementCollection
    @CollectionTable(name = "PATIENT_BANNER_ITEM_LIST",
            joinColumns = {
                    @JoinColumn(name = "PATIENT_BANNER_ID")
            })
    @Column(name = "CLINICAL_ITEM_ID")
    private List<String> clinicalItemList = new ArrayList<>();

    public String getPatientBannerId() {
        return patientBannerId;
    }

    public void setPatientBannerId(String patientBannerId) {
        this.patientBannerId = patientBannerId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public List<String> getClinicalItemList() {
        return clinicalItemList;
    }

    public void setClinicalItemList(List<String> clinicalItemList) {
        this.clinicalItemList = clinicalItemList;
    }
}
