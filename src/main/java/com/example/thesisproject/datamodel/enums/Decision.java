package com.example.thesisproject.datamodel.enums;


public enum Decision {

    PENDING("PENDING"),
    YES("YES"),
    NO("NO");

    private final String type;

    Decision(String type){
        this.type = type;
    }

    @Override
    public String toString() {
        return "Decision{" +
                "type='" + type + '\'' +
                '}';
    }
}
