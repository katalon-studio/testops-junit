package com.katalon.testops.junit.reporter;

import com.katalon.testops.junit.helper.LogHelper;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;

public class ReportListener extends RunListener {

    private static final Logger logger = LogHelper.getLogger();

    private TestRunManager testRunManager;

    public ReportListener() {
        super();
        testRunManager = new TestRunManager();
    }

    @Override
    public void testRunStarted(Description description) {
        tryCatch(() -> {
            testRunManager.testRunStarted(description);
        });
    }

    @Override
    public void testRunFinished(Result result) {
        tryCatch(() -> {
            testRunManager.testRunFinished(result);
        });
    }

    @Override
    public void testSuiteStarted(Description description) {
        tryCatch(() -> {
            testRunManager.testSuiteStarted(description);
        });
    }

    @Override
    public void testSuiteFinished(Description description) {
        tryCatch(() -> {
            testRunManager.testSuiteFinished(description);
        });
    }

    @Override
    public void testStarted(Description description) {
        tryCatch(() -> {
            testRunManager.testStarted(description);
        });
    }

    @Override
    public void testFinished(Description description) {
        tryCatch(() -> {
            testRunManager.testFinished(description);
        });
    }

    @Override
    public void testFailure(Failure failure) {
        tryCatch(() -> {
            testRunManager.testFailure(failure);
        });
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        tryCatch(() -> {
            testRunManager.testAssumptionFailure(failure);
        });
    }

    @Override
    public void testIgnored(Description description) {
        tryCatch(() -> {
            testRunManager.testIgnored(description);
        });
    }

    private void tryCatch(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            logger.error("An error has occurred in TestOps Reporter", e);
        }
    }

}
