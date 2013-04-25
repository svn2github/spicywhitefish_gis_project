import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;


public class DataPointTest {

	static File f;
	static List<DataPoint> pointList;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		f = new File("/data_file.txt");
		pointList = null;
		try {
			pointList = DataPoint.parseFile(f);
		} catch (FileNotFoundException e) {
			Assert.fail("DataPoint threw an FNF");
		}


	}

	@Test
	public final void testDataPointDoubleArray() {
//		fail("Not yet implemented");
	}

	@Test
	public final void testDataPointDoubleDoubleIntDouble() {
//		fail("Not yet implemented");
	}

	@Test
	public final void testInterpolateValueDoubleDoubleIntListOfDataPoint() {
//		fail("Not yet implemented");
	}

	@Test
	public final void testInterpolateValueDoubleDoubleIntIntDoubleListOfDataPoint() {
//		fail("Not yet implemented");
	}

	@Test
	public final void testGetLambda() {
//		fail("Not yet implemented");
	}
	
	
	
	@Test
	public final void testFindNeighbors() {
		DataPoint testPoint = new DataPoint(-87.881410, 30.498000, 62, 0);
		int numNeighbors = 2;
		List<DataPoint> neighbors = testPoint.findNeighbors(pointList, numNeighbors);
		Collections.shuffle(neighbors);
		Assert.assertEquals(numNeighbors, neighbors.size());
		double[] distances = new double[numNeighbors];
		for(int i=0; i<distances.length; i++) {
			distances[i] = testPoint.getDistanceTo(neighbors.get(i));
		}
		int index=0;
		for(DataPoint element : pointList) {
			index++;
			double testDistance = testPoint.getDistanceTo(element);
			boolean isSmaller=false;
			for(double closestDistance : distances) {
				isSmaller = false;
				if (testDistance < closestDistance) {
					isSmaller = true;
					System.out.println(testDistance);
					System.out.println(closestDistance);
					System.out.println(index);
					System.out.println(element);
					break;
				}
			}
			Assert.assertFalse(isSmaller);
		}
	}

	@Test
	public final void testGetDistanceTo() {
		DataPoint testPoint = new DataPoint(-87.881410, 30.498000, 62, 0);
		DataPoint targetPoint = new DataPoint(-88.087526, 30.769941, 61, 7.0);
		double actual = testPoint.getDistanceTo(targetPoint);
		double expected = 1.05662;
		double delta = .001;
		Assert.assertEquals(expected, actual, delta);
	}

	@Test
	public final void testParseFile() {
		//If this is executed, the beforeClass succeeded, which tests this method
	}

	@Test
	public final void testEqualsObject() {
		DataPoint testPoint = new DataPoint(-1.0, 2.5, 6, -5.7);
		DataPoint otherPoint = new DataPoint(-1.0, 2.5, 6, -5.7);
		Assert.assertEquals(testPoint, otherPoint);
	}

	@Test
	public final void testToString() {
		//not important
	}

}
