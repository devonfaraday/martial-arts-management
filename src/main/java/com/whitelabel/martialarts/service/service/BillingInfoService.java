package com.whitelabel.martialarts.service.service;

import com.whitelabel.martialarts.model.BillingInfo;

public interface BillingInfoService {
    BillingInfo getBillingInfoByStudentId(Long studentId);
    BillingInfo updateBillingInfo(Long studentId, BillingInfo billingInfo);
}
