package com.xenoamess.java_pojo_generator;

public class JavaCodeBakeProperties {

    private String outputFolder = "";
    private String packageName = "";
    private boolean ifBeautify = true;
    private boolean ifLombok = true;
    private boolean ifMongoDb = true;
    private boolean ifSpringData = true;

    public JavaCodeBakeProperties() {
    }

    public JavaCodeBakeProperties(String outputFolder, String packageName) {
        this.outputFolder = outputFolder;
        this.packageName = packageName;
    }

    public JavaCodeBakeProperties(
            String outputFolder,
            String packageName,
            boolean ifBeautify,
            boolean ifLombok,
            boolean ifMongoDb,
            boolean ifSpringData
    ) {
        this.outputFolder = outputFolder;
        this.packageName = packageName;
        this.ifBeautify = ifBeautify;
        this.ifLombok = ifLombok;
        this.ifMongoDb = ifMongoDb;
        this.ifSpringData = ifSpringData;
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
}
