package com.shiftclient.accounts;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shiftclient.ShiftClientMod;
import com.shiftclient.common.auth.AccountProfile;
import com.shiftclient.common.auth.AccountType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class AccountManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type LIST_TYPE = new TypeToken<List<StoredAccount>>() {}.getType();

    private final Path file = Path.of("config", "shiftclient", "accounts.json");
    private final List<StoredAccount> accounts = new ArrayList<>();
    private StoredAccount active;

    public void load() {
        try {
            if (!Files.exists(file)) {
                addOffline("Player");
                save();
                return;
            }
            List<StoredAccount> loaded = GSON.fromJson(Files.readString(file), LIST_TYPE);
            accounts.clear();
            if (loaded != null) {
                accounts.addAll(loaded);
            }
            active = accounts.isEmpty() ? null : accounts.get(0);
        } catch (IOException exception) {
            ShiftClientMod.LOGGER.error("Failed to load accounts", exception);
        }
    }

    public void save() {
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, GSON.toJson(accounts));
        } catch (IOException exception) {
            ShiftClientMod.LOGGER.error("Failed to save accounts", exception);
        }
    }

    public void addOffline(String username) {
        StoredAccount account = new StoredAccount(username, UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes()).toString(),
                AccountType.OFFLINE.name(), "", "", Long.MAX_VALUE);
        accounts.add(account);
        if (active == null) {
            active = account;
        }
        save();
    }

    public void addMicrosoft(StoredAccount account) {
        accounts.add(account);
        active = account;
        save();
    }

    public void switchAccount(String username) {
        find(username).ifPresent(account -> {
            active = account;
            save();
            ShiftClientMod.getInstance().getNotificationManager()
                    .info("Account", "Switched to " + account.username());
        });
    }

    public Optional<StoredAccount> find(String username) {
        return accounts.stream().filter(a -> a.username().equalsIgnoreCase(username)).findFirst();
    }

    public AccountProfile getActiveProfile() {
        if (active == null) {
            return AccountProfile.offline("Player");
        }
        return new AccountProfile(
                active.username(),
                UUID.fromString(active.uuid()),
                active.accessToken(),
                active.refreshToken(),
                AccountType.valueOf(active.type()),
                active.expiresAtEpochMs()
        );
    }

    public List<StoredAccount> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }

    public StoredAccount getActive() {
        return active;
    }
}
