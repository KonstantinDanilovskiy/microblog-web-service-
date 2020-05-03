package com.letscode.sweater.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CaptchaDTO {
    private boolean success;

    // timestamp of the challenge load (ISO format yyyy-MM-dd'T'HH:mm:ssZZ)
    @JsonAlias("challenge_ts")
    private LocalDateTime challengeTs;

    // the hostname of the site where the reCAPTCHA was solved
    private String hostname;

    @JsonAlias("error-codes")
    private Set<String> errorCodes;
}
