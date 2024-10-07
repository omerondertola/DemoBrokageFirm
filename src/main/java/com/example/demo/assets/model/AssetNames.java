package com.example.demo.assets.model;

public enum AssetNames {

    TRY("TRY"),
    USD("USD"),
    EUR("EUR");

    private final String name;

    AssetNames(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
