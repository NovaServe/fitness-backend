/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness;

import com.novaserve.fitness.users.model.Gender;
import com.novaserve.fitness.users.model.Role;
import com.novaserve.fitness.users.model.User;

public interface Helper {
  Role createSuperadminRole();

  Role createAdminRole();

  Role createCustomerRole();

  Role createInstructorRole();

  Gender createGender(String maleOrFemale);

  User createSuperadminUser(int seed);

  User createAdminUser(int seed);

  User createCustomerUser(int seed);

  User createInstructorUser(int seed);
}
