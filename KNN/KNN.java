import apcs.Window;
import java.util.*;
public class KNN {
	private static int numberOfBalls = 300;
	private static int numNearestNeighbors = 5;
	private Ball[] allBalls;
	public static void main(String[] args)
	{
		Window.size(600, 600);	
		//Window.setFrameRate(50);
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
	/*
	 * Implementation of a K-Nearest Neighbor Algorithm
	 * Looks at the nearest K neighbors and changes the color to the most common
	 */
	public void implementKNN()
	{
		double sumGreen = 0,sumBlue = 0;
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
				sumGreen++;
				
			}
			else
			{
				b.color = "Blue";
				sumBlue++;
			}
		}
		System.out.println(sumGreen/allBalls.length + " " + sumBlue/allBalls.length);
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