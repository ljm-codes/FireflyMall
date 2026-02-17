package com.fm.service;

import POJO.Login.AdminLoginRequest;
import POJO.DataList;

public interface PersonnelManagementService {
    boolean addPersonnel(AdminLoginRequest adminLoginRequest);

    DataList<AdminLoginRequest> getPersonnel(String sortBy, Integer page, Integer size);

    boolean updatePersonnel(AdminLoginRequest adminLoginRequest);

    boolean deletePersonnel(AdminLoginRequest adminLoginRequest);
}
