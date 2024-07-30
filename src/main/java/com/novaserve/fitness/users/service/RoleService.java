/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.service;

public interface RoleService {

  boolean hasRoleSuperAdmin(long id);

  boolean hasRoleAdmin(long id);

  boolean hasRoleInstructor(long id);

  boolean hasRoleCustomer(long id);
}
