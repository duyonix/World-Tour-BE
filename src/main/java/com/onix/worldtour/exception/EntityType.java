package com.onix.worldtour.exception;

public enum EntityType {
    USER("user"),
    CATEGORY("category"),
    REGION("region"),
    SCENE_SPOT("scene_spot"),
    COSTUME("costume"),
    ;

    private final String value;

    EntityType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
