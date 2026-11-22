package com.trading.billing.controller;

import com.trading.billing.dto.BillDto;
import com.trading.billing.service.BillService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bills")
public class BillingController {

    private final BillService service;

    public BillingController(BillService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<BillDto> create(@RequestBody BillDto dto) {
        return ResponseEntity.ok(service.createBill(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getBillById(id));
    }

    @GetMapping
    public ResponseEntity<List<BillDto>> getAll() {
        return ResponseEntity.ok(service.getAllBills());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BillDto> update(@PathVariable Long id, @RequestBody BillDto dto) {
        return ResponseEntity.ok(service.updateBill(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        service.deleteBill(id);
        return ResponseEntity.ok("Bill deleted successfully");
    }

    @PatchMapping("/{id}/pay")
    public ResponseEntity<BillDto> pay(@PathVariable Long id) {
        return ResponseEntity.ok(service.markAsPaid(id));
    }
}