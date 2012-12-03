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

    private static final File dicom1 = new File("test/resources/dicomTest1.dcm");
    private static final File dicom2 = new File("test/resources/dicomTest2.dcm");
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
        expected = Factory.MODEL_FACTORY.makeSlice(dicom1);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetSlice() {
        Slice result = cacher.getSlice(dicom1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    public void testChargeInCache() {
        cacher.chargeInCache(dicom1);
        Slice result = cacher.getSlice(dicom1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    public void testSameSlice() {
        Slice r1 = cacher.getSlice(dicom1);
        Slice r2 = cacher.getSlice(dicom1);
        assertSame(r1, r2);
    }
    
    @Test
    public void testDifferentSlices() {
        Slice r1 = cacher.getSlice(dicom1);
        Slice r2 = cacher.getSlice(dicom2);
        assertNotSame(r1, r2);
    }
    
    @Test
    public void testNoRedundancy() {
        assertEquals(0, cacher.size());
        cacher.chargeInCache(dicom1);
        assertEquals(1, cacher.size());
        cacher.chargeInCache(dicom1);
        assertEquals(1, cacher.size());
        cacher.chargeInCache(dicom2);
        assertEquals(2, cacher.size());
    }
    
}
