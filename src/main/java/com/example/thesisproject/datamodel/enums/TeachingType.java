package com.example.thesisproject.datamodel.enums;

public enum TeachingType {

    LECTURE("LECTURE"),
    LAB("LAB");

    private final String type;

    TeachingType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "TeachingType{" +
                "type='" + type + '\'' +
                '}';
    }
}
