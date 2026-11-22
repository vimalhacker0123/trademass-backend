package com.trading.billing.service;

import com.trading.billing.dto.BillDto;
import com.trading.billing.entity.Bill;
import com.trading.billing.exception.ResourceNotFoundException;
import com.trading.billing.repository.BillRepository;
import org.springframework.stereotype.Service;
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
        bill.setStatus("PENDING");
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
        bill.setCustomerName(dto.getCustomerName());
        bill.setCustomerEmail(dto.getCustomerEmail());
        bill.setAmount(dto.getAmount());
        bill.setDueDate(dto.getDueDate());
        return toDto(repository.save(bill));
    }

    @Override
    public void deleteBill(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Bill not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public BillDto markAsPaid(Long id) {
        Bill bill = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Bill not found with id: " + id));
        bill.setStatus("PAID");
        return toDto(repository.save(bill));
    }

    private BillDto toDto(Bill b) {
        BillDto d = new BillDto();
        d.setId(b.getId());
        d.setCustomerName(b.getCustomerName());
        d.setCustomerEmail(b.getCustomerEmail());
        d.setAmount(b.getAmount());
        d.setStatus(b.getStatus());
        d.setBillDate(b.getBillDate());
        d.setDueDate(b.getDueDate());
        return d;
    }

    private Bill toEntity(BillDto d) {
        Bill b = new Bill();
        b.setCustomerName(d.getCustomerName());
        b.setCustomerEmail(d.getCustomerEmail());
        b.setAmount(d.getAmount());
        b.setStatus(d.getStatus());
        b.setBillDate(d.getBillDate());
        b.setDueDate(d.getDueDate());
        return b;
    }
}