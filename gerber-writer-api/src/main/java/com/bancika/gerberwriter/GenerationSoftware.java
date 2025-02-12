package com.bancika.gerberwriter;

public class GenerationSoftware {

    private String vendor;
    private String application;
    private String version;
    
    public GenerationSoftware(String vendor, String application, String version) {
        this.vendor = sanitizeField(vendor);
        this.application = sanitizeField(application);
        this.version = sanitizeField(version);
    }

    private static String sanitizeField(String field) {
        return field.replaceAll("[*%,]", "_").trim();
    }

    public String getVendor() {
        return vendor;
    }

    public String getApplication() {
        return application;
    }

    public String getVersion() {
        return version;
    }
}
