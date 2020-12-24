package com.katalon.testops.junit.reporter;

import com.katalon.testops.commons.ReportLifecycle;
import com.katalon.testops.commons.helper.GeneratorHelper;
import com.katalon.testops.commons.model.Status;
import com.katalon.testops.commons.model.TestResult;
import com.katalon.testops.commons.model.TestSuite;
import com.katalon.testops.junit.helper.LogHelper;
import com.katalon.testops.junit.helper.ReportHelper;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TestRunManager {

    private static final Logger logger = LogHelper.getLogger();

    private static final String NULL = "null";

    private final ReportLifecycle reportLifecycle;

    private final ConcurrentMap<String, Execution> testSuites;

    private final ConcurrentMap<Description, Execution> testCases;

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
        testSuites.putIfAbsent(description.getClassName(), new Execution(description, uuid));
        TestSuite testSuite = new TestSuite();
        testSuite.setName(description.getClassName());
        reportLifecycle.startSuite(testSuite, uuid);
    }

    public void testStarted(Description description) {
        logger.info("testStarted: " + description.getMethodName());
        testCases.putIfAbsent(description, new Execution(description, getTestSuite(description).orElse(null)));
    }

    public void testFailure(Failure failure) {
        getTestCase(failure.getDescription())
                .ifPresent(execution -> {
                    execution.setFailure(failure);
                    execution.setStatus(Status.FAILED);
                });
    }

    public void testAssumptionFailure(Failure failure) {
        getTestCase(failure.getDescription())
                .ifPresent(execution -> {
                    execution.setFailure(failure);
                    execution.setStatus(Status.FAILED);
                });
    }

    public void testFinished(Description description) {
        getTestCase(description)
                .ifPresent(execution -> {
                    testCases.remove(description);
                    Status status = execution.getStatus() == Status.INCOMPLETE ? Status.PASSED : Status.FAILED;
                    execution.setStatus(status);
                    execution.setEnd(System.currentTimeMillis());
                    stopTestCase(execution);
                });
    }

    public void testIgnored(Description description) throws Exception {
        Execution execution = new Execution(description, getTestSuite(description).orElse(null));
        execution.setStatus(Status.SKIPPED);
        stopTestCase(execution);
    }

    public void testSuiteFinished(Description description) {
        if (NULL.equals(description.getClassName())) {
            return;
        }
        logger.info("testSuiteFinished: " + description.getClassName());
        Execution execution = testSuites.getOrDefault(description.getClassName(), null);
        String uuid = null;
        if (execution != null) {
            uuid = execution.getUuid();
        }
        if (uuid == null) {
            uuid = GeneratorHelper.generateUniqueValue();
        }
        testSuites.remove(description.getClassName());
        reportLifecycle.stopTestSuite(uuid);
    }

    public void testRunFinished(Result result) {
        logger.info("testRunFinished");
        reportLifecycle.stopExecution();
        reportLifecycle.writeTestResultsReport();
        reportLifecycle.writeTestSuitesReport();
        reportLifecycle.writeExecutionReport();
//        reportLifecycle.upload();
        cleanUp();
    }

    private void cleanUp() {
        reportLifecycle.reset();
        testSuites.clear();
        testCases.clear();
        ;
    }

    private Optional<Execution> getTestSuite(Description description) {
        return Optional.ofNullable(testSuites.get(description.getTestClass().getName()));
    }

    private Optional<Execution> getTestCase(Description description) {
        return Optional.ofNullable(testCases.get(description));
    }

    private void stopTestCase(Execution execution) {
        TestResult testResult = ReportHelper.createTestResult(execution);
        reportLifecycle.stopTestCase(testResult);
    }
}
