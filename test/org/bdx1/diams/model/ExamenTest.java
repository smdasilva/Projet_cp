package org.bdx1.diams.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bdx1.diams.parsing.InformationProvider;
import org.bdx1.diams.parsing.InformationProviderManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ExamenTest {

    private static final File studyFile = new File("test/resources/dicomTest1.dcm");
    private static final File secondSliceFile = new File("test/resources/dicomTest2.dcm");
    
    private InformationProvider provider;
    private Examen examen;
    private SliceManager mockManager;
    
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
 
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        mockManager = new SliceManager() {
            List<Slice> slices = new ArrayList<Slice>();
            public int numberOfSlices() {
                return slices.size();
            }
            
            public Slice getSlice(int i) {
                return slices.get(i);
            }
            
            public void addSlice(File source) {
                slices.add(ModelFactory.makeSlice(source));
            }
        };
        examen = new Examen(studyFile, mockManager);
        provider = InformationProviderManager.getDicomProvider();
        provider.read(studyFile);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetPatientInfos() {
        Map<String, String> expectedPatientInfos = provider.getPatientInfos();
        Map<String,String> patientInfos = examen.getPatientInfos();
        assertNotNull(patientInfos);
        for (String key : expectedPatientInfos.keySet()) {
            assertEquals(expectedPatientInfos.get(key), patientInfos.get(key));
        }
    }

    @Test
    public void testGetStudyInfos() {
        Map<String, String> expectedStudyInfos = provider.getStudyInfos();
        Map<String,String> studyInfos = examen.getStudyInfos();
        assertNotNull(studyInfos);
        for (String key : expectedStudyInfos.keySet()) {
            assertEquals(expectedStudyInfos.get(key), studyInfos.get(key));
        }
    }
    
    @Test
    public void testNumberOfSlices() {
        assertEquals(1, examen.getNumberOfSlices());
        examen.addSlice(secondSliceFile);
        assertEquals(2, examen.getNumberOfSlices());
    }
    
    @Test
    public void testGetSlice() {
        Slice firstSlice = examen.getSlice(0);
        assertNotNull(firstSlice);
    }

}
