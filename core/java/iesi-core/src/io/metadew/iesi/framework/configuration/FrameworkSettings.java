package io.metadew.iesi.framework.configuration;

public enum FrameworkSettings {
    IDENTIFIER("iesi");

    private String value;

    FrameworkSettings(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}