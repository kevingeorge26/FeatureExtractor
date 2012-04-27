package edu.commons.aider.sql;

import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class DataSet implements Iterable<DataRow>
{
    private static Logger logger 	= Logger.getLogger(DataSet.class.getName());
    private static final long serialVersionUID 	= 1L;
    private ArrayList<String> columnList = null;
    private ArrayList<DataRow> rows		= null;

    public DataSet(boolean isAlias,ResultSet result) throws SQLException {
        super();
        makeDataSet(isAlias,result);
    }

    private void makeDataSet(boolean isAlias,ResultSet result) throws SQLException
    {
        try
        {
            DataRow hashresult = null;
            boolean once = true;

            rows = new ArrayList<DataRow>();
            columnList = new ArrayList<String>();
            ResultSetMetaData meta = result.getMetaData();
            while(result.next())
            {
                hashresult = new DataRow();
                for(int i = 1;i <= meta.getColumnCount();i++)
                {
                    String column = meta.getColumnLabel(i);
                    if(isAlias) {
                        if(meta.getTableName(i) != null && meta.getTableName(i).length() > 0)
                            column = meta.getTableName(i) + "." + column;
                    }
                    String value = result.getString(i);
                    if(column != null && !hashresult.containsKey(column))
                    {
                        if(once)
                            columnList.add(column);
                        hashresult.put(column,value);
                    }
                }
                rows.add(hashresult);
                once = false;
            }
        }
        catch(Exception ex)
        {
            logger.error("makeDataSet, ",ex);
        }
    }

    private void validate(ArrayList<DataRow> row,ArrayList<String> column)
    {
        if(row == null || column == null)
        {
            logger.debug("validate, row and column are null, so NullPointerException is being thrown");
            throw new NullPointerException();
        }
    }

    private boolean validate(int row1,int row2)
    {
        if(rows != null)
        {
            if((row1 >= 0 && row1 < rows.size()) && (row2 >= 0 && row2 < rows.size()))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    public void swap(int row1,int row2)
    {
        validate(rows,columnList);
        if(validate(row1,row2))
        {
            DataRow dataRow1 = rows.get(row1);
            DataRow dataRow2 = rows.get(row2);

            rows.set(row1, dataRow2);
            rows.set(row2, dataRow1);
        }
    }

    public String getColumnName(int index)
    {
        try
        {
            if(columnList != null)
            {
                return columnList.get(index);
            }
        }
        catch(IndexOutOfBoundsException e)
        {
            logger.error("getColumnName, ",e);
        }
        return null;
    }

    public DataRow getRow(int row)
    {
        try
        {
            if(rows != null)
            {
                logger.debug("getRow, returning the DataRow");
                return rows.get(row);
            }
        }
        catch(IndexOutOfBoundsException e)
        {
            logger.error("getRow, ",e);
        }
        return null;
    }

    public String getValue(int row,String column)
    {
        try
        {
            if(rows != null)
            {
                DataRow drow = rows.get(row);
                if(drow.containsKey(column))
                    return drow.get(column);
            }
        }
        catch(IndexOutOfBoundsException e)
        {
            logger.error("getValue, ",e);
        }
        return null;
    }

    public String getValue(int row,int column)
    {
        try
        {
            if(rows != null)
            {
                return rows.get(row).getAt(column);
            }
        }
        catch(IndexOutOfBoundsException e)
        {
            logger.error("getValue, ",e);
        }
        return null;
    }

    public String setValue(int row,String column,String value)
    {
        try
        {
            if(columnList != null)
            {
                if(columnList.contains(column))
                {
                    return rows.get(row).put(column, value);
                }
            }
            else
                logger.debug("setValue, columnlist is empty");
        }
        catch(Exception ex)
        {
            logger.error("setValue, ",ex);
        }
        return null;
    }

    public boolean isEmpty()
    {
        if(rows != null)
        {
            return rows.isEmpty();
        }
        return true;
    }

    public DataRow remove(int index)
    {
        if(rows != null)
            return rows.remove(index);
        return null;
    }

    public int columnSize()
    {
        if(columnList != null)
            return columnList.size();
        return 0;
    }

    public int rowSize()
    {
        if(rows != null)
            return rows.size();
        return 0;
    }

    public Iterator<DataRow> iterator()
    {
        if(rows != null)
            return rows.iterator();
        return null;
    }

    public DataRow createDataRow()
    {
        return new DataRow();
    }

    private boolean validate(DataRow row)
    {
        if(row == null)
        {
            return false;
        }
        else
        {
            if(columnList != null)
            {
                if(row.size() != columnList.size())
                {
                    return false;
                }
                for(String key : columnList)
                {
                    if(!row.containsKey(key))
                        return false;
                }
            }
            else
            {
                return false;
            }
        }
        return true;
    }

    public boolean insertRow(DataRow row)
    {
        if(validate(row) && rows != null)
        {
            rows.add(row);
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean validate(DataSet set)
    {
        if(set != null && !set.isEmpty())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean append(DataSet set)
    {
        if(validate(set) && rows != null)
        {
            for(DataRow row : set)
                rows.add(row);
            return true;
        }
        else
            logger.debug("append, could not append due to invalid dataset entry");
        return false;
    }

    public Document toXml(String rowName)
    {
        try
        {
            if(!this.isEmpty())
            {
                DocumentBuilderFactory factory 	= DocumentBuilderFactory.newInstance();
                DocumentBuilder builder 		= factory.newDocumentBuilder();
                Document xmldocument 			= builder.newDocument();

                for(int row = 0;row < rowSize();row++)
                {
                    DataRow table = this.getRow(row);
                    Node root = xmldocument.createElement(rowName);
                    xmldocument.appendChild(root);

                    Object[] keys = table.keySet().toArray();
                    for(int i = 0;i < keys.length;i++)
                    {
                        String column 	= keys[i].toString();
                        String value	= table.get(column);
                        Node node1 		= xmldocument.createElement(column);
                        Node node2		= xmldocument.createTextNode(value);
                        node1.appendChild(node2);
                        root.appendChild(node1);
                    }
                }
                return xmldocument;
            }
            else
                logger.debug("toXml, this dataset is empty");
        }
        catch (Exception e)
        {
            logger.error("toXml, ",e);
        }
        return null;
    }

    public Document toXml()
    {
        try
        {
            if(!this.isEmpty())
            {
                DocumentBuilderFactory factory 	= DocumentBuilderFactory.newInstance();
                DocumentBuilder builder 		= factory.newDocumentBuilder();
                Document xmldocument 			= builder.newDocument();

                for(int row = 0;row < rowSize();row++)
                {
                    DataRow table = this.getRow(row);
                    Node root = xmldocument.createElement("datarow");
                    xmldocument.appendChild(root);

                    Object[] keys = table.keySet().toArray();
                    for(int i = 0;i < keys.length;i++)
                    {
                        String column 	= keys[i].toString();
                        String value	= table.get(column);
                        Node node1 		= xmldocument.createElement(column);
                        Node node2		= xmldocument.createTextNode(value);
                        node1.appendChild(node2);
                        root.appendChild(node1);
                    }
                }
                return xmldocument;
            }
            else
                logger.debug("toXml, this dataset is empty");
        }
        catch (ParserConfigurationException e)
        {
            logger.error("toXml, ",e);
        }
        return null;
    }

    public String toString()
    {
        StringWriter stringwriter = null;
        if(rows != null && !rows.isEmpty())
        {
            stringwriter = new StringWriter();
            for(DataRow row : rows)
            {
                for(String key : row.keySet())
                {
                    stringwriter.write(row.get(key) + "    ");
                }
                stringwriter.write(System.getProperty("line.separator"));
            }
        }
        if(stringwriter != null)
            return stringwriter.toString();
        return null;
    }
}
