package com.katalon.testops.junit.reporter;

import com.katalon.testops.commons.model.Status;
import com.katalon.testops.commons.model.WithUuid;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

import java.util.ArrayList;
import java.util.List;

public class ExecutionTestResult {

    private final Description description;

    private final WithUuid parent;

    private Status status = Status.INCOMPLETE;

    private List<Failure> failures = new ArrayList<>();

    private List<Failure> errors = new ArrayList<>();

    private String ignoreMessage;

    public ExecutionTestResult(Description description, WithUuid parent) {
        this.description = description;
        this.parent = parent;
    }

    public String getTestCaseName() {
        return getClassName() + "." + getMethodName();
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

    public List<Failure> getFailures() {
        return failures;
    }

    public void setFailures(List<Failure> failures) {
        this.failures = failures;
    }

    public List<Failure> getErrors() {
        return errors;
    }

    public void setErrors(List<Failure> errors) {
        this.errors = errors;
    }

    public String getIgnoreMessage() {
        return ignoreMessage;
    }

    public void setIgnoreMessage(String ignoreMessage) {
        this.ignoreMessage = ignoreMessage;
    }

    public void addError(Failure error) {
        errors.add(error);
    }

    public void addFailure(Failure failure) {
        failures.add(failure);
    }
}
