package com.testinc.centralizedpaymentsystem.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "payments")
@Data
public class Payments {

    @Id
    @Column(name = "payment_id")
    private String paymentId;

    @ManyToOne
    private Accounts accounts;

    @Column(name = "payment_type")
    private String paymentType;

    @Column(name = "credit_card")
    private String creditCard;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "created_on")
    private Timestamp createdOn;

    @Column(name = "processed")
    private Boolean processed;

    @Column(name = "valid")
    private Boolean valid;


}
