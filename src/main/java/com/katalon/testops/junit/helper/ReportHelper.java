package com.katalon.testops.junit.helper;

import com.katalon.testops.commons.helper.GeneratorHelper;
import com.katalon.testops.commons.model.Metadata;
import com.katalon.testops.commons.model.Status;
import com.katalon.testops.commons.model.TestResult;
import com.katalon.testops.junit.reporter.ExecutionTestResult;
import com.katalon.testops.junit.reporter.ReportListener;
import org.junit.runner.notification.Failure;

import java.util.Objects;

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

    public static TestResult createTestResult(ExecutionTestResult executionTestResult) {
        String uuid = GeneratorHelper.generateUniqueValue();

        TestResult testResult = new TestResult();
        testResult.setStatus(executionTestResult.getStatus());
        testResult.setUuid(uuid);
        testResult.setName(executionTestResult.getTestCaseName());
        testResult.setSuiteName(executionTestResult.getClassName());
        if (Objects.nonNull(executionTestResult.getParentUuid())) {
            testResult.setParentUuid(executionTestResult.getParentUuid());
        }

        if (executionTestResult.getStatus() != Status.PASSED) {
            Failure failure = executionTestResult.getFailure();
            if (failure != null) {
                Throwable throwable = failure.getException();
                testResult.setErrorMessage(getErrorMessage(throwable));
                testResult.setStackTrace(getStackTraceAsString(throwable));
            } else if (executionTestResult.getStatus() == Status.SKIPPED) {
                testResult.setErrorMessage(executionTestResult.getIgnoreMessage());
            }
        }

        return testResult;
    }

}
