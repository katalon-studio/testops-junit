package com.katalon.testops.utils;

import com.katalon.testops.commons.helper.GeneratorHelper;
import com.katalon.testops.commons.model.Metadata;
import com.katalon.testops.commons.model.Status;
import com.katalon.testops.commons.model.TestResult;
import com.katalon.testops.junit.reporter.ReportListener;
import com.katalon.testops.junit.reporter.Execution;
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

    public static TestResult createTestResult(Execution execution) {
        if (Objects.isNull(execution.getParent())) {
            throw new RuntimeException("TestSuite not found");
        }
        String uuid = GeneratorHelper.generateUniqueValue();

        TestResult testResult = new TestResult();
        testResult.setStatus(execution.getStatus());
        testResult.setUuid(uuid);
        testResult.setName(execution.getMethodName());
        testResult.setSuiteName(execution.getClassName());
        testResult.setParentUuid(execution.getParent().getUuid());

        if (execution.getStatus() != Status.PASSED && execution.getStatus() != Status.SKIPPED) {
            Failure failure = execution.getFailure();
            if (failure != null) {
                Throwable throwable = failure.getException();
                testResult.setErrorMessage(getErrorMessage(throwable));
                testResult.setStackTrace(getStackTraceAsString(throwable));
            }
        }
        testResult.setParameters(null);

        testResult.setStart(execution.getStart());
        testResult.setStop(execution.getEnd());
        testResult.setDuration(execution.getDuration());
        return testResult;
    }

}
