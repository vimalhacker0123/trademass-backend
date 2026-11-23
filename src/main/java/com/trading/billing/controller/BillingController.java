package com.trading.billing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

import com.trading.billing.entity.Bill;
import com.trading.billing.repository.BillRepository;

@RestController
@RequestMapping("/api/bills")
@CrossOrigin(origins = "*")
public class BillingController {

    @Autowired
    private BillRepository billRepository;

    @GetMapping
    public List<Bill> getAll() {
        return billRepository.findAll();
    }

    @PostMapping
    public Bill create(@RequestBody Bill bill) {
        if (bill.getTotalAmount() == null || bill.getTotalAmount().compareTo(BigDecimal.ZERO) == 0) {
            bill.setTotalAmount(bill.getAmount());
        }
        if (bill.getAmount() == null || bill.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            bill.setAmount(bill.getTotalAmount());
        }
        if (bill.getPaidAmount() == null) bill.setPaidAmount(BigDecimal.ZERO);
        if (bill.getCustomerEmail() == null) bill.setCustomerEmail(bill.getMobile());

        BigDecimal total = bill.getAmount() != null ? bill.getAmount() : BigDecimal.ZERO;
        BigDecimal paid = bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO;
        bill.setBalanceAmount(total.subtract(paid).max(BigDecimal.ZERO));
        bill.setStatus(bill.getBalanceAmount().compareTo(BigDecimal.ZERO) <= 0 && total.compareTo(BigDecimal.ZERO) > 0 ? "PAID" : "PENDING");
        return billRepository.save(bill);
    }

    @PutMapping("/{id}")
    public Bill update(@PathVariable Long id, @RequestBody Bill updated) {
        Bill b = billRepository.findById(id).orElseThrow();
        if (updated.getCustomerName() != null) b.setCustomerName(updated.getCustomerName());
        if (updated.getMobile() != null) b.setMobile(updated.getMobile());
        if (updated.getDistrict() != null) b.setDistrict(updated.getDistrict());
        if (updated.getCourse() != null) b.setCourse(updated.getCourse());
        if (updated.getDuration() != null) b.setDuration(updated.getDuration());
        if (updated.getTransactionId() != null) b.setTransactionId(updated.getTransactionId());
        if (updated.getAmount() != null) b.setAmount(updated.getAmount());
        if (updated.getPaidAmount() != null) b.setPaidAmount(updated.getPaidAmount());
        if (updated.getPaymentMode() != null) b.setPaymentMode(updated.getPaymentMode());
        if (updated.getStatus() != null) b.setStatus(updated.getStatus());

        BigDecimal total = b.getAmount() != null ? b.getAmount() : BigDecimal.ZERO;
        BigDecimal paid = b.getPaidAmount() != null ? b.getPaidAmount() : BigDecimal.ZERO;
        b.setBalanceAmount(total.subtract(paid).max(BigDecimal.ZERO));
        if (b.getBalanceAmount().compareTo(BigDecimal.ZERO) <= 0 && total.compareTo(BigDecimal.ZERO) > 0) b.setStatus("PAID");
        return billRepository.save(b);
    }

    @PatchMapping("/{id}/pay")
    public Bill pay(@PathVariable Long id, @RequestBody(required = false) Bill req) {
        Bill b = billRepository.findById(id).orElseThrow();
        BigDecimal total = b.getAmount() != null ? b.getAmount() : BigDecimal.ZERO;
        // FIX: setPaidAmount dhaan, setAmount illa!
        if (req != null && req.getPaidAmount() != null) b.setPaidAmount(req.getPaidAmount());
        else b.setPaidAmount(total);
        b.setBalanceAmount(BigDecimal.ZERO);
        b.setStatus("PAID");
        if (req != null && req.getPaymentMode() != null) b.setPaymentMode(req.getPaymentMode());
        return billRepository.save(b);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        billRepository.deleteById(id);
    }
}