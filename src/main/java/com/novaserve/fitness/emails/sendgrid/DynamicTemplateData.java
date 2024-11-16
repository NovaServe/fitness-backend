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
public class DynamicTemplateData {
    private String fullName;

    private String confirmationUrl;
}
