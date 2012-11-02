package org.bdx1.diams.parsing;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ImageProviderTest {

    private static final File dicom = new File("test/resources/dicomTest1.dcm");
    private ImageProvider provider;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        provider = new DicomImageProvider();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testRead() {
        assertTrue(provider.read(dicom));
    }
    
    @Test
    public void testGetImage() {
        provider.read(dicom);
        assertNotNull(provider.getImage());
    }

}
