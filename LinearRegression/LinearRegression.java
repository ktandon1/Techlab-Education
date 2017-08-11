import apcs.Window;
public class LinearRegression {
	private DataPoint[] data;
	private int dataSize = 100;
	
	public static void main(String[] args)
	{
		Window.size(600, 600);	
		Window.setFrameRate(1);

		LinearRegression b = new LinearRegression();
		b.runSimulation();	
	}
	/*
	 * Method that initalizes data and calls the regression methods
	 */
	public void runSimulation()
	{
		while(true)
		{
			data = new DataPoint[dataSize];
			double weightedX = (Math.random() * 4);
			double weightedY = (Math.random() * 4);
			for(int i =0; i<data.length;i++)
			{
				int x = (int)(Math.random() * Window.width() * weightedX);
				int y = (int)(Math.random() * Window.height() * weightedY);
				data[i] = new DataPoint(x,y);
			}
			Window.frame();
			for(DataPoint point: data)
			{
				point.draw();
			}
			determineBestRegressionLineSimpleLinearRegression();
			determineBestRegressionLineLeastSquares();
		}
	}
	/*
	 * Implementation of a Least Squares Regression. 
	 * Determines the best line from 20,000 randomly generated slopes and y-intercepts
	 */
	public void determineBestRegressionLineLeastSquares()
	{
		double lowestError = Double.MAX_VALUE;
		double bestSlope = 0;
		double bestYInt = 0;
		for(int i =0; i < 20000; i++)
		{
			double slope = genRandNum();
			double yInt = genRandNum();
			double error = 0;
			for(DataPoint point: data)
			{
				if(pointOnScreen(point))
				{
					double dist = calculateDistance(slope,yInt,point); //calculate distance from each point to regression line
					dist = dist*dist; //square distance
					error += dist; //sum squared errors
				}
			}
			if(error < lowestError)//if better, save
			{
				lowestError = error;
				bestSlope = slope;
				bestYInt = yInt;
			}
		}
		//draw best
		double startX = 0;
		double startY = bestYInt;
		double endX = Window.width();
		double endY = endX * bestSlope + bestYInt;
		Window.out.color("Green");
		Window.out.line(startX,startY,endX,endY);
	}
	/*
	 * Calculate distance between line and DataPoint
	 * Distance from point to line is |a(x1) + b(y1) + c|/sqrt(a^2+b^2), where ax + by + c = 0. y = mx+c, so a = slope, b = -1, c = yInt
	 * @param slope, yInt - slope/yInt of line
	 * @param p - DataPoint to check
	 * @return - distance from point to line
	 */
	public double calculateDistance(double slope, double yInt, DataPoint p)
	{
		return Math.abs(slope*p.getX() + (-1) * p.getY() + yInt)/Math.sqrt(slope * slope+1);
	}
	/*
	 * Implementation of Simple Linear Regression. Determines the best line using statistics from the DataPoints
	 */
	public void determineBestRegressionLineSimpleLinearRegression()
	{
		int length = calculateSize(data);
		double xSum = 0; double ySum = 0; double xySum = 0; double xxSum = 0;
		for(int a = 0; a<data.length; a++)
		{
			//make sure point is on screen
			if(pointOnScreen(data[a]))
			{
				xSum = xSum + data[a].getX();
				ySum = ySum + data[a].getY();
				xySum = xySum + data[a].getX()*data[a].getY();
				xxSum = xxSum + data[a].getX()*data[a].getX();
			}
		}
		double slope = (length*xySum - xSum*ySum)/(length*xxSum-(xSum*xSum));
		double yInt = (ySum-slope*xSum)/length;
		Window.out.color("Blue");
		Window.out.line(0,yInt,Window.width(),Window.width()*slope+yInt);
	}
	/*
	 * Generates a random number that can possibly be negative
	 * @return - random number
	 */
	public double genRandNum()
	{
		double neg = Math.random();
		double num = (Math.random()) * Window.height();
		if(neg < 0.5)
		{
			return - 1 * num;
		}
		return num;
		
	}
	/*
	 * Determines if a given DataPoint is on screen
	 * @param point - DataPoint to check
	 * @return - if point is within the Window's dimensions
	 */
	public boolean pointOnScreen(DataPoint point)
	{
		return(point.getX() >= 0 && point.getX() <= Window.width() && point.getY() >= 0 && point.getY() <= Window.height());
	}
	/*
	 * Determines how many DataPoints are in the Window
	 * @param points - Array of points to check
	 * @return - number of points in the Window's dimensions
	 */
	public int calculateSize(DataPoint[] points)
	{
		int count = 0;
		for(DataPoint point: points)
		{
			if(pointOnScreen(point))
			{
				count++;
			}
		}
		return count;
	}
}
class DataPoint
{
	private int x, y;
	public DataPoint(int xx, int yy)
	{
		x = xx;
		y = yy;
	}
	/*
	 * Method that returns the DataPoint's X position
	 */
	public int getX()
	{
		return x;
	}
	/*
	 * Method that returns the DataPoint's Y position
	 */
	public int getY()
	{
		return y;
	}
	/*
	 * Method that draws the DataPoint as a circle of radius 2
	 */
	 public void draw()
	{
		Window.out.color("White");
		Window.out.circle(x,y, 2);
	}
}
