package com.testinc.centralizedpaymentsystem.repository;

import com.testinc.centralizedpaymentsystem.entity.Payments;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentsRepository extends JpaRepository<Payments, String> {

    List<Payments> findByProcessed(Boolean processed, Pageable pageable);
}
