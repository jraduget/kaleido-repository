package org.kaleidofoundry.core.io;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.io.Tail.TailLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test Tail
 * 
 * @author Jerome RADUGET
 */
public class TailTest {

   static final Logger logger = LoggerFactory.getLogger(Tail.class);

   private Tail tailTool;

   @Before
   public void setUp() throws FileNotFoundException {
	tailTool = new Tail(this.getClass().getClassLoader(), "org/kaleidofoundry/core/io/java_install.log");
   }

   /**
    * @throws IOException
    */
   @Test
   public void testLastLine() throws IOException {

	final long LastLine = 2;
	final List<TailLine> result = tailTool.tail(LastLine);

	assertNotNull(result);
	assertEquals(LastLine, result.size());
	assertEquals(new TailLine(550, "A total of 7571805 file content bytes were written."), result.get(0));
	assertEquals(new TailLine(551, "A total of 3294 files (of which 3267 are classes) were written to output."), result.get(1));

	assertNotNull(tailTool.getCurrentBuffer());
	assertEquals(LastLine, tailTool.getCurrentBuffer().size());
	tailTool.clearCurrentBuffer();
	assertNotNull(tailTool.getCurrentBuffer());
	assertEquals(0, tailTool.getCurrentBuffer().size());
   }

   /**
    * @throws IOException
    */
   @Test
   public void testSubsetLine() throws IOException {

	final long MaxLine = 4;
	final long FromLine = 493;
	int cpt = 0;
	final List<TailLine> result = tailTool.tail(FromLine, MaxLine);

	assertNotNull(result);
	assertEquals(MaxLine, result.size());
	assertEquals(new TailLine(FromLine + cpt++, "extracting: lib/zi/Pacific/Fakaofo"), result.get(cpt));
	assertEquals(new TailLine(FromLine + cpt++, "extracting: lib/zi/Pacific/Fiji"), result.get(cpt));
	assertEquals(new TailLine(FromLine + cpt++, "extracting: lib/zi/Pacific/Funafuti"), result.get(cpt));
	assertEquals(new TailLine(FromLine + cpt++, "extracting: lib/zi/Pacific/Galapagos"), result.get(cpt));

	assertNotNull(tailTool.getCurrentBuffer());
	assertEquals(MaxLine, tailTool.getCurrentBuffer().size());
	tailTool.clearCurrentBuffer();
	assertNotNull(tailTool.getCurrentBuffer());
	assertEquals(0, tailTool.getCurrentBuffer().size());
   }
}
