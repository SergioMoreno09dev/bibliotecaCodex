package com.universidad.biblio.controller;

import com.universidad.biblio.model.Fine;
import com.universidad.biblio.service.FineService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fines")
public class FineController {
    private final FineService service;

    public FineController(FineService service) {
        this.service = service;
    }

    @GetMapping
    public List<Fine> list(@RequestParam(required = false) Integer userId) {
        if (userId != null) {
            return service.byUser(userId);
        }
        return service.list();
    }

    @PostMapping
    public Fine generate(@RequestBody FineRequest request) {
        return service.generate(request.loanId(), request.amount());
    }

    @PatchMapping("/{id}/pay")
    public Fine pay(@PathVariable Long id) {
        return service.pay(id);
    }

    public record FineRequest(Long loanId, double amount) {
    }
}
