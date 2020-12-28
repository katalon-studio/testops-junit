package com.katalon.testops.junit.reporter;

import com.katalon.testops.commons.model.WithUuid;

public class ExecutionTestSuite implements WithUuid {

    private final String uuid;

    public ExecutionTestSuite(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public String getParentUuid() {
        throw new UnsupportedOperationException("Not applicable");
    }
}
