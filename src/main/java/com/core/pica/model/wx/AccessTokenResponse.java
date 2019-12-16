package com.core.pica.model.wx;

import lombok.Data;

@Data
public class AccessTokenResponse {
    private String access_token;
    private Integer expires_in;
}
