package com.katalon.testops.junit.helper;

import com.katalon.testops.commons.helper.GeneratorHelper;
import com.katalon.testops.commons.model.Metadata;
import com.katalon.testops.commons.model.Status;
import com.katalon.testops.commons.model.TestResult;
import com.katalon.testops.junit.reporter.ExecutionTestResult;
import com.katalon.testops.junit.reporter.ReportListener;

import java.util.Objects;

public final class ReportHelper {

    private ReportHelper() {
        throw new IllegalStateException("Utility class");
    }

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
            if (executionTestResult.getStatus() == Status.SKIPPED && executionTestResult.getIgnoreMessage() != null) {
                testResult.addFailure(executionTestResult.getIgnoreMessage(), "");
            }
            executionTestResult.getFailures().forEach(failure -> {
                Throwable throwable = failure.getException();
                testResult.addFailure(throwable);
            });
            executionTestResult.getErrors().forEach(error -> {
                Throwable throwable = error.getException();
                testResult.addError(throwable);
            });
        }

        return testResult;
    }

}
