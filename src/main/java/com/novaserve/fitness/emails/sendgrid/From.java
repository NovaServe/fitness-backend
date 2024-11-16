/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.emails.sendgrid;

import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class From {
    private String from;
}
