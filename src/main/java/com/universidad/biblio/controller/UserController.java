package com.universidad.biblio.controller;

import com.universidad.biblio.model.User;
import com.universidad.biblio.service.UserServi;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserServi service;

    public UserController(UserServi service) {
        this.service = service;
    }

    @GetMapping
    public List<User> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public User find(@PathVariable int id) {
        return service.find(id);
    }

    @PostMapping
    public void register(@RequestBody User user) {
        service.register(user);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable int id, @RequestBody User user) {
        return service.update(id, user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        service.delete(id);
    }
}
