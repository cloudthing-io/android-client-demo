package io.cloudthing.sim.utils;

/**
 * Created by kleptoman on 02.09.16.
 */
public class CredentialCache {

    private static CredentialCache instance;

    public static CredentialCache getInstance() {
        if (instance == null) {
            instance = new CredentialCache();
        }
        return instance;
    }

    private String tenant;
    private String deviceId;
    private String token;

    public void setCredentials(String tenant, String deviceId, String token) {
        this.tenant = tenant;
        this.deviceId = deviceId;
        this.token = token;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
