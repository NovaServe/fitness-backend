/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.emails.brevo;

import com.novaserve.fitness.emails.EmailRequestBody;
import com.novaserve.fitness.emails.share.To;
import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewAccountVerificationRequestBody implements EmailRequestBody {
    private int template_id;

    private To to;

    private Params params;
}
