package com.gosqo.flyinheron.domain;

public enum EntityStatus {
    STANDBY // 활성화에 운영진 승인이 필요한 개체의 초기화 값.
    , ACTIVE // 정상적으로 등록이 가능한 개체로, 운영진 승인 없이 활성화 가능한 개체의 초기화 값.
    , SOFT_DELETED; // 비활성, 개체가 더 이상 유효하지 않음. 데이터베이스에 잔존하지만 사용자에게는 보이지 않는 개체.
}
