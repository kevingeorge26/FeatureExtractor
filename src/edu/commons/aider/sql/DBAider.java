package edu.commons.aider.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;

public class DBAider
{
	private static GenericObjectPool connectionPool 	= null;
	private static Properties credentials 				= null;
	private static ConnectionFactory connectionFactory	= null;
	private static PoolingDataSource datasource			= null;
	private static Logger logger 	= Logger.getLogger(DBAider.class.getName());
	private Connection connectionInstance = null;
	private PreparedStatement statement = null;
	private ResultSet set = null;

	public static void init(SettingsLoader settings) {
		try {
			String URL 			= settings.getProperty("configuration.database.protocol") + "://" +
					settings.getProperty("configuration.database.host") + ":" +
					settings.getProperty("configuration.database.port") + "/" +
					settings.getProperty("configuration.database.schema");

			String DRIVER 		= settings.getProperty("configuration.database.driver");
			String USER			= settings.getProperty("configuration.database.user");
			String PASSWORD		= settings.getProperty("configuration.database.password");
			int MAXACTIVE		= Integer.parseInt(settings.getProperty("configuration.database.pooling.maxactive"));
			int MINIDLE			= Integer.parseInt(settings.getProperty("configuration.database.pooling.maxidle"));

			logger.info("Connecting to database " + URL);

			Class.forName(DRIVER).newInstance();
			connectionPool 	= new GenericObjectPool(null);
			credentials 	= new Properties();

			credentials.setProperty("user",USER);
			credentials.setProperty("password",(PASSWORD == null ? "" : PASSWORD));

			connectionPool.setMaxActive(MAXACTIVE);
			connectionPool.setMinIdle(MINIDLE);
			connectionPool.setMaxWait(6000);

			connectionFactory = new DriverManagerConnectionFactory(URL,credentials);
			new PoolableConnectionFactory(connectionFactory,connectionPool,null,null,false,true);
			datasource = new PoolingDataSource(connectionPool);

		} catch (Exception e) {
			logger.error("DBAider static, ",e);
		}
	}

	public DBAider() throws SQLException
	{
		connectionInstance	= datasource.getConnection();
		connectionInstance.setAutoCommit(false);
	}

	public static DataSet read(String qry,List<Object> vars) {
		//logger.debug("read, a query : " + qry);
		try
		{
			Connection connection = datasource.getConnection();
			return basicRead(connection,false, qry, vars);
		} catch (Exception e) {
		}
		return null;
	}

	public static DataSet readAlias(String qry,List<Object> vars) {
		logger.debug("read, a query : " + qry);
		Connection connection       = null;
		try
		{
			connection  = datasource.getConnection();
			return basicRead(connection, true, qry, vars);
		} catch (Exception e) {
		}
		return null;
	}

	private static DataSet basicRead(Connection connection,boolean isAlias, String qry,List<Object> vars)
	{
		PreparedStatement statement = null;
		ResultSet resultset			= null;
		try
		{
			//logger.debug("read, Obtained the connection");
			if(connection != null)
			{
				statement = connection.prepareStatement(qry);
				if(statement != null)
				{
					for(int row = 0;row < vars.size();row++)
						statement.setObject(row + 1, vars.get(row));
					resultset = statement.executeQuery();
					//logger.debug("read, The query was executed successfully");
				}
			}
			return new DataSet(isAlias,resultset);
		}
		catch (SQLException e)
		{
			logger.error("read, ",e);
		}
		finally
		{
			try
			{
				if(resultset != null)
					resultset.close();
				if(statement != null)
					statement.close();
				if(connection != null)
					connection.close();
			}
			catch (NullPointerException e)
			{
				logger.error("read, ",e);
			}
			catch (SQLException e)
			{
				logger.error("read, ",e);
			}
		}
		return null;
	}

	public static DataSet read(String qry,Object... vars) {
		return read(qry, Arrays.asList(vars));
	}

	public static DataSet readAlias(String qry,Object... vars) {
		return readAlias(qry, Arrays.asList(vars));
	}

	public static UpdateResult write(String qry,List<Object> vars) {
		logger.debug("write, an update query : " + qry);
		Connection connection       = null;
		PreparedStatement statement = null;
		UpdateResult result         = null;
		ResultSet set = null;
		try
		{
			connection      = datasource.getConnection();
			logger.debug("write, Obtained the connection");
			if(connection != null)
			{
				statement       = connection.prepareStatement(qry,Statement.RETURN_GENERATED_KEYS);
				if(statement != null)
				{
					for(int row = 0;row < vars.size();row++)
						statement.setObject(row + 1, vars.get(row));
				}
				int ret = statement.executeUpdate();
				logger.debug("write, update query " + qry + " returned : " + ret);
				result = new UpdateResult(false,statement.getGeneratedKeys());
				result.setStatus(ret);
				return result;
			}
		}
		catch (SQLException e)
		{
			logger.error("write, ",e);
		}
		finally
		{
			try
			{
				if(set != null)
					set.close();
				if(statement != null)
					statement.close();
				if(connection != null)
					connection.close();
			}
			catch (NullPointerException e)
			{
				logger.error("write, ",e);
			}
			catch (SQLException e)
			{
				logger.error("write, ",e);
			}
		}
		return null;	    
	}

	public static UpdateResult write(String qry,Object... vars) {
		return write(qry, Arrays.asList(vars));
	}

	public UpdateResult writeWithCommit(String qry,Object... vars)
	{
		logger.debug("writeWithCommit, a update query without commit : " + qry);
		UpdateResult result = null;
		try
		{
			if(connectionInstance != null)
			{
				statement = connectionInstance.prepareStatement(qry,Statement.RETURN_GENERATED_KEYS);
				if(statement != null)
				{
					for(int row = 0;row < vars.length;row++)
						statement.setObject(row + 1, vars[row]);
				}
				int ret = statement.executeUpdate();
				logger.debug("writeWithCommit, update query " + qry + " returned : " + result);
				result = new UpdateResult(false,statement.getGeneratedKeys());
				result.setStatus(ret);
				return result;
			}
		}
		catch (SQLException e)
		{
			logger.error("writeWithCommit, ",e);
		}
		finally
		{
			try
			{
				if(set != null)
					set.close();
				if(statement != null)
					statement.close();
			}
			catch (NullPointerException e)
			{
				logger.error("writeWithCommit, ",e);
			}
			catch (SQLException e)
			{
				logger.error("preparedWriteWithoutAutoCommit, ",e);
			}
		}
		return null;
	}

	public boolean commit()
	{
		boolean isCommit = false;
		try
		{
			logger.debug("commit, is inside commit function");
			if(connectionInstance != null)
			{
				connectionInstance.commit();
				connectionInstance.close();
				connectionInstance = null;
				logger.debug("commit, committed a query");
			}
			isCommit = true;
		}
		catch (SQLException e)
		{
			isCommit = false;
			logger.error("commit, ",e);
		}
		return isCommit;
	}

	public boolean rollback()
	{
		boolean isRollback = false;
		try
		{
			logger.debug("rollback, is inside rollback function");
			if(connectionInstance != null)
			{
				connectionInstance.rollback();
				connectionInstance.close();
				connectionInstance = null;
				logger.debug("rollback, rollbacked from a query");
			}
			isRollback = true;
		}
		catch (SQLException e)
		{
			isRollback = false;
			logger.error("rollback, ",e);
		}
		return isRollback;
	}
}
