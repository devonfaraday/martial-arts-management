package com.whitelabel.martialarts.service;

import com.whitelabel.martialarts.model.AppUser;
import java.util.List;

public interface AppUserService {
    List<AppUser> getAllAppUsers();
    AppUser getAppUserById(Long id);
    AppUser createAppUser(AppUser user);
    AppUser updateAppUser(Long id, AppUser user);
    void deleteAppUser(Long id);
}
