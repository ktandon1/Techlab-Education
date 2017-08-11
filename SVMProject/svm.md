@title "Support Vector Machines"
@author "Kaushik Tandon"
@heading "Support Vector Machines"
@subheading "A supervised machine learning algorithm used in both classification and regression."
@objective "learn about Support Vector Machines"
@nav { "Machine Learning": "." }
@navActive "SVM"
@highlight "java"

---
@title "Introduction"

Support Vector Machine (SVM) is a supervised Machine Learning technique that is useful for classification and regression problems. SVM is a good technique to use if you have two classes of data and want to determine where a new data point would fall. Given a set of training examples, each marked as belonging to either one of two categories, an SVM training algorithm builds a model that assigns new examples to one category or the other. 

SVM works by defining a **separating hyperplane** between the data classes. A new data point will be classified based on which **side** of the plane it falls on. If there are two features being compared in each class (i.e. x and y position), then the separating hyperplane is a line. In the example below, the method learns the best possible separation between the blue and yellow data. A new data point on the right side of the line would be classified as being from the ‘yellow’ class while a new data point on the left side of the line would be ‘blue’.

[SVM Diagram](https://raw.githubusercontent.com/ktandon1/Techlab-Education/master/SVMProject/svm_diagram.png)

There’s a lot of math behind this that you can look into if you are curious. SVM works by **maximizing** the distance between a line and the closest data point, also known as the **margin**. The best line is one that has the **largest** margin. 

+++
@title "Non Linear SVM"
The method can even deal with non-linear classifications. This lesson will only look at linear classifications.

---
@title "Getting Started"
@template "steps"
@length 15

You will be drawing a bunch of “particles” with a line separating them into two distinct classes. There are three simple methods that you can use to implement SVM in Java. One method is calculating the midpoints of the different particle classes and finding a perpendicular line. Another method is comparing 10,000 lines to determine the best line to separate the particles. The last method is using Weka, an online library that implements the SVM algorithm. To use Weka, you will need to download Weka.jar from the [Weka website](http://www.cs.waikato.ac.nz/ml/weka/downloading.html) and add it to the build path. If you do not wish to use Weka, then you do not need to download it.
+++
Start off by creating a Java class named SVM. Set the window to 600 x 600, create the integers goodPopulationSize and badPopulationSize and set their value to a number between 1 and 10 of your choice. 

```java
public class SVM {
	//set them to be between 1 and 10
	private int goodPopulationSize;
	private int badPopulationSize;
	public static void main(String[] args)
	{
		Window.size(600, 600);	
		Window.setFrameRate(50);
	}

}
```

+++

Now create a Particle class.
The class has a few fields:
* starting position (x,y) that is randomly generated
* speeds dx, dy for moving
* good that represents which class the particle belongs to - "good" or "bad"

The Particle constructor takes a boolean that determines if the particle is good or bad. The starting position is chosen randomly and the speed is initially based off the class. This class also contains methods for drawing, moving, and dealing with Particles colliding with other Particles.

```java
class Particle
{
	public double x, y, dx, dy;
	public boolean good;
	/*
	 * Constructor that creates a good/bad particle
	 * @param g - if particle is good or bad
	 */
	public Particle(boolean g)
	{
		good = g;
		// start at random spot on window
		x = Math.random() * Window.width();
		y = Math.random() * Window.height();
		
		// Speed of the particle
		if(good)
		{
			dx = 1;
			dy = 1;
		}
		else
		{
			dx = -1;
			dy = -1;
		}
	}
	/*
	 * Draw particle represented by circle of radius 5
	 */
	public void draw()
	{		
		Window.out.circle(x, y, 5);
	}
	/*
	 * Move particle
	 */
	public void move()
	{
		x = x + dx;
		y = y + dy;
		// Check if particle is off borders
		if(x > Window.width() || x < 0)
		{
			x  = Math.min(Math.max(x, 0),Window.width());
			dx  = -dx;
		}
		if(y > Window.height() || y < 0)
		{
			y = Math.min(Math.max(y, 0), Window.height());
			dy= -dy;
		}
	}
	/*
	 * Check if particle is touching another particle
	 * @param other - other particle to check
	 * @return - true if touching, false if not
	 */
	public boolean isTouching(Particle other) 
	{
		// Return whether or not distance is less than or equal to 10
		double distance = Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
		return distance <= 10;
	}
	/*
	 * If the particles are touching, the dx and dy values are swapped
	 * @param other - other Particle to swap with
	 */
	public void contactWith(Particle other)
	{
		double tempdx = dx;
		dx = other.dx;
		other.dx = tempdx;
		
		double tempdy = dy;
		dy = other.dy;
		other.dy = tempdy;
	}
	/*
	 * returns if particle is good or bad
	 * @return
	 */
	public boolean isGood()
	{
		return good;
	}
}
```
+++
In the SVM class, create an array of Particles. 
* Initialize a subset of the array of Particles to be ‘good’ and the rest be ‘bad’ based off your population sizes.
Draw all the frames and loop through all the particles. 
* If they are good, draw them as green, else draw red particles. 
* Be sure to make them move and deal with the situation of them bouncing off each other using the isTouching(Particle other) and contactWith(Particle other) methods.

```java
public class SVM {
	//variables
	public static void main(String[] args)
	{
		Window.size(600, 600);	
		Window.setFrameRate(50);
		//create arrays of all particles
		SVM b = new SVM();
		b.runSimulation();
	}
	/*
	* Constructor, initializes a Particle array that contains both 'good' and 'bad' particles
	*/
	public SVM()
	{
		allParticles = new Particle[goodPopulationSize + badPopulationSize];
		for(int i = 0; i<goodPopulationSize; i++)
		{
			allParticles[i] = new Particle(true);
		}
		for(int i =goodPopulationSize;i<allParticles.length; i++)
		{
			allParticles[i] = new Particle(false);
		}
	}
	/*
	 * Method that draws and moves the particles in every frame
	 */
	public void runSimulation()
	{
		while(true)
		{
			Window.frame();
			//Draw the particles
			for(Particle p: allParticles)
			{
				if(p!= null)
				{
					Window.out.color("Red");
					if(p.isGood())
						Window.out.color("Green");
					p.draw();
					p.move();
					for (Particle other : allParticles) {
						if (other!=null && p != other && p.isTouching(other)) {
							p.contactWith(other);
						}
					}
				} 
			}
		}
	}
}
```
---

@title "Midpoint Method"
@template "steps"
@length 10

This is the simplest to implement, but is also the least accurate. You will need to find the midpoints of the good particles, the midpoints of the bad particles, and the midpoints of all the particles. Each of those should be a (x,y) pair. 

```java
public class SVM
{
	private double rMidX, rMidY, gMidX, gMidY, allMidX, allMidY;

	/*
	 * Calculate midPoints of given subset of particles
	 * @param good - if particles are good or bad
	 */
	public void findMidPoints(boolean good)
	{
		if(good)
		{
			gMidX = 0;
			gMidY = 0;
			// if good, iterate through this part of the allParticles array
			for(int i =0; i<goodPopulationSize; i++) 
			{
				gMidX = gMidX + allParticles[i].x;
				allMidX = allMidX + allParticles[i].x;
				gMidY = gMidY + allParticles[i].y;
				allMidY = allMidY + allParticles[i].y;
			}
			gMidX = gMidX/goodPopulationSize;
			gMidY = gMidY/goodPopulationSize;
		}
		else
		{
			//iterate through bad particles and calculate midpoints
			rMidX = 0;
			rMidY = 0;
			for(int i =goodPopulationSize; i<goodPopulationSize + badPopulationSize; i++)
			{
				rMidX = rMidX + allParticles[i].x;
				allMidX = allMidX + allParticles[i].x;
				rMidY = rMidY + allParticles[i].y;
				allMidY = allMidY + allParticles[i].y;
			}
			rMidX = rMidX/badPopulationSize;
			rMidY = rMidY/badPopulationSize;
			//calculating midpoint of all particles
			allMidX = allMidX/(goodPopulationSize+badPopulationSize); 
			allMidY = allMidY/(goodPopulationSize+badPopulationSize);
		}		
	}
}
```
+++
Calculate the slope of the line that is from the midpoint of the good particles to the midpoint of the bad particles. 
The line to draw is a line through the (x,y) for the midpoints of **ALL** the particles that is perpendicular to the previous line. 
* To calculate the slope of the line to draw, just do -1/slope.
```java
public class SVM
{
	/*
     * Method to draw best fit line using midpoints
     */
	public void drawBestLineMidPoints()
	{
		//find midpoints for good and bad particles
		findMidPoints(true); 
		findMidPoints(false);
		//slope of line from midpoints of good particles to bad
		double slope = (gMidY - rMidY)/(rMidY-rMidX); 
		double perpSlope = -1/slope;
		
		//point (allMidX, allMidY), slope=perpSlope
		//y = perpSlope(x-allMidX) + allMidY
		
		double startX = 0;
		double startY = perpSlope * -1 * allMidX + allMidY;
		double endX = Window.width();
		double endY = perpSlope*(endX-allMidX) + allMidY;
		Window.out.color("Orange");
		Window.out.line(startX, startY, endX, endY);
	}
}
```
---
@title "Score Method"
@template "steps"
@length 10

This method will compare 10,000 randomly generated lines and determine the best line to separate the particles. Start off by going through a loop 10,000 times and defining a slope and y-integer variable to random numbers *Window.height().
* Be sure to include the possibility of negative slopes and y-intercepts. 

```java
public class SVM
{
	/*
	 * Method to draw best fit line. Uses scoring system to determine best of 10,000 generated lines
	 */
	public void drawBestLine()
	{
		int numberOfLines = 10000;
		for(int i =0; i<numberOfLines; i++)
		{
			double neg = Math.random();
			double slope = (Math.random()) * Window.height();
			if(neg < 0.5)
			{
				slope = -1*slope;
			}
			neg = Math.random();
			double yInt = (Math.random()) * Window.height();
			if(neg < 0.5)
			{
				yInt = -1*yInt;
			}
		}
	}
}

```
+++
Now define a calculateScore method that takes the slope and y-intercepts of each line and returns a score. The best separating line will be one with a majority of good Particles on one side with the majority of bad Particles on the other. 
In calculateScore(), loop through all the particles and determine which side of the line they are on. If there are more good particles on one side and more bad particles on the other, increase the score by 500. If all the good particles are on one side and all the bad particles are on the other, increase the score by 1000. If all the good particles and bad particles are on the same side, subtract 500 from the score

```java
public class SVM
{
	public void drawBestLine()
	{		
		//variables from previous step
		double score = calculateScore(slope, yInt);
	}
	/*
	 * Determines if the particle is left of a line
	 * @param startX, startY - starting points of line
	 * @param endX, endY - end points of line
	 * @param p - particle to check if left of line
	 * @return - true if left of line, false if right of line
	 */
	public boolean isLeft(double startX, double startY, double endX, double endY, Particle c){
	     return ((endX - startX)*(c.y - startY) - (endY - startY)*(c.x - startX)) > 0;
	}
	/*
	 * Method to calculate score for line
	 * @param slope - slope of line
	 * @param yInt - y-intercept of line
	 * @return - score for line
	 */
	public double calculateScore(double slope, double yInt)
	{
		double score = 0;
		//good line = majority of good particles on one side, bad particles on other 
		double goodLeftOfLine = 0;
		double badLeftOfLine = 0;
		for(Particle p:allParticles)
		{
			//which side particle is on
			boolean leftOf = isLeft(0,yInt,Window.width(),slope*Window.width() + yInt,p);
			if(leftOf && p.isGood())
			{
				goodLeftOfLine += 1;
			}
			else if(leftOf && !(p.isGood()))
			{
				badLeftOfLine +=1;
			}
		}
		//if the particles are not left of the line, they are right of the line
		double goodRightOfLine = goodPopulationSize - goodLeftOfLine; 
		double badRightOfLine = badPopulationSize - badLeftOfLine;
		
		//good on right, bad on left
		if(goodRightOfLine > goodLeftOfLine && badLeftOfLine > badRightOfLine) 
		{
			score += 500; 
		}
		// good on left, bad on right
		else if(goodRightOfLine < goodLeftOfLine && badLeftOfLine < badRightOfLine) 
		{
			score += 500;
		}
		//all good on one side, all bad on other
		if((goodRightOfLine == goodPopulationSize && badLeftOfLine == badPopulationSize) || (badRightOfLine == badPopulationSize && goodLeftOfLine == goodPopulationSize))
		{
			score += 1000;
		}
		//all particles on one side of line
		if((goodLeftOfLine == 0 && badRightOfLine == badPopulationSize) || (goodRightOfLine == 0 && badLeftOfLine == badPopulationSize))
		{
			score -= 500;
		}
		else if((badLeftOfLine == 0 && goodRightOfLine == goodPopulationSize) || (badRightOfLine == 0 && goodLeftOfLine == goodPopulationSize))
		{
			score -=500;
		}
}
```
+++
The next step is to find the nearest particle.
* Goal is to maximize margin (distance to the closest point)
Increase score by the return value of calculateDistance(slope, yInt, nearest). 

Now that you have the score, compare it to the previous score. 
* If score is higher, store the score, slope, and y-intercept variables. 

You can also compare the score to the previous best score which had drawn the line in the previous frame. You will want to recalculate that score using the previous best slope and previous best y-intercept. Doing this will make the line more stable and not as all over the place. 

```java
public class SVM
{
	private double previousBestScore, previousBestSlope,previousBestYInt;
	public double calculateScore(double slope, double yInt)
	{
		Particle nearest = findNearestParticle(slope,yInt);
		score += calculateDistance(slope, yInt, nearest); 
		return score;
	}
	/*
	 * Calculate distance between line and Particle
	 * Distance from point to line is |a(x1) + b(y1) + c|/sqrt(a^2+b^2), where ax + by + c = 0. y = mx+c, so a = slope, b = -1, c = yInt
	 * @param slope, yInt - slope/yInt of line
	 * @param p - Particle to check
	 * @return - distance from point to line
	 */
	public double calculateDistance(double slope, double yInt, Particle p)
	{
		return Math.abs(slope*p.x + (-1) * p.y + yInt)/Math.sqrt(slope * slope+1);
	}
	public void drawBestLine()
	{
		for(int i =0; i<10000; i++) //from before
		{
		}
		previousBestScore = calculateScore(previousBestSlope, previousBestYInt);
		if(previousBestScore < bestScore)
		{
			previousBestYInt = bestYInt;
			previousBestScore = bestScore;
			previousBestSlope = bestSlope;
		}
		else
		{
			bestYInt = previousBestYInt;
			bestSlope = previousBestSlope;
		}
	}

}
```

+++
Draw a line from (0,y-intercept) to (Window.width(), slope*Window.width() + y-intercept). 
```java
public class SVM
{
	//variables and methods from before
	public void drawBestLine()
	{
		//previous code
		double startX = 0;
		double startY = bestYInt;
		double endX = Window.width();
		double endY = bestSlope * endX + bestYInt;
		Window.out.color("Green");
		Window.out.line(startX, startY, endX, endY);
	}

}
```
---
@title "Weka Method"
@template "steps"
@length 10

Most of the time people do not want to spend time writing up a SVM algorithm. There are numerous libraries out there that are used, the most notable being Weka. In order to use Weka, you will need to add weka.jar to the build path by downloading from the [Weka website](http://www.cs.waikato.ac.nz/ml/weka/downloading.html).

+++

In the SVM class, add the following libraries
```java
import weka.core.Instances;
import weka.classifiers.functions.*;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.SelectedTag;
import java.io.*;
```

In order for Weka to work, you will need to make a .csv file with the particles positions and class label in every frame. After you finish looping through all the particles when drawing and moving, add the following code. 
```java
public class SVM
{
	public void runSimulation()
	{
		while(true)
		{
			Window.frame();
			//draw and move particles

			//Write csv file containing x,y,good values for each particle
			PrintWriter pw = openToWrite("data.csv"); //Easiest to use .csv file
			pw.println("X,Y,Good"); //Headers for file, needed for Weka
			for(Particle p: allParticles)
			{
				pw.println(p.x + "," + p.y + "," + p.good);
			}
			pw.close();
		}
	}
	/*
	 * Method to open file to begin writing
	 * @param fileString - name of the file to open
	 * @return - PrintWriter to use to write file
	 */
	public static PrintWriter openToWrite(String fileString)
	{
		PrintWriter outFile = null;
        try {
        	outFile = new PrintWriter(fileString);
           	} catch(Exception e)
           	{
        		System.out.println("\n Error: File could not be created " + fileString);
            	System.exit(1);
      		}
        return outFile;
   	}
}
```

+++

The SVM equivalent in Weka is SMO. Most of the following lines are attributes that Weka needs to be set up. There are different settings you can explore in more detail if you are interested. 

Add this code to your SVM class 

```java
public class SVM
{
	public void runSimulation()
	{
		//after pw.close()
		drawBestWekaLine();
	}
	public void drawBestWekaLine()
	{
		DataSource source;
		Instances data = null;
		SMO classifier = new SMO();
		try {
			source = new DataSource("data.csv");
			data = source.getDataSet();
			data.setClassIndex(data.numAttributes() - 1);
			classifier.setFilterType(new SelectedTag(SMO.FILTER_NONE,SMO.TAGS_FILTER));;
			classifier.buildClassifier(data);
			
		} catch (Exception e) {
			System.out.println("Data not found, so classifier not built");
		}
	}
}
```
+++
If you are to print classifier.toString() now, you will see something like [this](https://raw.githubusercontent.com/ktandon1/Techlab-Education/master/SVMProject/classifierString.png)

The Kernel used line just means that a linear classifier is being used as intended. There are two classes: “true” and “false” which refer to if the particle is good or not good. 
The most important lines is the equation 0.0049X + 0.004Y -2.054 = 0. You will need to extract the coefficients and the intercept from that equation by splitting the lines from classifier.toString(). 
* Extract the y-coefficient first by checking if the line contains a “+” and a “*” 
* If the line only contains a “*” then you can parse the x-coefficient 
* If the line contains only a “+” or only a “-“, you can parse out the intercept.  

```java
public class SVM
{
	public void drawBestWekaLine()
	{
		//code from previous step
		double xCoefficient = 0;
		double yCoefficient = 0;
		double c = 0;
		System.out.println(classifier.toString());
		String[] lines = classifier.toString().split("\n"); //xX + yY + c = 0.. yY = -xX -c, Y = (-xX-c)/y
		
		//parsing the equation from the classifier string
		for(String line:lines)
		{
			if(line.contains("+") && line.contains("*")) //has to have both + and *
			{
				yCoefficient = Double.parseDouble(line.substring(2,line.indexOf("*")).trim());
			}
			else if(line.contains("*"))
			{
				xCoefficient = Double.parseDouble(line.substring(2,line.indexOf("*")).trim());
			}
			else if(line.contains("+"))
			{
				c  = Double.parseDouble(line.substring(2).trim());
				
			}
			else if(line.contains("-"))
			{
				c  = -1 * Double.parseDouble(line.substring(3).trim());
			}
		}
	}
}
```

+++
Using some algebra, you can rewrite the line as Y = (-xX – c)/y where x is the x-coefficient, y is the y-coefficient, and c is the intercept. Draw a line starting from X = 0 to X = Window.width() with the respective Y from the equation. This will be the line that separates the data

```java
public class SVM
{
	public void drawBestWekaLine()
	{
		//code from above
		Window.out.color("White");
		double startX = 0; ////xX + yY + c = 0.. yY = -xX -c, Y = (-xX-c)/y
		double startY = -c/yCoefficient;
		double endX = Window.width();
		double endY = ((-1 * xCoefficient* endX) - c)/yCoefficient;
		Window.out.line(startX,startY,endX,endY); 
	}
}
```

---
@title "Conclusion"

Hopefully you got the code to work. Keep in mind the lines may not be the best if you cannot actually draw a line separating the data. SVM is truly an exciting technique with many more possibilities than the limited few I gave. If you are interested in learning more, there are numerous online tutorials that go into more detail about the complex aspects of this method.
