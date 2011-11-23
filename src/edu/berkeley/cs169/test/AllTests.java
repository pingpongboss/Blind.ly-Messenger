package edu.berkeley.cs169.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


//JUnit test file that runs all existing test cases in the test package.
@RunWith(Suite.class)
@SuiteClasses({ BMUtilsTest.class, MorseCodeModelTest.class })
public class AllTests {

}
