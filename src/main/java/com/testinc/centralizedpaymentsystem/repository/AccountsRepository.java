package com.testinc.centralizedpaymentsystem.repository;

import com.testinc.centralizedpaymentsystem.entity.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountsRepository extends JpaRepository<Accounts, Integer> {
}
