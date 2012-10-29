package org.bdx1.diams.caching;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import java.io.File;

import org.bdx1.diams.Factory;
import org.bdx1.diams.caching.SliceCacher;
import org.bdx1.diams.model.Slice;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SliceCacherTest {

    private static final File dicom = new File("test/resources/dicomTest1.dcm");
    private static final int cacheSize = 5;
    
    private SliceCacher cacher;
    private Slice expected;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        cacher = new SliceCacher(cacheSize);
        expected = Factory.MODEL_FACTORY.makeSlice(dicom);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetSlice() {
        Slice result = cacher.getSlice(dicom);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    public void testChargeInCache() {
        cacher.chargeInCache(dicom);
        Slice result = cacher.getSlice(dicom);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    public void testSameSlice() {
        Slice r1 = cacher.getSlice(dicom);
        Slice r2 = cacher.getSlice(dicom);
        assertSame(r1, r2);
    }
    
    @Test
    public void testNoRedundancy() {
        assertEquals(0, cacher.size());
        cacher.chargeInCache(dicom);
        assertEquals(1, cacher.size());
        cacher.chargeInCache(dicom);
        assertEquals(1, cacher.size());
    }
    
}
