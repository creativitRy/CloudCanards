package com.cloudcanards;

import com.cloudcanards.algorithms.kd.KDTree;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.text.DecimalFormat;

/**
 * KDTest
 *
 * @author creativitRy
 */
public class RangeQueryTest
{
	public static void main(String[] args)
	{
		Timer timer = new Timer();
		Array<Vector2> vectors;
		/*vectors = Array.with(
			new Vector2(2, 3),
			new Vector2(5, 4),
			new Vector2(9, 6),
			new Vector2(4, 7),
			new Vector2(8, 1)
		);*/
		//System.out.println(vectors);
		Array<Vector2> temp = new Array<>(40000);
		
		for (int i = 500; i <= 100000; i += 500)
		{
			vectors = generateRandomVectors(i);
			KDTree<Vector2> tree = new KDTree<Vector2>(vectors)
			{
				@Override
				protected Vector2 getPosition(Vector2 element)
				{
					return element;
				}
			};
			timer.start();
			//System.out.println(tree);
			tree.getAllElementsInBox(temp, 40, 40, 60, 60);
			System.out.print("K-D Tree: ");
			timer.stop();
			
			timer.start();
			temp.clear();
			for (Vector2 vector : vectors)
			{
				if (40 <= vector.x && 40 <= vector.y &&
					vector.x <= 60 && vector.y <= 60)
					temp.add(vector);
			}
			System.out.print("Brute Force: ");
			timer.stop();
			
			System.out.println(i);
			System.out.println();
		}
		
		//System.out.println(temp);
		
	}
	
	private static Array<Vector2> generateRandomVectors(int length)
	{
		Array<Vector2> vectors = new Array<>(length);
		for (int i = 0; i < length; i++)
		{
			vectors.add(new Vector2((float) Math.random(), (float) Math.random()).scl(100f));
		}
		
		return vectors;
	}
	
	static class Timer
	{
		long startTime;
		double deltaTime;
		
		public Timer()
		{
		}
		
		public void start()
		{
			startTime = System.nanoTime();
		}
		
		public void stop()
		{
			deltaTime = (System.nanoTime() - startTime) / 1000000000.0;
			System.out.println("Time : " + new DecimalFormat("#.##########").format(deltaTime) + " Seconds");
		}
	}
}
