package com.xenoamess.java_pojo_generator;

public class JavaCodeBakeProperties {
    private String outputFolder = "./output";
    private String packageName = "generated.demo";
    private boolean ifBeautify = true;
    private boolean ifLombok = true;
    private boolean ifMarkLombokGenerated = true;
    private boolean ifMongoDb = true;
    private boolean ifSpringData = true;
    private boolean ifUsingImports = false;

    public JavaCodeBakeProperties() {
    }

    public JavaCodeBakeProperties(String outputFolder, String packageName) {
        this.outputFolder = outputFolder;
        this.packageName = packageName;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isIfBeautify() {
        return ifBeautify;
    }

    public void setIfBeautify(boolean ifBeautify) {
        this.ifBeautify = ifBeautify;
    }

    public boolean isIfLombok() {
        return ifLombok;
    }

    public void setIfLombok(boolean ifLombok) {
        this.ifLombok = ifLombok;
    }

    public boolean isIfMongoDb() {
        return ifMongoDb;
    }

    public void setIfMongoDb(boolean ifMongoDb) {
        this.ifMongoDb = ifMongoDb;
    }

    public boolean isIfSpringData() {
        return ifSpringData;
    }

    public void setIfSpringData(boolean ifSpringData) {
        this.ifSpringData = ifSpringData;
    }

    public boolean isIfUsingImports() {
        return ifUsingImports;
    }

    public void setIfUsingImports(boolean ifUsingImports) {
        this.ifUsingImports = ifUsingImports;
    }

    public boolean isIfMarkLombokGenerated() {
        return ifMarkLombokGenerated;
    }

    public void setIfMarkLombokGenerated(boolean ifMarkLombokGenerated) {
        this.ifMarkLombokGenerated = ifMarkLombokGenerated;
    }
}
