package com.katalon.testops.utils;

import com.katalon.testops.commons.helper.GeneratorHelper;
import com.katalon.testops.commons.model.Metadata;
import com.katalon.testops.commons.model.Status;
import com.katalon.testops.commons.model.TestResult;
import com.katalon.testops.junit.reporter.ReportListener;
import com.katalon.testops.junit.reporter.Execution;
import org.junit.runner.notification.Failure;

import static com.katalon.testops.commons.helper.StringHelper.getErrorMessage;
import static com.katalon.testops.commons.helper.StringHelper.getStackTraceAsString;

public final class ReportHelper {

    public static Metadata createMetadata() {
        Metadata metadata = new Metadata();
        metadata.setFramework("junit4");
        metadata.setLanguage("java");
        metadata.setVersion(ReportListener.class.getPackage().getImplementationVersion());
        return metadata;
    }

    public static TestResult createTestResult(Execution step) {
        String uuid = GeneratorHelper.generateUniqueValue();

        TestResult testResult = new TestResult();
        testResult.setStatus(step.getStatus());
        testResult.setUuid(uuid);
        testResult.setName(step.getMethodName());
        testResult.setSuiteName(step.getTestsuite().getClassName());
        testResult.setParentUuid(step.getTestsuite().getTestSuiteUUID());

        if (step.getStatus() != Status.PASSED && step.getStatus() != Status.SKIPPED) {
            Failure failure = step.getFailure();
            if (failure != null) {
                Throwable throwable = failure.getException();
                testResult.setErrorMessage(getErrorMessage(throwable));
                testResult.setStackTrace(getStackTraceAsString(throwable));
            }
        }
        testResult.setParameters(null);

        testResult.setStart(step.getStart());
        testResult.setStop(step.getEnd());
        testResult.setDuration(step.getDuration());
        return testResult;
    }

}
