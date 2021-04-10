package com.testinc.centralizedpaymentsystem.entity;

import com.sun.istack.NotNull;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "accounts")
@Data
public class Accounts {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "account_id")
    private Integer accountId;

    @Column(name = "name", unique = true)
    @NotNull
    private String name;

    @Column(name = "email", unique = true)
    @NotNull
    private String email;

    @Column(name = "birthdate")
    private Date birthDate;

    @Column(name = "last_payment_date")
    private Date lastPaymentDate;

    @Column(name = "created_on")
    private Timestamp createdOn;

    @OneToMany(mappedBy = "accounts")
    private List<Payments> payments;

}
