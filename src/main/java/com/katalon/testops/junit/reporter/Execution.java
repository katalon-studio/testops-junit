package com.katalon.testops.junit.reporter;

import com.katalon.testops.commons.model.Status;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

public class Execution {
    private Description description;
    private Status status;
    private long start;
    private long end;
    private String uuid;
    private Failure failure;
    private Execution parent;

    public Execution(Description description, Execution testsuite) {
        this.description = description;
        this.start = System.currentTimeMillis();
        this.end = -1;
        this.status = Status.INCOMPLETE;
        this.parent = testsuite;
    }

    public Execution(Description description, String uuid) {
        this.description = description;
        this.uuid = uuid;
        this.start = System.currentTimeMillis();
        this.end = -1;
        this.status = Status.INCOMPLETE;
    }

    public String getMethodName() {
        return description.getMethodName();
    }

    public String getClassName() {
        return description.getTestClass().getName();
    }

    public void setParent(Execution parent) {
        this.parent = parent;
    }

    public Execution getParent() {
        return this.parent;
    }

    public void setFailure(Failure failure) {
        this.failure = failure;
    }

    public Failure getFailure() {
        return this.failure;
    }

    public String getUuid() {
        if (!isTestSuite()) {
            throw new UnsupportedOperationException("Not a TestSuite step");
        }
        return uuid;
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

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getDuration() {
        return this.end > 0 ? (this.end - this.start) : 0;
    }

}
