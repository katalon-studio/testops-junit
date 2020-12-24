package com.katalon.testops.junit.reporter;

import com.katalon.testops.commons.ReportLifecycle;
import com.katalon.testops.commons.helper.GeneratorHelper;
import com.katalon.testops.commons.model.Status;
import com.katalon.testops.commons.model.TestResult;
import com.katalon.testops.commons.model.TestSuite;
import com.katalon.testops.utils.ReportHelper;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TestRunManager {

    private static final String NULL = "null";

    ReportLifecycle reportLifecycle;
    ConcurrentMap<String, Execution> testSuites;
    ConcurrentMap<Description, Execution> testCases;

    public TestRunManager() {
        reportLifecycle = new ReportLifecycle();
        testSuites = new ConcurrentHashMap<>();
        testCases = new ConcurrentHashMap<>();
    }

    public void testRunStarted(Description description) throws Exception {
        System.out.println("testRunStarted");
        reportLifecycle.startExecution();
        reportLifecycle.writeMetadata(ReportHelper.createMetadata());
    }

    public void testRunFinished(Result result) throws Exception {
        System.out.println("testRunFinished");
        reportLifecycle.stopExecution();
        reportLifecycle.writeTestResultsReport();
        reportLifecycle.writeTestSuitesReport();
        reportLifecycle.writeExecutionReport();
//        reportLifecycle.upload();
        testSuites.clear();
        testCases.clear();
    }

    public void testSuiteStarted(Description description) throws Exception {
        if (NULL.equals(description.getClassName())) {
            return;
        }
        System.out.println("testSuiteStarted: " + description.getClassName());
        String uuid = GeneratorHelper.generateUniqueValue();
        testSuites.putIfAbsent(description.getClassName(), new Execution(description, uuid));
        TestSuite testSuite = new TestSuite();
        testSuite.setName(description.getClassName());
        reportLifecycle.startSuite(testSuite, uuid);
    }

    public void testSuiteFinished(Description description) throws Exception {
        if (NULL.equals(description.getClassName())) {
            return;
        }
        System.out.println("testSuiteFinished: " + description.getClassName());
        Execution execution = testSuites.getOrDefault(description.getClassName(), null);
        String uuid = null;
        if (execution != null) {
            execution.setEnd(System.currentTimeMillis());
            uuid = execution.getUuid();
        }
        if (uuid == null) {
            uuid = GeneratorHelper.generateUniqueValue();
        }
        testSuites.remove(description.getClassName());
        reportLifecycle.stopTestSuite(uuid);
    }

    public void testStarted(Description description) throws Exception {
        System.out.println("testStarted: " + description.getMethodName());
        testCases.putIfAbsent(description, new Execution(description, testSuites.getOrDefault(description.getTestClass().getName(), null)));
    }

    public void testFinished(Description description) throws Exception {
        Execution execution = testCases.getOrDefault(description, null);
        testCases.remove(description);
        if (execution == null) {
            return;
        }
        execution.setEnd(System.currentTimeMillis());
        Status status = execution.getStatus() == Status.INCOMPLETE ? Status.PASSED : Status.FAILED;
        execution.setStatus(status);
        TestResult testResult = ReportHelper.createTestResult(execution);
        reportLifecycle.stopTestCase(testResult);
    }

    public void testFailure(Failure failure) throws Exception {
        Execution execution = testCases.getOrDefault(failure.getDescription(), null);
        if (execution == null) {
            return;
        }
        execution.setFailure(failure);
        execution.setStatus(Status.FAILED);
    }

    public void testAssumptionFailure(Failure failure) {
        Execution execution = testCases.getOrDefault(failure.getDescription(), null);
        if (execution == null) {
            return;
        }
        execution.setFailure(failure);
        execution.setStatus(Status.FAILED);
    }

    public void testIgnored(Description description) throws Exception {
        Execution execution = new Execution(description, testSuites.getOrDefault(description.getTestClass().getName(), null));
        TestResult testResult = ReportHelper.createTestResult(execution);
        testResult.setStatus(Status.SKIPPED);
        reportLifecycle.stopTestCase(testResult);
    }
}
