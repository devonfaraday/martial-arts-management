package com.whitelabel.martialarts.service.service;

import com.whitelabel.martialarts.model.AppUser;
import java.util.Optional;
import java.util.List;

public interface AppUserService {
    List<AppUser> getAllAppUsers();
    AppUser getAppUserById(Long id);
    AppUser getCurrentUser();
    AppUser createAppUser(AppUser user);
    AppUser updateAppUser(Long id, AppUser user);
    void deleteAppUser(Long id);
}
