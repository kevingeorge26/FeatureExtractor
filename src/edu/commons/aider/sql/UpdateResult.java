package edu.commons.aider.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateResult extends DataSet
{
    private int status;

	public UpdateResult(boolean isAlias, ResultSet result) throws SQLException {
        super(isAlias, result);
    }

    public int getStatus()
	{
		return status;
	}
	
	void setStatus(int status)
	{
		this.status = status;
	}
}
