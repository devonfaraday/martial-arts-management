package com.whitelabel.martialarts.service.service;

import com.whitelabel.martialarts.model.Organization;

public interface OrganizationService {
    Organization getOrganization(Long id);
    Organization updateOrganization(Organization organization);
    Organization createOrganization(Organization organization);
    void deleteOrganization(Long id);
}
