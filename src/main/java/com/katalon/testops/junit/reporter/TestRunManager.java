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

import java.util.HashMap;
import java.util.Map;

public class TestRunManager {

    private static final String NULL = "null";

    ReportLifecycle reportLifecycle;
    Map<String, Execution> testSuites;
    Map<Description, Execution> testCases;

    public TestRunManager() {
        reportLifecycle = new ReportLifecycle();
        testSuites = new HashMap<>();
        testCases = new HashMap<>();
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
        testSuites.put(description.getClassName(), new Execution(description, uuid));
        TestSuite testSuite = new TestSuite();
        testSuite.setName(description.getClassName());
        reportLifecycle.startSuite(testSuite, uuid);
    }

    public void testSuiteFinished(Description description) throws Exception {
        if (NULL.equals(description.getClassName())) {
            return;
        }
        System.out.println("testSuiteFinished: " + description.getClassName());
        Execution execution = testSuites.get(description.getClassName());
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
        testCases.put(description, new Execution(description, testSuites.get(description.getTestClass().getName())));
    }

    public void testFinished(Description description) throws Exception {
        Execution execution = testCases.get(description);
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
        Execution execution = testCases.get(failure.getDescription());
        if (execution == null) {
            return;
        }
        execution.setFailure(failure);
        execution.setStatus(Status.FAILED);
    }

    public void testAssumptionFailure(Failure failure) {
        Execution execution = testCases.get(failure.getDescription());
        if (execution == null) {
            return;
        }
        execution.setFailure(failure);
        execution.setStatus(Status.FAILED);
    }

    public void testIgnored(Description description) throws Exception {
        Execution execution = new Execution(description, testSuites.get(description.getTestClass().getName()));
        TestResult testResult = ReportHelper.createTestResult(execution);
        testResult.setStatus(Status.SKIPPED);
        reportLifecycle.stopTestCase(testResult);
    }
}
