package com.novaserve.fitness.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Service
public class ApiUrl {
    @Value("/error/**")
    private String errorURL;

    @Value("${api.basePath}/${api.version}/users/auth/**")
    private String authURL;

    @Value("${api.basePath}/${api.version}/test")
    private String testURL;
}
