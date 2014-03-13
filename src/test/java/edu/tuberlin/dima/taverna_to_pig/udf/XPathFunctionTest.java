package edu.tuberlin.dima.taverna_to_pig.udf;

import static junit.framework.Assert.*;

import java.io.File;
import java.io.FileReader;

import org.apache.pig.data.DataBag;
import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.apache.tools.ant.util.FileUtils;
import org.junit.Test;

import com.google.common.collect.Iterables;
import com.google.common.io.Resources;

import edu.tuberlin.dima.taverna_to_pig.udf.XPathFunction;

/**
 * 
 * @author Umar Maqsud
 * 
 */
public class XPathFunctionTest {

    TupleFactory tupleFactory = TupleFactory.getInstance();

    @Test
    public void testXPath() throws Exception {
    	
    	String xml = FileUtils.readFully(new FileReader(new File(Resources
				.getResource("pig/input/fits_validation/19191227b.xml")
				.getFile())));
    	
        String expression = "/fits/filestatus/valid";

        XPathFunction xPathFunction = new XPathFunction();

        Tuple tuple = tupleFactory.newTuple(2);
        tuple.set(0, expression);
        tuple.set(1, xml);

        DataBag dataBag = xPathFunction.exec(tuple);

        Tuple[] tuples = Iterables.toArray(dataBag, Tuple.class);
        
        assertEquals(1, tuples.length);
        
        assertEquals("true", tuples[0].get(0));
        
    }

}
