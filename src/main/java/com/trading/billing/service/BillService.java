package com.trading.billing.service;

import com.trading.billing.dto.BillDto;
import java.util.List;

public interface BillService {
    BillDto createBill(BillDto billDto);
    BillDto getBillById(Long id);
    List<BillDto> getAllBills();
    BillDto updateBill(Long id, BillDto billDto);
    void deleteBill(Long id);
    BillDto markAsPaid(Long id);
}