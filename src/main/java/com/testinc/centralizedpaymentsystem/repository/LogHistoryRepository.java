package com.testinc.centralizedpaymentsystem.repository;

import com.testinc.centralizedpaymentsystem.entity.LogHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogHistoryRepository extends JpaRepository<LogHistory,Integer> {

    List<LogHistory> findByPosted(Boolean successfullyLogged, Pageable pageable);

}
