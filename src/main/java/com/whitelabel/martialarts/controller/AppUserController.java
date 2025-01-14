package com.whitelabel.martialarts.controller;

import com.whitelabel.martialarts.model.AppUser;
import com.whitelabel.martialarts.service.service.AppUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appUsers")
public class AppUserController {

    @Autowired
    private AppUserService appUserService;

    @GetMapping
    public List<AppUser> getAllAppUsers() {
        return appUserService.getAllAppUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppUser> getUserById(@PathVariable Long id) {
        AppUser appUser = appUserService.getAppUserById(id);
        return ResponseEntity.ok(appUser);
    }

    @PostMapping
    public AppUser createAppUser(@RequestBody AppUser user) {
        return appUserService.createAppUser(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppUser> updateAppUser(@PathVariable Long id, @RequestBody AppUser user) {
        AppUser updatedAppUser = appUserService.updateAppUser(id, user);
        return ResponseEntity.ok(updatedAppUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppUser(@PathVariable Long id) {
        appUserService.deleteAppUser(id);
        return ResponseEntity.noContent().build();
    }
}
