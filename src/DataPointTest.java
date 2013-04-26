import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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
		f = new File("../data/pm25_2009_measured.txt");
		pointList = null;
		try {
			pointList = DataPoint.parseFile(f);
		} catch (FileNotFoundException e) {
			Assert.fail("DataPoint threw an File Not Found");
		}


	}

	@Test
	public final void testDataPointDoubleArray() {
		double[] testingArray = {-87.881410, 30.498000, 62, 0};
		DataPoint testing = new DataPoint(testingArray);
		Assert.assertEquals(testingArray[0], testing.x);
		Assert.assertEquals(testingArray[1], testing.y);
		Assert.assertEquals(testingArray[2], testing.time);
		Assert.assertEquals(testingArray[3], testing.measurement);
	}

	@Test
	public final void testDataPointDoubleDoubleIntDouble() {
		DataPoint testing = new DataPoint(-87.881410,30.498000,62,3);
		Assert.assertEquals(-87.881410,testing.x);
		Assert.assertEquals(30.498000,testing.y);
		Assert.assertEquals(62,testing.time);
		Assert.assertEquals(3,testing.measurement);
	}

	@Test
	public final void testInterpolateValueDoubleDoubleIntListOfDataPoint() {
		//create list of points here
		DataPoint a = new DataPoint(-87.881412,30.498001,67,0);
		DataPoint b = new DataPoint(-87.881412,30.498001,70,0);
		DataPoint c = new DataPoint(-87.881412,30.498001,73,0);
		ArrayList<DataPoint> dataPoints = new ArrayList<DataPoint>();
		dataPoints.add(a); dataPoints.add(b); dataPoints.add(c);
		//testInterpolate now
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
