package com.trading.billing.service;

import com.trading.billing.dto.BillDto;
import com.trading.billing.entity.Bill;
import com.trading.billing.exception.ResourceNotFoundException;
import com.trading.billing.repository.BillRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BillServiceImpl implements BillService {

    private final BillRepository repository;

    public BillServiceImpl(BillRepository repository) {
        this.repository = repository;
    }

    @Override
    public BillDto createBill(BillDto dto) {
        Bill bill = toEntity(dto);
        if (bill.getTotalAmount() == null || bill.getTotalAmount().compareTo(BigDecimal.ZERO) == 0) {
            bill.setTotalAmount(bill.getAmount());
        }
        if (bill.getAmount() == null) bill.setAmount(bill.getTotalAmount());
        if (bill.getPaidAmount() == null) bill.setPaidAmount(BigDecimal.ZERO);
        BigDecimal total = bill.getAmount() != null ? bill.getAmount() : BigDecimal.ZERO;
        BigDecimal paid = bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO;
        bill.setBalanceAmount(total.subtract(paid).max(BigDecimal.ZERO));
        bill.setStatus(bill.getBalanceAmount().compareTo(BigDecimal.ZERO) <= 0 && total.compareTo(BigDecimal.ZERO) > 0 ? "PAID" : "PENDING");
        return toDto(repository.save(bill));
    }

    @Override
    public BillDto getBillById(Long id) {
        Bill bill = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Bill not found with id: " + id));
        return toDto(bill);
    }

    @Override
    public List<BillDto> getAllBills() {
        return repository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public BillDto updateBill(Long id, BillDto dto) {
        Bill bill = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Bill not found with id: " + id));
        if (dto.getCustomerName() != null) bill.setCustomerName(dto.getCustomerName());
        if (dto.getCustomerEmail() != null) bill.setCustomerEmail(dto.getCustomerEmail());
        if (dto.getMobile() != null) bill.setMobile(dto.getMobile());
        if (dto.getDistrict() != null) bill.setDistrict(dto.getDistrict());
        if (dto.getCourse() != null) bill.setCourse(dto.getCourse());
        if (dto.getDuration() != null) bill.setDuration(dto.getDuration());
        if (dto.getAmount() != null) bill.setAmount(dto.getAmount());
        if (dto.getPaidAmount() != null) bill.setPaidAmount(dto.getPaidAmount());
        if (dto.getPaymentMode() != null) bill.setPaymentMode(dto.getPaymentMode());
        if (dto.getTransactionId() != null) bill.setTransactionId(dto.getTransactionId());
        if (dto.getStatus() != null) bill.setStatus(dto.getStatus());
        if (dto.getBillDate() != null) bill.setBillDate(dto.getBillDate());
        if (dto.getDueDate() != null) bill.setDueDate(dto.getDueDate());

        BigDecimal total = bill.getAmount() != null ? bill.getAmount() : BigDecimal.ZERO;
        BigDecimal paid = bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO;
        bill.setBalanceAmount(total.subtract(paid).max(BigDecimal.ZERO));
        if (bill.getBalanceAmount().compareTo(BigDecimal.ZERO) <= 0 && total.compareTo(BigDecimal.ZERO) > 0) bill.setStatus("PAID");
        return toDto(repository.save(bill));
    }

    @Override
    public void deleteBill(Long id) {
        repository.deleteById(id);
    }

    @Override
    public BillDto markAsPaid(Long id) {
        Bill bill = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Bill not found with id: " + id));
        BigDecimal total = bill.getAmount() != null ? bill.getAmount() : BigDecimal.ZERO;
        bill.setPaidAmount(total);
        bill.setBalanceAmount(BigDecimal.ZERO);
        bill.setStatus("PAID");
        return toDto(repository.save(bill));
    }

    private BillDto toDto(Bill bill) {
        BillDto dto = new BillDto();
        dto.setId(bill.getId());
        dto.setCustomerName(bill.getCustomerName());
        dto.setCustomerEmail(bill.getCustomerEmail());
        dto.setMobile(bill.getMobile());
        dto.setDistrict(bill.getDistrict());
        dto.setCourse(bill.getCourse());
        dto.setDuration(bill.getDuration());
        dto.setAmount(bill.getAmount());
        dto.setTotalAmount(bill.getTotalAmount());
        dto.setPaidAmount(bill.getPaidAmount());
        dto.setBalanceAmount(bill.getBalanceAmount());
        dto.setPaymentMode(bill.getPaymentMode());
        dto.setTransactionId(bill.getTransactionId());
        dto.setStatus(bill.getStatus());
        dto.setBillDate(bill.getBillDate());
        dto.setDueDate(bill.getDueDate());
        dto.setPaymentDate(bill.getPaymentDate());
        return dto;
    }

    private Bill toEntity(BillDto dto) {
        Bill bill = new Bill();
        bill.setId(dto.getId());
        bill.setCustomerName(dto.getCustomerName());
        bill.setCustomerEmail(dto.getCustomerEmail());
        bill.setMobile(dto.getMobile());
        bill.setDistrict(dto.getDistrict());
        bill.setCourse(dto.getCourse());
        bill.setDuration(dto.getDuration());
        bill.setAmount(dto.getAmount());
        bill.setTotalAmount(dto.getTotalAmount());
        bill.setPaidAmount(dto.getPaidAmount());
        bill.setBalanceAmount(dto.getBalanceAmount());
        bill.setPaymentMode(dto.getPaymentMode());
        bill.setTransactionId(dto.getTransactionId());
        bill.setStatus(dto.getStatus());
        bill.setBillDate(dto.getBillDate());
        bill.setDueDate(dto.getDueDate());
        bill.setPaymentDate(dto.getPaymentDate());
        return bill;
    }
}