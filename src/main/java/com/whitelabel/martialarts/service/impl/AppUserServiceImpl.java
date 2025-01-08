package com.whitelabel.martialarts.service.impl;

import com.whitelabel.martialarts.model.AppUser;
import com.whitelabel.martialarts.repository.AppUserRepository;
import com.whitelabel.martialarts.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.List;

@Service
public class AppUserServiceImpl implements AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<AppUser> getAllAppUsers() {
        return appUserRepository.findAll();
    }

    @Override
    public AppUser getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<AppUser> optionalUser = appUserRepository.findByUsername(userDetails.getUsername());
        return optionalUser.orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public AppUser getAppUserById(Long id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    public AppUser createAppUser(AppUser appUser) {
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        return appUserRepository.save(appUser);
    }

    @Override
    public AppUser updateAppUser(Long id, AppUser appUser) {
        AppUser existingAppUser = appUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        existingAppUser.setUsername(appUser.getUsername());
        existingAppUser.setFirstName(appUser.getFirstName());
        existingAppUser.setLastName(appUser.getLastName());
        existingAppUser.setEmail(appUser.getEmail());
        existingAppUser.setRole(appUser.getRole());
        existingAppUser.setOrganization(appUser.getOrganization());
        
        // Encode the password before saving if it has changed
        if (!appUser.getPassword().equals(existingAppUser.getPassword())) {
            existingAppUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        }
        
        return appUserRepository.save(existingAppUser);
    }

    @Override
    public void deleteAppUser(Long id) {
        appUserRepository.deleteById(id);
    }
}
