package com.katalon.testops.junit.reporter;

import com.katalon.testops.junit.helper.LogHelper;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;

public class ReportListener extends RunListener {

    private static final Logger logger = LogHelper.getLogger();

    private static final String TESTOPS_GENERAL_ERROR = "An error has occurred in step: %s";

    private final TestRunManager testRunManager;

    public ReportListener() {
        super();
        testRunManager = new TestRunManager();
    }

    @Override
    public void testRunStarted(Description description) {
        try {
            testRunManager.testRunStarted(description);
        } catch (Exception e) {
            logger.error(getErrorMessage("testRunStarted"), e);
        }
    }

    @Override
    public void testRunFinished(Result result) {
        try {
            testRunManager.testRunFinished(result);
        } catch (Exception e) {
            logger.error(getErrorMessage("testRunFinished"), e);
        }
    }

    @Override
    public void testSuiteStarted(Description description) {
        try {
            testRunManager.testSuiteStarted(description);
        } catch (Exception e) {
            logger.error(getErrorMessage(getStepName("testSuiteStarted", description)), e);
        }
    }

    @Override
    public void testSuiteFinished(Description description) {
        try {
            testRunManager.testSuiteFinished(description);
        } catch (Exception e) {
            logger.error(getErrorMessage(getStepName("testSuiteFinished", description)), e);
        }
    }

    @Override
    public void testStarted(Description description) {
        try {
            testRunManager.testStarted(description);
        } catch (Exception e) {
            logger.error(getErrorMessage(getStepName("testStarted", description)), e);
        }
    }

    @Override
    public void testFinished(Description description) {
        try {
            testRunManager.testFinished(description);
        } catch (Exception e) {
            logger.error(getErrorMessage(getStepName("testFinished", description)), e);
        }
    }

    @Override
    public void testFailure(Failure failure) {
        try {
            testRunManager.testFailure(failure);
        } catch (Exception e) {
            logger.error(getErrorMessage(getStepName("testFailure", failure.getDescription())), e);
        }
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        try {
            testRunManager.testAssumptionFailure(failure);
        } catch (Exception e) {
            logger.error(getErrorMessage(getStepName("testAssumptionFailure", failure.getDescription())), e);
        }
    }

    @Override
    public void testIgnored(Description description) {
        try {
            testRunManager.testIgnored(description);
        } catch (Exception e) {
            logger.error(getErrorMessage(getStepName("testIgnored", description)), e);
        }
    }

    private String getErrorMessage(String step) {
        return String.format(TESTOPS_GENERAL_ERROR, step);
    }

    private String getStepName(String name, Description description) {
        String stepName = description.getClassName();
        if (description.isTest()) {
            stepName += "." + description.getMethodName();
        }
        return name + ": " + stepName;
    }

}
