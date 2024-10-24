package com.gosqo.flyinheron.global.exception;

import com.gosqo.flyinheron.domain.Member;

public class ThrowIf {

    public static void NotMatchedResourceOwner(Member requester, Long resourceOwnerId) {
        if (!requester.getId().equals(resourceOwnerId)) {
            throw new IllegalArgumentException("요청자와 자원 소유자의 불일치");
        }
    }
}
