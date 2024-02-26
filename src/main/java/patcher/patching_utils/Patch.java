package patcher.patching_utils;

import javafx.beans.property.SimpleStringProperty;

public class Patch {
    private SimpleStringProperty patchDate;
    private SimpleStringProperty versionFrom;
    private SimpleStringProperty versionTo;
    private SimpleStringProperty message;

    public Patch(String patchDate, String versionFrom, String versionTo, String message) {
        this.patchDate = new SimpleStringProperty(patchDate);
        this.versionFrom = new SimpleStringProperty(versionFrom);
        this.versionTo = new SimpleStringProperty(versionTo);
        this.message = new SimpleStringProperty(message);
    }

    public String getPatchDate() {
        return patchDate.get();
    }
    public void setPatchDate(String value) {
        patchDate.set(value);
    }

    public String getVersionFrom() {
        return versionFrom.get();
    }
    public void setVersionFrom(String value) {
        versionFrom.set(value);
    }

    public String getVersionTo() {
        return versionTo.get();
    }
    public void setVersionTo(String value) {
        versionTo.set(value);
    }

    public String getMessage() {
        return message.get();
    }
    public void setMessage(String value) {
        message.set(value);
    }
}
