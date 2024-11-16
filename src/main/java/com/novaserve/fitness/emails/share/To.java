/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.emails.share;

import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class To {
    private String email;
}
