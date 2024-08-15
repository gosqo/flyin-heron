package com.vong.manidues.domain.fixture;

import com.vong.manidues.dto.auth.AuthenticationRequest;

public class MemberFixture {
    public static final String EMAIL = "check@auth.io";
    public static final String PASSWORD = "Password0";
    public static final String ENCODED_PASSWORD = "$2a$10$.YTh5A02ylk3nhxMltZ0F.fdPp0InH6Sin.w91kve8SEGUYR4KAZ.";
    public static final String NICKNAME = "testOnly";
    public static final AuthenticationRequest AUTH_REQUEST = AuthenticationRequest.builder()
            .email(EMAIL)
            .password(PASSWORD)
            .build();
}
