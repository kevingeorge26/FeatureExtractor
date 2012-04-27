package edu.pos.feature;

public class FeatureItemSet
{
	String feature;
	int count;
	
	public FeatureItemSet(String feature) 
	{
		this.feature = feature;
		count = 1;
	}

	public void incrementCount()
	{
		count++;
	}
	
	

	public String getFeature() {
		return feature;
	}



	public int getCount() {
		return count;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((feature == null) ? 0 : feature.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FeatureItemSet other = (FeatureItemSet) obj;
		if (feature == null) {
			if (other.feature != null)
				return false;
		} else if (!feature.equals(other.feature))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FeatureItemSet [feature=" + feature + ", count=" + count + "]";
	}
	
	
	
	



}
