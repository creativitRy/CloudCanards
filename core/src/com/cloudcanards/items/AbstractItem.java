package com.cloudcanards.items;

/**
 * AbstractItem
 *
 * @author creativitRy
 */
public abstract class AbstractItem
{
	/**
	 * file name
	 */
	private String id;
	
	/**
	 * name of the item
	 */
	private String name;
	
	/**
	 * multiline description
	 */
	private String description;
	
	/**
	 * category
	 */
	private ItemCategory category;
	
	/**
	 * abstract value of item - may be used in stores to calculate price
	 */
	private int value;
	
	//todo: something about the texture
	
	public AbstractItem()
	{
	
	}
	
	public void setId(String id)
	{
		this.id = id;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public void setCategory(ItemCategory category)
	{
		this.category = category;
	}
	
	public void setValue(int value)
	{
		this.value = value;
	}
}
