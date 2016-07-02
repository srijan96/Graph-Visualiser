import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import javax.swing.JFrame;
import javax.swing.JPanel;
class Point
{
	public int x;
	public int y;
	Point(int x,int y){ this.x = x;this.y = y;	}
}
class Edge
{
	public int s;
	public int e;
	Edge(int x,int y){	s = x;e = y;	}
}
public class GraphGen
{
	private static int numEdges;
	private static Edge edges[];
	public static void main(String args[])
	{
		int numNodes = 20;
		int maxDegree = 12;	
		if(args.length >=2)
		{
			numNodes = Integer.parseInt(args[0]);	
			maxDegree = Integer.parseInt(args[1]);			
		}
		System.out.println(numNodes);
		generate(numNodes,maxDegree);
	}
	private static void generate(int numNodes,int maxDegree)
	{
		int degree[] = new int[numNodes];
		int sum = 0;
		for(int i=0;i<numNodes;i++)
		{
			int d = rand(maxDegree);
			sum += d;
			degree[i] = d;
		}
		if(sum%2 != 0)
		{
			sum++;
			degree[rand(maxDegree)]++;
		}
		edges = new Edge[sum/2];
		numEdges = sum/2;
		int e = 0;
		while(sum > 0)
		{
			int a = (int) (numNodes * Math.random());
			int b = (int) (numNodes * Math.random());			
			if(a==numNodes || b==numNodes)	continue;
			if(degree[a]==0 || degree[b]==0) continue;
			degree[a]--;
			degree[b]--;
			System.out.println(a+","+b);
			sum -= 2;
			edges[e] = new Edge(a,b);
			e++;
		}
	}
	private static int rand(int maxDegree)
	{
		double cycleLength = 0;
		int i;
		for(i=0;i<maxDegree;i++)	cycleLength += pdf(i,maxDegree);
		double arc = Math.random() * cycleLength;
		double sum = pdf(-1,maxDegree);
		for(i=0;i<maxDegree && sum < arc;i++)	sum += pdf(i,maxDegree);
		if(i>=maxDegree)	i = maxDegree - 1;
		return i;
	}
	private static double pdf(int x,int maxDegree)
	{
		double mu = (maxDegree-1)/2;
		double sigma = mu/2;
		double t = -1 * (x-mu)*(x-mu)/(sigma*sigma);
		return Math.exp(t);
	}
}
