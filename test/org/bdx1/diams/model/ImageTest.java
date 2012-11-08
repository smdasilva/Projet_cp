package org.bdx1.diams.model;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ImageTest {

    private static final File dicom = new File("test/resources/dicomTest1.dcm");
    private Image img;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        img = DefaultModelFactory.makeImage(dicom);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testNotNull() {
        assertNotNull(img);
    }
    
    @Test
    public void testSize() {
        assertEquals(512, img.getHeight());
        assertEquals(512, img.getWidth());
    }

}
