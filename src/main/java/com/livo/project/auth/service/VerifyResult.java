package com.livo.project.auth.service;

public record VerifyResult(boolean success, String msg) {
    public static VerifyResult ok() {
        return new VerifyResult(true, null);
    }

    public static VerifyResult fail(String msg) {
        return new VerifyResult(false, msg);
    }
}
