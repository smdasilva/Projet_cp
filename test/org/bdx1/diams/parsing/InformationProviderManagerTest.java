package org.bdx1.diams.parsing;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class InformationProviderManagerTest {
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetDicomProvider() {
        InformationProvider dicomProvider = InformationProviderManager.getDicomProvider();
        assertTrue(dicomProvider instanceof DicomInfosProvider);
    }

}
