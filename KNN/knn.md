@title "K-Nearest Neighbor"
@author "Kaushik Tandon"
@heading "K-Nearest Neighbor"
@subheading "A supervised machine learning algorithm used in both classification and regression."
@objective "learn about K-Nearest Neighbor"
@nav { "Machine Learning": "." }
@navActive "KNN"
@highlight "java"

---
@title "Introduction"

K-Nearest Neighbor (k-NN) is a supervised learning technique that is used for classification and regression. The inputs to the method are the attributes of the closest neighbors. A decision for a certain class is based off the majority of all the neighbors. For example, if you wish to look at the 5 nearest neighbors (k = 5) and determine if the object belongs to a specific class, the decision will be whichever class has the majority of the neighboring objects. If k = 1, then the object is assigned to the class of the closest neighbor.

[k-NN Diagram](https://raw.githubusercontent.com/ktandon1/Techlab-Education/master/KNN/knn.png)

In the example above, k = 5 and the green triangle represents the test sample. The 5 closest neighbors are 4 black circles and 1 red square. Since the majority of the neighbors are black circles, the green triangle would be classified as being part of the black circle class.

k-NN is a type of instance-based learning, or lazy learning, where the function is only approximated locally and all computation is deferred until classification. The k-NN algorithm is among the simplest of all machine learning algorithms. The neighbors are taken from a set of objects for which the class is known. This can be thought of as the training set for the algorithm, though no explicit training step is required.

One of the problems with k-NN is that the “majority voting” of the classification will cause a lower accuracy when the data is skewed towards one class. If there are 20 data points for one class and 5 for another, it is likely newer data points would be classified for the first class. 

The distance to the nearest neighbor is called the Euclidean distance and can be found using the distance formula. 

---
@title "Example"
@template "steps"
@length 10

Let’s begin an implementation of the k-NN method. You will be drawing a bunch of balls who will change their color based on the colors of the closest neighbors. 

Start off by creating a Java program named KNN. Set the window to 600 x 600 and initialize a global variable for the maximum number of balls to be between 50 and 1000. Determine how many nearest neighbors you want to use (make it odd to avoid ties).

```java
public class KNN {
	private static int numberOfBalls = 300;
	private static int numNearestNeighbors = 5;
	public static void main(String[] args)
	{
		Window.size(600, 600);	
	}
}
```
+++

Now create a Ball class
The class has a few fields:
* X position
* Y position
* X speed
* Y Speed
* Color

In the constructor, start at a random point on the screen and set the speed a random number between 0 and 3. Assign the color randomly between two colors of your choice. 

Include a draw() method that draws a circle at (x,y) of radius 2. 
This class must contain methods for moving and dealing with Balls colliding with other Balls.
Finally write a getDistance() method that returns the distance between this ball and another ball using the distance formula.

```java
class Ball
{
	public double x, y, dx, dy;
	public String color;
	/*
	 * Creates a ball at a random position, speed, and color
	 */
	public Ball()
	{
		// start at random spot on window
		x = Math.random() * Window.width();
		y = Math.random() * Window.height();
		
		// Speed of the particle
		dx = Math.random() * 3;
		dy = Math.random() * 3;
		double choice = Math.random();
		color = "Green";
		if(choice < 0.5)
		{
			dx = -dx;
			dy = -dy;
			color = "Blue";
		}
	}
	/*
	 * Draws a Ball of radius 2 as a circle
	 */
	public void draw()
	{	
		Window.out.circle(x, y,2);
	}
	/*
	 * Method to move the Ball, checking to make sure it is still on screen
	 */
	public void move()
	{
		x = x + dx;
		y = y + dy;
		// if particle is off borders
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
	 * Method to calculate Euclidean Distance between two Balls
	 * @param other - other ball to find distance between
	 * @return distance
	 */
	public double getDistance(Ball other)
	{
		double sum = Math.pow(x-other.x, 2) + Math.pow(y-other.y,2);
		return Math.sqrt(sum);
	}
	/*
	 * Determines if two balls are intersecting for bouncing off
	 * @param other - other Ball to check
	 * @return - true if intersecting, false if not
	 */
	public boolean isTouching(Ball other) {
		double distance = Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
		// Return whether or not distance is less than or equal to 4
		return distance <= 4;
	}
	/*
	 * Swaps speeds if bouncing
	 * @param other - other Ball to swap with
	 */
	public void contactWith(Ball other) {
		double tempdx = dx;
		dx = other.dx;
		other.dx = tempdx;
		
		double tempdy = dy;
		dy = other.dy;
		other.dy = tempdy;
	}
}
```

+++
In the KNN class, initalize an array of Ball with the size from above. 

For every frame, move and draw all the balls. Be sure to check if they are bouncing off each other.

```java
public class KNN {
	private Ball[] allBalls;
	public static void main(String[] args)
	{
		Window.size(600, 600);	
		KNN k = new KNN();
		k.runSimulation();
	}
	/*
	 * Constructor to make an array of balls
	 */
	public KNN()
	{
		allBalls = new Ball[numberOfBalls];
		for(int i = 0; i<allBalls.length; i++)
		{
			allBalls[i] = new Ball();
		}
	}
	/*
	 * Method that draws and moves every ball
	 */
	public void runSimulation()
	{
		while(true)
		{
			Window.frame();
			for(Ball b : allBalls)
			{
				Window.out.color(b.color);
				b.draw();
				b.move();
				for (Ball other : allBalls) {
					if (other!=null && b != other && b.isTouching(other)) {
						b.contactWith(other);
					}
				}
			}
			implementKNN();
		}
	}
}
```

+++
For all the balls, find the k number of closest balls for every ball. The easiest way to do this is to store the distances between every ball and the current ball in an ArrayList of doubles. Sort the ArrayList and return an Ball ArrayList that contains the neighbors with the smallest distances. 

```java
public class KNN
{
	/*
	 * Implementation of a K-Nearest Neighbor Algorithm
	 * Looks at the nearest K neighbors and changes the color to the most common
	 */
	public void implementKNN()
	{
		for(Ball b: allBalls)
		{
			ArrayList<Ball> closestBalls = findNearestNeighbors(b);
		}
	}
	/*
	 * Finds the nearest K neighbors for each Ball
	 * @param b - Ball to find nearest neighbors of
	 * @return - ArrayList of K closest Balls
	 */
	public ArrayList<Ball> findNearestNeighbors(Ball b)
	{
		ArrayList<Double> distances = new ArrayList<Double>();
		for(Ball other: allBalls)
		{
			if (other!=null && b!= null && b != other)
			{
				distances.add(b.getDistance(other));
			}
		}
		Collections.sort(distances); //sorted by smallest to biggest distances
		ArrayList<Ball> nearest = new ArrayList<Ball>();
		//get numNearestNeighbors closest Neighbors
		for(int i = 0; i<numNearestNeighbors; i++)
		{
			double distToFind = distances.get(i);
			for(Ball ball: allBalls)
			{
				if (ball!=null && b!= null && b != ball)
				{
					if(b.getDistance(ball) == distToFind)
					{
						nearest.add(ball);
					}
				}
			}
		}
		return nearest;
	}
}
```

+++

You will then determine which of the two colors each ball has. Set the current ball to have the most common color of those neighbors.

```java
public class KNN
{
	public void implementKNN()
	{
		for(Ball b: allBalls)
		{
			ArrayList<Ball> closestBalls = findNearestNeighbors(b);
			int greenCount = 0, blueCount = 0;
			for(Ball c : closestBalls)
			{
				if(c.color.equals("Green"))
				{
					greenCount++;
				}
				else
				{
					blueCount++;
				}
			}
			if(greenCount > blueCount)
			{
				b.color = "Green";			
			}
			else
			{
				b.color = "Blue";
			}
		}
	}
}
```

---
@title "Conclusion"

You have now successfully implemented a K-Nearest Neighbors algorithm. k-NN can be used for a variety of purposes in Machine Learning and is definitely a technique you should explore further if it interests you. 