package edu.pos.feature;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class FeatureAggregator 
{

	private HashMap<String, FeatureItemSet> allFeatures = new HashMap<String, FeatureItemSet>();
	private Set<FeatureItemSet> sortedList = new HashSet<FeatureItemSet>();
	
	float minSup;
	

	public FeatureAggregator(float minSup) 
	{
		this.minSup = minSup;
	}
	
	public void addToList(Set<String> features)
	{
		for(String toAdd : features)
		{
			if ( allFeatures.containsKey(toAdd) )
			{
				allFeatures.get(toAdd).incrementCount();
			}
			else
			{
				FeatureItemSet newFeature = new FeatureItemSet(toAdd);
				allFeatures.put(toAdd, newFeature);
			}
		}
	}

	public Set<FeatureItemSet> sortFeatureAboveThreshold(int numberOfreviews)
	{
		int threshold = (int) (numberOfreviews * minSup);

		threshold = threshold<3?3:threshold;
		
		Set<String> keySet = allFeatures.keySet();
		for( String key : keySet )
		{
			FeatureItemSet temp = allFeatures.get(key);

			if(temp.getCount() >= threshold)
			{
				sortedList.add(temp);
			}


		}
		
		return sortedList;
	}

	// return the features that are above the threshold
	// this functio should be called only after sortFeatureAboveThreshold is called 
	public Set<FeatureItemSet> getSortedList() {
		return sortedList;
	}
	
	


}
