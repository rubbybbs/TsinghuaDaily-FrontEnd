package com.example.tsinghuadaily.models;

public class UserConfiguration {
    private String label;
    private String value;
    private int icon;

    public UserConfiguration(String label, String value, int icon)
    {
        this.label = label;
        this.value = value;
        this.icon = icon;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public int getIcon() {
        return icon;
    }
}

