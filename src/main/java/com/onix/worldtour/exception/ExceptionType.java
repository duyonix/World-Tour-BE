package com.onix.worldtour.exception;

public enum ExceptionType {
    ENTITY_NOT_FOUND("not.found"),
    DUPLICATE_ENTITY("duplicate"),
    ALREADY_USED_ELSEWHERE("used.elsewhere"),
    NOT_MATCH("not.match"),
    DUPLICATE_LEVEL("duplicate.level"),
    ENTITY_EXCEPTION("exception");


    private final String value;

    ExceptionType(String value) {
        this.value = value;
    }

    String getValue() {
        return this.value;
    }
}

