package com.katalon.testops.junit.reporter;

import com.katalon.testops.commons.ReportLifecycle;
import com.katalon.testops.commons.helper.GeneratorHelper;
import com.katalon.testops.commons.model.Status;
import com.katalon.testops.commons.model.TestResult;
import com.katalon.testops.commons.model.TestSuite;
import com.katalon.testops.junit.helper.LogHelper;
import com.katalon.testops.junit.helper.ReportHelper;
import org.junit.Ignore;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TestRunManager {

    private static final Logger logger = LogHelper.getLogger();

    private static final String NULL = "null";

    private final ReportLifecycle reportLifecycle;

    private final ConcurrentMap<String, ExecutionTestSuite> testSuites;

    private final ConcurrentMap<Description, ExecutionTestResult> testCases;

    public TestRunManager() {
        reportLifecycle = new ReportLifecycle();
        testSuites = new ConcurrentHashMap<>();
        testCases = new ConcurrentHashMap<>();
    }

    public void testRunStarted(Description description) {
        logger.info("testRunStarted");
        reportLifecycle.startExecution();
        reportLifecycle.writeMetadata(ReportHelper.createMetadata());
    }

    public void testSuiteStarted(Description description) {
        if (NULL.equals(description.getClassName())) {
            return;
        }
        logger.info("testSuiteStarted: " + description.getClassName());
        String uuid = GeneratorHelper.generateUniqueValue();
        testSuites.putIfAbsent(description.getClassName(), new ExecutionTestSuite(uuid));
        TestSuite testSuite = new TestSuite();
        testSuite.setName(description.getClassName());
        reportLifecycle.startSuite(testSuite, uuid);
    }

    public void testStarted(Description description) {
        logger.info("testStarted: " + description.getMethodName());
        testCases.putIfAbsent(description, new ExecutionTestResult(description, getTestSuite(description).orElse(null)));
        reportLifecycle.startTestCase();
    }

    public void testFailure(Failure failure) {
        getTestCase(failure.getDescription())
                .ifPresent(executionTestResult -> {
                    executionTestResult.setFailure(failure);
                    Status status = (failure.getException() instanceof AssertionError) ? Status.FAILED : Status.ERROR;
                    executionTestResult.setStatus(status);
                });
    }

    public void testAssumptionFailure(Failure failure) {
        getTestCase(failure.getDescription())
                .ifPresent(executionTestResult -> {
                    executionTestResult.setFailure(failure);
                    executionTestResult.setStatus(Status.SKIPPED);
                });
    }

    public void testFinished(Description description) {
        getTestCase(description)
                .ifPresent(executionTestResult -> {
                    testCases.remove(description);
                    Status status = executionTestResult.getStatus() == Status.INCOMPLETE ? Status.PASSED : Status.FAILED;
                    executionTestResult.setStatus(status);
                    stopTestCase(executionTestResult);
                });
    }

    public void testIgnored(Description description) {
        ExecutionTestResult executionTestResult = new ExecutionTestResult(description, getTestSuite(description).orElse(null));
        executionTestResult.setStatus(Status.SKIPPED);
        executionTestResult.setIgnoreMessage(getTestIgnoreMessage(description));
        stopTestCase(executionTestResult);
    }

    public void testSuiteFinished(Description description) {
        if (NULL.equals(description.getClassName())) {
            return;
        }
        logger.info("testSuiteFinished: " + description.getClassName());
        ExecutionTestSuite executionTestSuite = testSuites.get(description.getClassName());
        if (executionTestSuite != null) {
            String uuid = executionTestSuite.getUuid();
            reportLifecycle.stopTestSuite(uuid);
        }
        testSuites.remove(description.getClassName());
    }

    public void testRunFinished(Result result) {
        logger.info("testRunFinished");
        reportLifecycle.stopExecution();
        reportLifecycle.writeTestResultsReport();
        reportLifecycle.writeTestSuitesReport();
        reportLifecycle.writeExecutionReport();
        reportLifecycle.upload();
        cleanUp();
    }

    private void cleanUp() {
        reportLifecycle.reset();
        testSuites.clear();
        testCases.clear();
    }

    private Optional<ExecutionTestSuite> getTestSuite(Description description) {
        return Optional.ofNullable(testSuites.get(description.getTestClass().getName()));
    }

    private Optional<ExecutionTestResult> getTestCase(Description description) {
        return Optional.ofNullable(testCases.get(description));
    }

    private void stopTestCase(ExecutionTestResult executionTestResult) {
        TestResult testResult = ReportHelper.createTestResult(executionTestResult);
        reportLifecycle.stopTestCase(testResult);
    }

    private String getTestIgnoreMessage(Description description) {
        if (Objects.isNull(description)) {
            return "";
        }
        Ignore ignore = description.getAnnotation(Ignore.class);
        return Objects.nonNull(ignore) ? ignore.value() : "";
    }

}
