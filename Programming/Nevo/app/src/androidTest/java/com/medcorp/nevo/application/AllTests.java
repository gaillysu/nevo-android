package com.medcorp.nevo.application;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;
import junit.framework.TestSuite;
import android.test.suitebuilder.TestSuiteBuilder;

/**
 * Created by karl-john on 30/11/15.
 */
public class AllTests extends TestSuite {
    public static Test suite(){
        return new TestSuiteBuilder(AllTests.class).includeAllPackagesUnderHere().build();
    }
}
