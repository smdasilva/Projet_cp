package org.bdx1.diams.parsing;

import static org.junit.Assert.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class InformationProviderTest {

    private static final Map<String,String> expectedPatientInfos = new HashMap();
    
    private static final File dcmFile = new File("test/resources/1.3.12.2.1107.5.1.4.50152.4.0.9805533212151202 copie.dcm");
    private InformationProvider provider;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        expectedPatientInfos.put(DicomTags.PatientName.getName(), "FRETILLERE^MARC");
        expectedPatientInfos.put(DicomTags.PatientBirthDate.getName(), "19421029");
        expectedPatientInfos.put(DicomTags.PatientAge.getName(), "060Y");
        expectedPatientInfos.put(DicomTags.PatientID.getName(), "03601135");
        expectedPatientInfos.put(DicomTags.PatientSex.getName(), "M");
        expectedPatientInfos.put(DicomTags.PatientSize.getName(), "null");
        expectedPatientInfos.put(DicomTags.PatientWeight.getName(), "null");
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        provider = new DicomInfosProvider();
    }

    @After
    public void tearDown() throws Exception {
        provider = null;
    }

    @Test
    public void testPatientInfos() {
        assertTrue(provider.read(dcmFile));
        Map<String, String> infos = provider.getPatientInfos();
        for (String key : expectedPatientInfos.keySet()) {
            assertEquals(expectedPatientInfos.get(key), infos.get(key));
        }
    }

}
