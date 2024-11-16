/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.emails.repository;

import com.novaserve.fitness.emails.model.ConfirmationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfirmationCodeRepository extends JpaRepository<ConfirmationCode, Long> {}
