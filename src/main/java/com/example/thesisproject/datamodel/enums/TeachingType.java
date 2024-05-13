package com.example.thesisproject.datamodel.enums;

public enum TeachingType {

    LECTURE("LECTURE"),
    LAB("LAB"),

    SEMINAR("SEMINAR");

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
    public static TeachingType fromString(String text) {
        for (TeachingType b : TeachingType.values()) {
            if (b.type.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}
