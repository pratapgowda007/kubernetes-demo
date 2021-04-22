package com.nsc.kubernetes.demo.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ITEM")
public class Item {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String clinicalItemId;
    private String name;

    public String getClinicalItemId() {
        return clinicalItemId;
    }

    public void setClinicalItemId(String clinicalItemId) {
        this.clinicalItemId = clinicalItemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
