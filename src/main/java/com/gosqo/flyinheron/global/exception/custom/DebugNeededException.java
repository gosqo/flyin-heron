package com.gosqo.flyinheron.global.exception.custom;

public class DebugNeededException extends RuntimeException {

    public DebugNeededException() {
        super("대비하지 못한 예외 발생. 디버깅, 수정 필요.");
    }

    public DebugNeededException(String message) {
        super("WARNING: 터지지 않을 것이라 예상한 수준에서 예외 발생. 디버깅 요구됨.: " + message);
    }
}
