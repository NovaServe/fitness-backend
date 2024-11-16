/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.emails.sendgrid;

import com.novaserve.fitness.emails.share.To;
import java.util.List;
import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Personalization {
    private List<To> to;

    private DynamicTemplateData dynamic_template_data;
}
