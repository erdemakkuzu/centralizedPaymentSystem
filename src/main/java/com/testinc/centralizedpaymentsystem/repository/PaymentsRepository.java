package com.testinc.centralizedpaymentsystem.repository;

import com.testinc.centralizedpaymentsystem.entity.Payments;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentsRepository extends JpaRepository<Payments, String> {

    Optional<Payments>findByProcessed(Boolean processed);

    List<Payments> findByProcessed(Boolean processed, Pageable pageable);

}
