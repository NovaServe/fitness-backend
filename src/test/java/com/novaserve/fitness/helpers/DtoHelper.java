/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.helpers;

import com.novaserve.fitness.users.dto.CreateUserReqDto;

public class DtoHelper {
  public CreateUserRequestDtoBuilder createUserRequestDto() {
    return new CreateUserRequestDtoBuilder();
  }

  public static class CreateUserRequestDtoBuilder {
    private int seed;
    private String roleName;
    private String genderName;
    private String ageGroupName;

    public CreateUserReqDto empty() {
      return CreateUserReqDto.builder().build();
    }

    public CreateUserRequestDtoBuilder seed(int seed) {
      this.seed = seed;
      return this;
    }

    public CreateUserRequestDtoBuilder role(String roleName) {
      this.roleName = roleName;
      return this;
    }

    public CreateUserRequestDtoBuilder gender(String genderName) {
      this.genderName = genderName;
      return this;
    }

    public CreateUserRequestDtoBuilder ageGroup(String ageGroupName) {
      this.ageGroupName = ageGroupName;
      return this;
    }

    public CreateUserReqDto get() {
      return CreateUserReqDto.builder()
          .username("username" + seed)
          .fullName("User Full Name")
          .email("username" + seed + "@email.com")
          .phone("+312300000" + seed)
          .password("Password1!")
          .confirmPassword("Password1!")
          .role(roleName)
          .gender(genderName)
          .ageGroup(ageGroupName)
          .build();
    }
  }
}
