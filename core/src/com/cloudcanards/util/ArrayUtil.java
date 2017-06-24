package com.cloudcanards.util;

import com.badlogic.gdx.utils.Array;

import java.util.Comparator;

/**
 * ArrayUtil
 *
 * @author creativitRy
 */
public class ArrayUtil
{
	private ArrayUtil() {}
	
	/**
	 * Searches the specified list for the specified object using the binary
	 * search algorithm.  The list must be sorted into ascending order
	 * according to the specified comparator, prior to making this call.  If it is
	 * not sorted, the results are undefined.  If the list contains multiple
	 * elements equal to the specified object, there is no guarantee which one
	 * will be found.
	 * <p>
	 * <p>This method runs in log(n) time for a "random access" list (which
	 * provides near-constant-time positional access).
	 *
	 * @param <T>   the class of the objects in the list
	 * @param array the list to be searched.
	 * @param key   the key to be searched for.
	 * @return the index of the search key, if it is contained in the list;
	 * otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>.  The
	 * <i>insertion point</i> is defined as the point at which the
	 * key would be inserted into the list: the index of the first
	 * element greater than the key, or <tt>list.size()</tt> if all
	 * elements in the list are less than the specified key.  Note
	 * that this guarantees that the return value will be &gt;= 0 if
	 * and only if the key is found.
	 * @throws ClassCastException if the list contains elements that are not
	 *                            <i>mutually comparable</i> using the specified comparator,
	 *                            or the search key is not mutually comparable with the
	 *                            elements of the list using this comparator.
	 */
	public static <T> int binarySearch(Array<? extends Comparable<? super T>> array, T key)
	{
		int low = 0;
		int high = array.size - 1;
		
		while (low <= high)
		{
			int mid = (low + high) >>> 1;
			Comparable<? super T> midVal = array.get(mid);
			int cmp = midVal.compareTo(key);
			
			if (cmp < 0)
				low = mid + 1;
			else if (cmp > 0)
				high = mid - 1;
			else
				return mid; // key found
		}
		return -(low + 1);  // key not found
	}
	
	/**
	 * Searches the specified list for the specified object using the binary
	 * search algorithm.  The list must be sorted into ascending order
	 * according to the specified comparator, prior to making this call.  If it is
	 * not sorted, the results are undefined.  If the list contains multiple
	 * elements equal to the specified object, there is no guarantee which one
	 * will be found.
	 * <p>
	 * <p>This method runs in log(n) time for a "random access" list (which
	 * provides near-constant-time positional access).
	 *
	 * @param <T>        the class of the objects in the list
	 * @param array      the list to be searched.
	 * @param key        the key to be searched for.
	 * @param comparator the comparator by which the list is ordered.
	 *                   A <tt>null</tt> value indicates that the elements'
	 *                   {@linkplain Comparable natural ordering} should be used.
	 * @return the index of the search key, if it is contained in the list;
	 * otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>.  The
	 * <i>insertion point</i> is defined as the point at which the
	 * key would be inserted into the list: the index of the first
	 * element greater than the key, or <tt>list.size()</tt> if all
	 * elements in the list are less than the specified key.  Note
	 * that this guarantees that the return value will be &gt;= 0 if
	 * and only if the key is found.
	 * @throws ClassCastException if the list contains elements that are not
	 *                            <i>mutually comparable</i> using the specified comparator,
	 *                            or the search key is not mutually comparable with the
	 *                            elements of the list using this comparator.
	 */
	@SuppressWarnings("unchecked")
	public static <T> int binarySearch(Array<? extends T> array, T key, Comparator<? super T> comparator)
	{
		if (comparator == null)
			return binarySearch((Array<? extends Comparable<? super T>>) array, key);
		
		int low = 0;
		int high = array.size - 1;
		
		while (low <= high)
		{
			int mid = (low + high) >>> 1;
			T midVal = array.get(mid);
			int cmp = comparator.compare(midVal, key);
			
			if (cmp < 0)
				low = mid + 1;
			else if (cmp > 0)
				high = mid - 1;
			else
				return mid; // key found
		}
		return -(low + 1);  // key not found
	}
}
