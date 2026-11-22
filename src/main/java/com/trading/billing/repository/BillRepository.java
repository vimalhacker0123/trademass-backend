package com.trading.billing.repository;

import com.trading.billing.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByStatus(String status);
    List<Bill> findByCustomerEmail(String email);
}