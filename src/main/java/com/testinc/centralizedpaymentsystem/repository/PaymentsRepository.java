package com.testinc.centralizedpaymentsystem.repository;

import com.testinc.centralizedpaymentsystem.entity.Payments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentsRepository extends JpaRepository<Payments, String> {
}
