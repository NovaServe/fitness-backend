/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.emails.brevo;

import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Params {
    private String fullName;

    private String confirmationUrl;
}
