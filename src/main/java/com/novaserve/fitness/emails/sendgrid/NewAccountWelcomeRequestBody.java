/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.emails.sendgrid;

import com.novaserve.fitness.emails.EmailRequestBody;
import java.util.List;
import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewAccountWelcomeRequestBody implements EmailRequestBody {
    private From from;

    private String template_id;

    private List<Personalization> personalizations;
}
