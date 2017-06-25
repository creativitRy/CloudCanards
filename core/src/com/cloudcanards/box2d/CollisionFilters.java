package com.cloudcanards.box2d;

/**
 * To be used alongside with {@link com.badlogic.gdx.physics.box2d.Filter filters}
 * <p>
 * Bits can range from (1 << 0) to (1 << 15), increasing by (1 << x)
 *
 * @author creativitRy
 */
public class CollisionFilters
{
	public static final short DEFAULT = (1);
	public static final short CHARACTER = (short) (1 << 1);
	public static final short ROPE = (short) (1 << 15);
	
	public static final short NONE = 0;
	public static final short ALL = -1;
	
	private CollisionFilters() {}
	
	/**
	 * Exclude the given bits from the bit field
	 * <p>
	 * {@code bitField &= ~bit}
	 *
	 * @param bitField bit field to exclude bits from
	 * @param bits     bits to exclude
	 * @return bit field with the bits excluded
	 */
	public static short exclude(short bitField, short... bits)
	{
		for (short bit : bits)
		{
			bitField &= ~bit;
		}
		return bitField;
	}
	
	/**
	 * include the given bits from the bit field
	 * <p>
	 * {@code bitField |= bit}
	 *
	 * @param bitField bit field to include bits from
	 * @param bits     bits to include
	 * @return bit field with the bits included
	 */
	public static short include(short bitField, short... bits)
	{
		for (short bit : bits)
		{
			bitField |= bit;
		}
		return bitField;
	}
	
	/**
	 * toggle the given bits from the bit field
	 * <p>
	 * {@code bitField ^= bit}
	 *
	 * @param bitField bit field to toggle bits from
	 * @param bits     bits to toggle
	 * @return bit field with the bits toggled
	 */
	public static short toggle(short bitField, short... bits)
	{
		for (short bit : bits)
		{
			bitField ^= bit;
		}
		return bitField;
	}
	
	/**
	 * toggles all bits in the bit field
	 * <p>
	 * {@code ~bitField}
	 *
	 * @param bitField bit field to toggle
	 * @return toggled bit field
	 */
	public static short toggleAll(short bitField)
	{
		return (short) ~bitField;
	}
	
	/**
	 * Checks whether the given bit is set in the bit field
	 * <p>
	 * {@code (bitField & bit) != 0}
	 *
	 * @param bitField bit field to check if bit is set
	 * @param bit      bits to check
	 * @return true if bit field includes the bit
	 */
	public static boolean check(short bitField, short bit)
	{
		return (bitField & bit) != 0;
	}
}
