package com.core.pica.enums;

public enum ContentTypeEnum {
    /**
     * SOAP XML content type
     */
    SOAP_XML("application/soap+xml"),

    HTML("text/html");

    private final String value;

    private ContentTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
