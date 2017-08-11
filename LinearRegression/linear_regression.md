@title "Linear Regression"
@author "Kaushik Tandon"
@heading "Linear Regression"
@subheading "A machine learning technique used to find the best-fit line through a set of data."
@objective "implement a Linear Regression Algorithm"
@nav { "Machine Learning": "." }
@navActive "Linear Regression"
@highlight "java"

---
@title "Introduction"

Linear Regression is one of the most well-known and simplest supervised learning Machine Learning algorithms to use. If you are familiar with statistics, you might be wondering why Linear Regression is considered a Machine Learning algorithm. Machine Learning is the field of predictive modeling and focuses on minimizing the error of a model. Linear Regression was first developed for statistics, but is used as a Machine Learning technique.

[Linear Regresion Diagram](https://raw.githubusercontent.com/ktandon1/Techlab-Education/master/LinearRegression/regression_diagram.png)

This technique works by determining a best-fit line through a bunch of data points. The model assumes a linear relationship between the input variables and the output variables. There are other techniques for non-linear relationships, such as logistic regression. There are numerous methods used to determine the best-fit line for a dataset. The most commonly used method is minimizing the sum of squared errors, and is a method that will be explored in this lesson. You can easily go into Microsoft Excel, plot data, and get the best fit line. Linear Regression is a rather simple topic to understand and is not very hard to implement in Java. The form of a line is y = B0 + B1 * x, so if you can determine B0 and B1, you will have a relationship between y and x. 

---
@title "Setup"
@template "steps"
@length 15

Let’s begin an implementation of linear regression. You will be drawing a bunch of data points and will determine the best-fit line. There are two methods that are easy to use and do not require too much math.
+++
Start off by creating a Java program named LinearRegression. Set the window to 600 x 600 and initialize a global variable for the maximum data size to be around 100. Make the frame rate low so you can actually see what happens.
```java
public class LinearRegression {
	private int dataSize = 100;
	public static void main(String[] args)
	{
		Window.size(600, 600);	
		Window.setFrameRate(1);

	}
}
```
+++
Now create a DataPoint class
The class has a few fields:
* Position (x,y) that is randomly generated
Contains a draw() method that draws a circle of radius 2 and getX() and getY() methods

```java
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
```
+++
In the LinearRegression class, Make an array of DataPoints with the dataSize from the first step. In every frame, initialize all the points in the array to have random x and y values. I would recommend including some sort of “weighted” values to ensure the points end up more clustered, like the code on the right. Draw the points as well. If you use weighting, include a method to check if the point is on the screen and have a way to count how many are in the frame.

```java
public class LinearRegression {
	private DataPoint[] data;
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
		}
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

```
---
@title "Simple Linear Regression"
@template "steps"
@length 5

The best-fit line will be made using the following formulas. 
* Regression Equation(y) = a + bx
* Slope(b) = (NΣXY - (ΣX)(ΣY)) / (NΣX2 - (ΣX)2)
* Intercept(a) = (ΣY - b(ΣX)) / N
ΣXY = Sum of the product of X and Y, ΣX = Sum of all the X values, ΣY = Sum of all the Y values, ΣX2 = Sum of all the (X)^2 values, and N is the number of data points on the screen.

For every point that is on the screen, calculate the above sums. 

```java
public class LinearRegression
{
	private DataPoint[] data;
	public void runSimulation()
	{
		//code from last step
		determineBestRegressionLineSimpleLinearRegression();
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
	}
}
```

+++
You should now have the slope and y-intercept. Draw the line from (0,yInt) to (Window.width(), Window.width()*slope + y-intercept).
```java
public class LinearRegression
{
	public void determineBestRegressionLineSimpleLinearRegression()
	{
		Window.out.color("Blue");
		Window.out.line(0,yInt,Window.width(),Window.width()*slope+yInt);	
	}
}
```

+++
You should now have the best-fit line. It may or may not be very accurate depending on how spread out the data is. 

---
@title "Ordinary Least Squares"
@template "steps"
@length 10

The method focuses on minimizing the sum of squared errors. The error is defined as the difference between the actual value and the predicted value of a line. 

To implement this method, we will compare 20000 lines to determine which line has the lowest sum of squared errors. 

For each line, we randomly generate a slope and a y-intercept. Be sure to include the possibilities of negative slopes and y-intercepts. 

```java
public class LinearRegression
{
	public void runSimulation()
	{
		determineBestRegressionLineLeastSquares();
	}
	/*
	 * Implementation of a Least Squares Regression. 
	 * Determines the best line from 20,000 randomly generated slopes and y-intercepts
	 */
	public void determineBestRegressionLineLeastSquares()
	{
		for(int i =0; i < 20000; i++)
		{
			double slope = genRandNum();
			double yInt = genRandNum();
		}
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
}
```
+++

For each data point, calculate the distance between the point and the line. You will want to use the method on the right to do that. Square that distance and add it to a running total of errors.
```java
public class LinearRegression
{
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
	public void determineBestRegressionLineLeastSquares()
	{
		for(int i =0; i < 20000; i++) //same one from before
		{
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
		}
	}
}
```
+++
Check if the error is lower than the lowest error so far. If it is, save the error, slope, and y-intercept. At the end, draw a line from (0,y-int) to (Window.width(), Window.width()*slope + y-int)

```java
public class LinearRegression
{
	public void determineBestRegressionLineLeastSquares()
	{
		double lowestError = Double.MAX_VALUE;
		double bestSlope = 0;
		double bestYInt = 0;
		for(int i =0; i < 20000; i++) //same one from before
		{	
			if(error < lowestError)//if better, save
			{
				lowestError = error;
				bestSlope = slope;
				bestYInt = yInt;
			}
		}
		double startX = 0;
		double startY = bestYInt;
		double endX = Window.width();
		double endY = endX * bestSlope + bestYInt;
		Window.out.color("Green");
		Window.out.line(startX,startY,endX,endY);
	}
}
```

---
@title "Conclusion"
You should now have a best-fit line through the data. If you implemented both methods, you’ll see that the second method typically has better results than the first. This is because thousands of lines are checked with the second method to determine the best line. The first one only uses statistics, which will be affected by outliers. The second method is more robust when dealing with outliers. If you only implemented the first method, try implementing the second and compare the results for yourself.

Linear Regression is one of the simpler Machine Learning techniques to understand and may not seem as exciting. If you got this to work, then you are ready to learn more advanced techniques.
