package SVMProject;

import weka.core.Instances;
import weka.classifiers.functions.*; 
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.SelectedTag;

import java.io.*;

import apcs.Window;

public class SVM {
	private static int goodPopulationSize = 4;
	private static int badPopulationSize = 4;
	private double rMidX, rMidY, gMidX, gMidY, allMidX, allMidY;
	private double previousBestScore, previousBestSlope,previousBestYInt;
	private static Particle[] allParticles;
	public static void main(String[] args)
	{
		Window.size(600, 600);	
		Window.setFrameRate(50);
		//create arrays of all particles
		SVM b = new SVM();
		b.runSimulation();
	}
	/**
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
	 * Method that runs through each frame and determines the best fit line
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
			PrintWriter pw = openToWrite("data.csv"); //Easiest to use .csv file in SMO for Weka
			pw.println("X,Y,Good");
			for(Particle p: allParticles)
			{
				pw.println(p.x + "," + p.y + "," + p.good);
			}
			pw.close();
			//drawBestWekaLine();
			drawBestLine();
			drawBestLineMidPoints();
		}		
	}
	/*
	 * Uses the Weka Library to draw the best fit line
	 */
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
		Window.out.color("White");
		double startX = 0; ////xX + yY + c = 0.. yY = -xX -c, Y = (-xX-c)/y
		double startY = -c/yCoefficient;
		double endX = Window.width();
		double endY = ((-1 * xCoefficient* endX) - c)/yCoefficient;
		Window.out.line(startX,startY,endX,endY); 
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
        }
        catch(Exception e)
        {
            System.out.println("\n Error: File could not be created " + fileString);
            System.exit(1);
        }
        return outFile;
    }  
    /*
     * Method to draw best fit line using midpoints
     */
	public void drawBestLineMidPoints()
	{
		//find midpoints for good and bad particles
		findMidPoints(true); 
		findMidPoints(false);
		double slope = (gMidY - rMidY)/(rMidY-rMidX); //slope of line from midpoints of good particles to bad
		double perpSlope = -1/slope; // line is perpendicular to above line and through allMid;
		
		//point (allMidX, allMidY), slope=perpSlope :: y - allMidY = perpSlope(x-allMidX) :: y = perpSlope(x-allMidX) + allMidY
		double startX = 0;
		double startY = perpSlope * -1 * allMidX + allMidY;
		double endX = Window.width();
		double endY = perpSlope*(endX-allMidX) + allMidY;
		Window.out.color("Orange");
		Window.out.line(startX, startY, endX, endY);
	}
	/*
	 * Method to draw best fit line. Uses scoring system to determine best of 10,000 generated lines
	 */
	public void drawBestLine()
	{
		double bestSlope = 0;
		double bestYInt = 0;
		double bestScore = 0;
		for(int i =0; i<10000; i++)
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
			double score = calculateScore(slope, yInt);
			//0<= slope*x + c <=Window.height() :: Making sure line will actually be visible
			int count = 0;
			for(int a =0; a<Window.height(); a++)
			{
				if(!(slope*a + yInt >= 0 && slope *a + yInt <= Window.width())) //if not on screen
				{
					count++;
				}
			}
			if(count > Window.width() *9/10) // more than 90% points not on screen
			{
				score = score - 10000; //not going to be a good fit
			}
			if(score > bestScore)
			{
				bestScore = score;
				bestSlope = slope;
				bestYInt = yInt;
			}
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
		//y = slope * x  +yInt
		double startX = 0;
		double startY = bestYInt;
		double endX = Window.width();
		double endY = bestSlope * endX + bestYInt;
		Window.out.color("Green");
		Window.out.line(startX, startY, endX, endY);
	}
	/*
	 * Method to calculate score for line
	 * @param slope - slope of line
	 * @param yInt - line's y-intercept
	 * @return - score for line
	 */
	public double calculateScore(double slope, double yInt)
	{
		double score = 0;
		//majority of good particles on one side, bad particles on other. necessary to ensure there are no lines in corners where every single particle is on one side and nothing is on the other
		double goodLeftOfLine = 0;
		double badLeftOfLine = 0;
		for(Particle p:allParticles)
		{
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
		double goodRightOfLine = goodPopulationSize - goodLeftOfLine; //if the particles are not left of the line, they are right of the line
		double badRightOfLine = badPopulationSize - badLeftOfLine;
		if(goodRightOfLine > goodLeftOfLine && badLeftOfLine > badRightOfLine)
		{
			score += 500; 
		}
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
		//maximizing the distances between nearest data point (either class) and line - https://www.analyticsvidhya.com/blog/2015/10/understaing-support-vector-machine-example-code/
		Particle nearest = findNearestParticle(slope,yInt);
		//distance from point to line is |a(x1) + b(y1) + c|/(sqrt(a^2+b^2), y = mx + c :: mx -y + c = 0
		score += calculateDistance(slope, yInt, nearest); 
		return score;
	}
	/*
	 * Finds the closest Particle to a line
	 * @param slope - slope of line
	 * @param yInt - line's y-intercept
	 * @return Particle - closest Particle
	 */
	public Particle findNearestParticle(double slope, double yInt)
	{
		Particle nearest = null;
		double closestDist = 999999999;
		for(Particle p: allParticles)
		{
			double dist = calculateDistance(slope,yInt,p);
			if(dist < closestDist)
			{
				closestDist = dist;
				nearest = p;
			}
		}
		return nearest;
	}
	//https://stackoverflow.com/questions/1560492/how-to-tell-whether-a-point-is-to-the-right-or-left-side-of-a-line
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
			for(int i =0; i<goodPopulationSize; i++) // if good, iterate through this part of the allParticles array
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
	 * Check if particle is touching another particle
	 * @param other - other particle to check
	 * @return - true if touching, false if not
	 */
	public boolean isTouching(Particle other) {
		double distance = Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
		// Return whether or not distance is less than or equal to 10
		return distance <= 10;
	}
	/*
	 * If the particles are touching, the dx and dy values are swapped
	 * @param other - other Particle to swap with
	 */
	public void contactWith(Particle other) {
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