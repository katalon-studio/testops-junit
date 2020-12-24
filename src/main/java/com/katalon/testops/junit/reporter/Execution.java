package com.katalon.testops.junit.reporter;

import com.katalon.testops.commons.model.Status;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

public class Execution {
    private Description description;
    private Status status = Status.INCOMPLETE;
    private String uuid;
    private Failure failure;
    private Execution parent;
    private String ignoreMessage;

    public Execution(Description description, Execution testsuite) {
        this.description = description;
        this.parent = testsuite;
    }

    public Execution(Description description, String uuid) {
        this.description = description;
        this.uuid = uuid;
    }

    public String getMethodName() {
        return description.getMethodName();
    }

    public String getClassName() {
        return description.getTestClass().getName();
    }

    public Execution getParent() {
        return this.parent;
    }

    public void setParent(Execution parent) {
        this.parent = parent;
    }

    public Failure getFailure() {
        return this.failure;
    }

    public void setFailure(Failure failure) {
        this.failure = failure;
    }

    public String getUuid() {
        if (!isTestSuite()) {
            throw new UnsupportedOperationException("Not a TestSuite step");
        }
        return uuid;
    }

    public String getIgnoreMessage() {
        return ignoreMessage;
    }

    public void setIgnoreMessage(String ignoreMessage) {
        this.ignoreMessage = ignoreMessage;
    }

    public boolean isTestSuite() {
        return description.isSuite();
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
