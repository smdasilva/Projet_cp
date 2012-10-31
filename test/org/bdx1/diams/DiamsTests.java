package org.bdx1.diams;

import org.bdx1.diams.caching.CachingSuite;
import org.bdx1.diams.model.ModelSuite;
import org.bdx1.diams.parsing.ParsingSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ModelSuite.class, ParsingSuite.class, CachingSuite.class})
public class DiamsTests {

}
