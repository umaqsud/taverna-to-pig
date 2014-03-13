package edu.tuberlin.dima.taverna_to_pig;

import java.io.IOException;
import java.util.Iterator;

import org.apache.pig.data.Tuple;
import org.apache.pig.pigunit.PigTest;
import org.apache.pig.tools.parameters.ParseException;

/**
 * 
 * @author Umar Maqsud
 * 
 */
public class PigUnitTest {

	protected void printAlias(PigTest test, String alias) throws IOException,
			ParseException {
		Iterator<Tuple> iterator = test.getAlias(alias);
		while (iterator.hasNext()) {
			Tuple next = iterator.next();
			System.out.println(next);
		}
	}

}
