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
    private static final Map<String,String> expectedSliceInfos = new HashMap();
    private static final Map<String, String> expectedStudyInfos = new HashMap();
    
    private static final File dcmFile = new File("test/resources/dicomTest2.dcm");
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
        
        expectedSliceInfos.put(DicomTags.BitsAllocated.getName(), "16");
        expectedSliceInfos.put(DicomTags.BitsStored.getName(), "12");
        expectedSliceInfos.put(DicomTags.HighBit.getName(), "11");
        expectedSliceInfos.put(DicomTags.RescaleIntercept.getName(), "-1024");
        expectedSliceInfos.put(DicomTags.RescaleSlope.getName(), "1");
        expectedSliceInfos.put(DicomTags.SliceThickness.getName(), "1");
        expectedSliceInfos.put(DicomTags.WindowCenter.getName(), "50");
        expectedSliceInfos.put(DicomTags.WindowCenterWidthExplanation.getName(), "WINDOW1");
        expectedSliceInfos.put(DicomTags.WindowWidth.getName(), "350");
        
        expectedStudyInfos.put(DicomTags.StudyDate.getName(), "20030522");
        expectedStudyInfos.put(DicomTags.StudyDescription.getName(), "Thorax^1-THORAX C-");
        expectedStudyInfos.put(DicomTags.StudyID.getName(), "1");
        expectedStudyInfos.put(DicomTags.StudyTime.getName(), "115643.750000");
        expectedStudyInfos.put(DicomTags.ImageOrientationPatient.getName(), "1");
        expectedStudyInfos.put(DicomTags.ImagePositionPatient.getName(), "-175.64258");
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
    public void testSliceInfos() {
        assertTrue(provider.read(dcmFile));
        Map<String,String> map = provider.getSliceInfos();
        for (String key : expectedSliceInfos.keySet()) {
            assertEquals(expectedSliceInfos.get(key), map.get(key));
        }
    }
    
    @Test
    public void testPatientInfos() {
        assertTrue(provider.read(dcmFile));
        Map<String, String> infos = provider.getPatientInfos();
        for (String key : expectedPatientInfos.keySet()) {
            assertEquals(expectedPatientInfos.get(key), infos.get(key));
        }
    }

    @Test
    public void testStudyInfos() {
        assertTrue(provider.read(dcmFile));
        Map<String,String> infos = provider.getStudyInfos();
        for (String key : expectedStudyInfos.keySet()) {
            assertEquals(expectedStudyInfos.get(key), infos.get(key));
        }
    }
}
