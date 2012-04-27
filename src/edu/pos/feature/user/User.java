package edu.pos.feature.user;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.commons.aider.sql.DBAider;
import edu.commons.aider.sql.DataSet;
import edu.pos.feature.FeatureAggregator;
import edu.pos.feature.FeatureItemSet;
import edu.pos.feature.extractor.Main;
import edu.pos.feature.extractor.Tagger;


public class User 
{
	private static Logger logger 	= Logger.getLogger(User.class.getName());
	
	String userID;
	final float minSup = 0.2f;
	FeatureAggregator featureAggregator;

	public User( String userID )
	{
		this.userID = userID;
		featureAggregator = new FeatureAggregator(minSup);

		getUserFeatures();
	}



	private void getUserFeatures()
	{
		DataSet ds = DBAider.read("select text from review where reviewer_id = ? and rating=?", userID , "five" );
		int rowCount = ds.rowSize();

		for(int i = 0 ; i < rowCount ; i++ )
		{
			Set<String> features = new HashSet<String>();
			Tagger.getFeatures(features, ds.getValue(i, "text"));

			featureAggregator.addToList(features);

		}

		featureAggregator.sortFeatureAboveThreshold(rowCount );

		//logger.debug(featureAggregator.getSortedList());
	}

	public boolean probableCustomer(Set<FeatureItemSet> storeFeatures,int commonFeatures)
	{
		boolean status = false;
	
		int count = 0;
		
		Set<FeatureItemSet> customerfeature = featureAggregator.getSortedList();

		for(FeatureItemSet feature : storeFeatures)
		{
			if( customerfeature.contains(feature) )
			{
				count++;
			}
		}
		
		if(count >= commonFeatures)
			status = true;

		return status;
	}

}
