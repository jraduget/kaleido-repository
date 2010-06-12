package org.kaleidofoundry.core.io;

import static junit.framework.Assert.assertEquals;

import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test FileHelper
 * 
 * @author Jerome RADUGET
 */
public class FileHelperTest {

   static final Logger logger = LoggerFactory.getLogger(FileHelper.class);

   @Test
   public void testBuildPath() {
	assertEquals(File.separator + "foo" + File.separator + "toto" + File.separator + "tutu" + File.separator + "gaga" + File.separator,
		FileHelper.buildPath("/foo\\toto/tutu\\gaga"));
   }

   @Test
   public void testBuildUnixPath() {
	assertEquals("/foo/toto/tutu/gaga/", FileHelper.buildUnixAppPath("/foo\\toto/tutu\\gaga"));
   }

   @Test
   public void testBuildWindowsPath() {
	assertEquals("\\foo\\toto\\tutu\\gaga\\", FileHelper.buildWindowsAppPath("/foo\\toto/tutu\\gaga"));
   }

   @Test
   public void testBuildWebappPath() {
	assertEquals("/foo/toto/tutu/gaga/", FileHelper.buildWebAppPath("/foo\\toto/tutu\\gaga"));
   }

   @Test
   public void testBuildCustomPath() {
	assertEquals("--foo--toto--tutu--gaga--", FileHelper.buildCustomPath("/foo\\toto/tutu\\gaga", "--"));
   }

   @Test
   public void testGetRelativeClassPathByObj() {
	assertEquals("org" + File.separator + "kaleidofoundry" + File.separator + "core" + File.separator + "io" + File.separator, FileHelper
		.getRelativeClassPath(this));
   }

   @Test
   public void testGetRelativeClassPathByClass() {
	assertEquals("org" + File.separator + "kaleidofoundry" + File.separator + "core" + File.separator + "io" + File.separator, FileHelper
		.getRelativeClassPath(FileHelper.class));
   }

   @Test
   public void testBuildParentPath() {
	assertEquals(File.separator + "foo" + File.separator + "toto" + File.separator + "tutu", FileHelper.buildParentPath(File.separator
		+ "foo" + File.separator + "toto" + File.separator + "tutu" + File.separator + "gaga" + File.separator));
   }

   @Test
   public void testGetFileName() {
	assertEquals("filename.txt", FileHelper.getFileName("/foo/filename.txt"));
   }

   @Test
   public void testGetFileNameExtension() {
	assertEquals("txt", FileHelper.getFileNameExtension("/foo/filename.txt"));
   }

   @Test
   public void testGetFileNameWithoutExt() {
	assertEquals("/foo/filename", FileHelper.getFileNameWithoutExt("/foo/filename.txt"));
   }

}
