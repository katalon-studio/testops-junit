package com.katalon.testops.junit.reporter;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class ReportListener extends RunListener {

    TestRunManager testRunManager;

    public ReportListener() {
        super();
        testRunManager = new TestRunManager();
    }

    @Override
    public void testRunStarted(Description description) throws Exception {
        testRunManager.testRunStarted(description);
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        testRunManager.testRunFinished(result);
    }

    @Override
    public void testSuiteStarted(Description description) throws Exception {
        testRunManager.testSuiteStarted(description);
    }

    @Override
    public void testSuiteFinished(Description description) throws Exception {
        testRunManager.testSuiteFinished(description);
    }

    @Override
    public void testStarted(Description description) throws Exception {
        testRunManager.testStarted(description);
    }

    @Override
    public void testFinished(Description description) throws Exception {
        testRunManager.testFinished(description);
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        testRunManager.testFailure(failure);
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        testRunManager.testAssumptionFailure(failure);
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        testRunManager.testIgnored(description);
    }

}
