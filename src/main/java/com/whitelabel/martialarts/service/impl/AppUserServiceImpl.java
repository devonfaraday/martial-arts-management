package com.whitelabel.martialarts.service.impl;

import com.whitelabel.martialarts.model.AppUser;
import com.whitelabel.martialarts.repository.AppUserRepository;
import com.whitelabel.martialarts.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppUserServiceImpl implements AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Override
    public List<AppUser> getAllAppUsers() {
        return appUserRepository.findAll();
    }

    @Override
    public AppUser getAppUserById(Long id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    public AppUser createAppUser(AppUser appUser) {
        return appUserRepository.save(appUser);
    }

    @Override
    public AppUser updateAppUser(Long id, AppUser appUser) {
        AppUser existingAppUser = appUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        existingAppUser.setUsername(appUser.getUsername());
        existingAppUser.setPassword(appUser.getPassword());
        existingAppUser.setFirstName(appUser.getFirstName());
        existingAppUser.setLastName(appUser.getLastName());
        existingAppUser.setEmail(appUser.getEmail());
        existingAppUser.setRole(appUser.getRole());
        existingAppUser.setOrganization(appUser.getOrganization());
        existingAppUser.setCreatedAt(appUser.getCreatedAt());
        existingAppUser.setUpdatedAt(appUser.getUpdatedAt());
        return appUserRepository.save(existingAppUser);
    }

    @Override
    public void deleteAppUser(Long id) {
        appUserRepository.deleteById(id);
    }
}
