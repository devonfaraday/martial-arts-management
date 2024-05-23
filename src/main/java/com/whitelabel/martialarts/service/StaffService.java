package com.whitelabel.martialarts.service;

import com.whitelabel.martialarts.model.Staff;
import java.util.List;

public interface StaffService {
    List<Staff> getAllStaff();
    Staff getStaffById(Long id);
    Staff createStaff(Staff staff);
    Staff updateStaff(Long id, Staff staff);
    void deleteStaff(Long id);
}
