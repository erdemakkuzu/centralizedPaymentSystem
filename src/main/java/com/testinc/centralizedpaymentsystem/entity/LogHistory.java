package com.testinc.centralizedpaymentsystem.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "log_history")
@Data
public class LogHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name = "payment_id")
    private String paymentId;

    @Column(name="error_type")
    private String errorType;

    @Column(name="error_description")
    private String errorDescription;

    @Column(name="posted")
    private Boolean posted;

}
