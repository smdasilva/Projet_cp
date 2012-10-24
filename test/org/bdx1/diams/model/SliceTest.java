package org.bdx1.diams.model;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Map;

import org.bdx1.diams.parsing.InformationProvider;
import org.bdx1.diams.parsing.InformationProviderManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SliceTest {

    private static final File dicom = new File("test/resources/dicomTest1.dcm");
    
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
    public void testGetInfos() {
        Slice s = new Slice(dicom);
        InformationProvider prov = InformationProviderManager.getDicomProvider();
        prov.read(dicom);
        Map<String, String> expectedInfos = prov.getSliceInfos();
        Map<String, String> sliceInfos = s.getInfos();
        for (String key : expectedInfos.keySet()) {
            assertEquals(expectedInfos.get(key), sliceInfos.get(key));
        }
    }

}
