package org.bdx1.diams.parsing;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Map;

import org.bdx1.diams.parsing.DicomInfosProvider;
import org.bdx1.diams.parsing.InformationProvider;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class InformationProviderTest {

    private static final File dcmFile = new File("resources/1.3.12.2.1107.5.1.4.50152.4.0.9805535011461839 copie.dcm");
    private InformationProvider provider;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        provider = new DicomInfosProvider(dcmFile);
    }

    @After
    public void tearDown() throws Exception {
        provider = null;
    }

    @Test
    public void testPatientInfos() {
        Map<String, String> infos = provider.getPatientInfos();
        assertNotNull(infos);
    }

}
