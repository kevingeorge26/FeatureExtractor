package edu.pos.feature.store;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.commons.aider.sql.DBAider;
import edu.commons.aider.sql.DataSet;
import edu.pos.feature.FeatureAggregator;
import edu.pos.feature.FeatureItemSet;
import edu.pos.feature.extractor.Tagger;


// build the feature set for a store
public class Store 
{

	private final float minSup = 0.2f;
	private String storeID;
	FeatureAggregator featureAggregator;
	

	public Store(String storeID)
	{
		featureAggregator = new FeatureAggregator(minSup);
		this.storeID = storeID;
	}




	public Set<FeatureItemSet> getFeatureForStoreFromDB()
	{

		DataSet ds = DBAider.read("select text from review where store_id=? and rating=?",storeID,"five");
		int rowCount = ds.rowSize();
		
		

		for(int i = 0 ; i < rowCount ; i++)
		{
			Set<String> features = new HashSet<String>();
			Tagger.getFeatures(features, ds.getValue(i, "text"));
			
			featureAggregator.addToList(features);
		}


		return featureAggregator.sortFeatureAboveThreshold(rowCount);
	}
	
	public void addReview(String review)
	{
		Set<String> features = new HashSet<String>();
		Tagger.getFeatures(features, review);
		
		featureAggregator.addToList(features);
	}
	
	public Set<FeatureItemSet> getAggregatedFeature(int totalReview)
	{
		return featureAggregator.sortFeatureAboveThreshold(totalReview);
	}







}
