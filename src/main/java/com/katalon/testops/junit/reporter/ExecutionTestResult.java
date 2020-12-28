package com.katalon.testops.junit.reporter;

import com.katalon.testops.commons.model.Status;
import com.katalon.testops.commons.model.WithUuid;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

public class ExecutionTestResult {

    private final Description description;

    private final WithUuid parent;

    private Status status = Status.INCOMPLETE;

    private Failure failure;

    private String ignoreMessage;

    public ExecutionTestResult(Description description, WithUuid parent) {
        this.description = description;
        this.parent = parent;
    }

    public String getMethodName() {
        return description.getMethodName();
    }

    public String getClassName() {
        return description.getTestClass().getName();
    }

    public String getParentUuid() {
        if (parent == null) {
            return null;
        }
        return parent.getUuid();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Failure getFailure() {
        return failure;
    }

    public void setFailure(Failure failure) {
        this.failure = failure;
    }

    public String getIgnoreMessage() {
        return ignoreMessage;
    }

    public void setIgnoreMessage(String ignoreMessage) {
        this.ignoreMessage = ignoreMessage;
    }
}
