package com.shiftclient.accounts;

public record StoredAccount(
        String username,
        String uuid,
        String type,
        String accessToken,
        String refreshToken,
        long expiresAtEpochMs
) {
}
