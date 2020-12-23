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

import java.util.Optional;
import java.util.Stack;

public class TestRunManager {

    Stack<Execution> running;
    ReportLifecycle reportLifecycle;

    public TestRunManager() {
        running = new Stack<>();
        reportLifecycle = new ReportLifecycle();
    }

    public Execution endStep(Description description) {
        if (running.peek().getDescription() == description) {
            return running.pop();
        }
        return null;
    }

    public Execution getTestStep(Description description) {
        if (running.empty()) {
            return null;
        }
        Execution latest = running.peek();
        if (latest.getDescription() == description && latest.isTestSuite()) {
            return  running.peek();
        }
        Optional<Execution> rel = running.stream().filter(s -> s.getDescription() == description && s.isTestSuite()).findFirst();
        return rel.isPresent() ? rel.get() : null;
    }

    public void testRunStarted(Description description) throws Exception {
        System.out.println("testRunStarted");
        reportLifecycle.startExecution();
        reportLifecycle.writeMetadata(ReportHelper.createMetadata());
    }

    public void testRunFinished(Result result) throws Exception {
        System.out.println("testRunFinished");
        running.clear();
        reportLifecycle.stopExecution();
        reportLifecycle.writeTestResultsReport();
        reportLifecycle.writeTestSuitesReport();
        reportLifecycle.writeExecutionReport();
//        reportLifecycle.upload();
    }

    public void testSuiteStarted(Description description) throws Exception {
        System.out.println("testSuiteStarted: " + description.getDisplayName());
        String uuid = GeneratorHelper.generateUniqueValue();
        running.push(new Execution(description, uuid));
        TestSuite testSuite = new TestSuite();
        testSuite.setName(description.getDisplayName());
        reportLifecycle.startSuite(testSuite, uuid);
    }

    public void testSuiteFinished(Description description) throws Exception {
        System.out.println("testSuiteFinished: " + description.getDisplayName());
        Execution step = getTestStep(description);
        String uuid = null;
        if (step != null) {
            step.setEnd(System.currentTimeMillis());
            uuid = step.getTestSuiteUUID();
        }
        if (uuid == null) {
            uuid = GeneratorHelper.generateUniqueValue();
        }
        endStep(description);
        reportLifecycle.stopTestSuite(uuid);
    }

    public void testStarted(Description description) throws Exception {
        System.out.println("testStarted: " + description.getMethodName());
        running.push(new Execution(description, running.peek()));
    }

    public void testFinished(Description description) throws Exception {
        Execution step = getTestStep(description);
        endStep(description);
        if (step == null) {
            return;
        }
        step.setEnd(System.currentTimeMillis());
        Status status = step.getFailure() != null ? Status.PASSED : Status.FAILED;
        step.setStatus(status);
        TestResult testResult = ReportHelper.createTestResult(step);
        reportLifecycle.stopTestCase(testResult);
    }

    public void testFailure(Failure failure) throws Exception {
        Execution step = getTestStep(failure.getDescription());
        if (step == null) {
            return;
        }
        step.setFailure(failure);
        step.setStatus(Status.FAILED);
    }

    public void testAssumptionFailure(Failure failure) {
        Execution step = getTestStep(failure.getDescription());
        if (step == null) {
            return;
        }
        step.setFailure(failure);
        step.setStatus(Status.FAILED);
    }

    public void testIgnored(Description description) throws Exception {
        Execution step = new Execution(description, running.peek());
        TestResult testResult = ReportHelper.createTestResult(step);
        testResult.setStatus(Status.SKIPPED);
        reportLifecycle.stopTestCase(testResult);
    }
}
