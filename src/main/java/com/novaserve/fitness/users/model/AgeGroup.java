/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.model;

import jakarta.persistence.*;
import java.util.Set;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "age_groups")
public class AgeGroup {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false, unique = true)
  private String name;

  @OneToMany(mappedBy = "ageGroup")
  private Set<User> users;
}
