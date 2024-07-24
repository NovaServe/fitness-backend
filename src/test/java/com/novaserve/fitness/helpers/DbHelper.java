/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.helpers;

import com.novaserve.fitness.users.model.AgeGroup;
import com.novaserve.fitness.users.model.Gender;
import com.novaserve.fitness.users.model.Role;
import com.novaserve.fitness.users.model.User;
import com.novaserve.fitness.users.repository.AgeGroupRepository;
import com.novaserve.fitness.users.repository.GenderRepository;
import com.novaserve.fitness.users.repository.RoleRepository;
import com.novaserve.fitness.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@TestComponent
public class DbHelper {
  @Autowired RoleRepository roleRepository;

  @Autowired GenderRepository genderRepository;

  @Autowired AgeGroupRepository ageGroupRepository;

  @Autowired UserRepository userRepository;

  @Autowired PasswordEncoder passwordEncoder;

  @Transactional
  public void deleteAll() {
    userRepository.deleteAll();
    roleRepository.deleteAll();
    genderRepository.deleteAll();
    ageGroupRepository.deleteAll();
  }

  public Role superadminRole() {
    return roleRepository.save(Role.builder().name("ROLE_SUPERADMIN").build());
  }

  public Role adminRole() {
    return roleRepository.save(Role.builder().name("ROLE_ADMIN").build());
  }

  public Role customerRole() {
    return roleRepository.save(Role.builder().name("ROLE_CUSTOMER").build());
  }

  public Role instructorRole() {
    return roleRepository.save(Role.builder().name("ROLE_INSTRUCTOR").build());
  }

  public Gender male() {
    return genderRepository.save(Gender.builder().name("Male").build());
  }

  public Gender female() {
    return genderRepository.save(Gender.builder().name("Female").build());
  }

  public AgeGroup child() {
    return ageGroupRepository.save(AgeGroup.builder().name("Child").build());
  }

  public AgeGroup teen() {
    return ageGroupRepository.save(AgeGroup.builder().name("Teenager").build());
  }

  public AgeGroup adult() {
    return ageGroupRepository.save(AgeGroup.builder().name("Adult").build());
  }

  public AgeGroup senior() {
    return ageGroupRepository.save(AgeGroup.builder().name("Senior").build());
  }

  public UserBuilder user() {
    return new DbHelper.UserBuilder(userRepository, passwordEncoder);
  }

  public static class UserBuilder {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private int seed;
    private Role role;
    private Gender gender;
    private AgeGroup ageGroup;

    public UserBuilder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
      this.userRepository = userRepository;
      this.passwordEncoder = passwordEncoder;
    }

    public UserBuilder seed(int seed) {
      this.seed = seed;
      return this;
    }

    public UserBuilder role(Role role) {
      this.role = role;
      return this;
    }

    public UserBuilder gender(Gender gender) {
      this.gender = gender;
      return this;
    }

    public UserBuilder ageGroup(AgeGroup ageGroup) {
      this.ageGroup = ageGroup;
      return this;
    }

    public User get() {
      return userRepository.save(
          User.builder()
              .username("username" + seed)
              .fullName("User Full Name")
              .email("username" + seed + "@email.com")
              .phone("+312300000" + seed)
              .password(passwordEncoder.encode("Password1!"))
              .role(role)
              .gender(gender)
              .ageGroup(ageGroup)
              .build());
    }
  }

  public User getUser(String username) {
    return userRepository.findByUsername(username).orElse(null);
  }
}