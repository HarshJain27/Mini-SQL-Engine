package Common;

import java.util.ArrayList;
import java.util.List;

import gudusoft.gsqlparser.nodes.TJoin;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

public class TwoTablesWithWhereClause {
	
	static boolean checkColumnFlag;
	static List<String> columnNames;

	public static void AnalyseSelect(TSelectSqlStatement stmt) {
		// TODO Auto-generated method stub
		TJoin Join1 = stmt.joins.getJoin(0);
		String Table1 = Join1.getTable().toString();
		
		TJoin Join2 = stmt.joins.getJoin(1);
		String Table2 = Join2.getTable().toString();
		
		List<String> T1=Input.tablesInfo.get(Table1);
		if(T1==null) { System.out.println("Table doesn't exist...."); return;}
		
		List<String> T2=Input.tablesInfo.get(Table2);
		if(T2==null) { System.out.println("Table doesn't exist...."); return;}
		
		boolean selectAllFlag=false;
		List<List<Integer>> allRows1 = new ArrayList<List<Integer>>(Input.tuplesInfo.get(Table1));

		List<List<Integer>> allRows2 = new ArrayList<List<Integer>>(Input.tuplesInfo.get(Table2));

		
		if(allRows1==null|| allRows2==null)
		{
			System.out.print("Table Empty...");
			return;
		}
		
		List<List<Integer>>rowValues1=null;
		List<List<Integer>>rowValues2=null;
		
		columnNames = new ArrayList<String>();
		
		if(stmt.getResultColumnList().size()==1)
		{
			
			TResultColumn resultColumn =stmt.getResultColumnList().getResultColumn(0);
			String temp = resultColumn.getExpr().toString();
			
			if(temp.toString().equals("*")) 
			{
				selectAllFlag=true;
				
				rowValues1 = new ArrayList<List<Integer>>(allRows1);
				rowValues2 = new ArrayList<List<Integer>>(allRows2);
						
				for(int u=0;u< Input.tablesInfo.get(Table1).size();u++)
				{
					columnNames.add(Table1+"."+Input.tablesInfo.get(Table1).get(u));
				}
				for(int u=0;u<Input.tablesInfo.get(Table2).size();u++)
				{
					columnNames.add(Table2+"."+Input.tablesInfo.get(Table2).get(u));
				}
			}
		}
		
		if(!selectAllFlag)
		{
			
			rowValues1 = new ArrayList<List<Integer>>();
			rowValues2 = new ArrayList<List<Integer>>();			
			
			for(int j=0; j < stmt.getResultColumnList().size();j++)
				{
					
					TResultColumn resultColumn =stmt.getResultColumnList().getResultColumn(j);
					String columnName = resultColumn.getExpr().toString();
					
					if(columnName.contains("."))
					{
						if(columnName.contains(Table1+"."))
						{
							feedValues(columnName,Table1,allRows1,rowValues1);
						}
						if(columnName.contains(Table2+"."))
						{
							feedValues(columnName, Table2,allRows2,rowValues2);
						}
					}
					else
					{
						feedValues(columnName, Table1,allRows1,rowValues1);
					}
					
					if(!checkColumnFlag)
					{
						feedValues(columnName, Table2,allRows2,rowValues2);
					}
					if(!checkColumnFlag)
					{
						System.out.print("Invalid Column Name");
						return;
					}
		
				}
					
			}
		
	/*********************************************************************************************************/
	List<List<Integer>> result = new ArrayList<List<Integer>>();
	for(int i=0;i<rowValues1.size();i++)
	{
		for(int j=0;j<rowValues2.size();j++)
		{	
			List<Integer> temp1 = new ArrayList<Integer>(rowValues1.get(i));
			List<Integer> temp2 = new ArrayList<Integer>(rowValues2.get(j));
			for(int k=0;k<temp2.size();k++)
			{
				temp1.add(temp2.get(k));
			}
			result.add(temp1);
			
		}
	}
	/*********************************************************************************************************/
	PrintTuples.printDataFromTwoTablesWithoutWhere(columnNames, result);	
  }


	private static void feedValues(String columnName, String Table, List<List<Integer>> allRows, List<List<Integer>> rowValues) {
		// TODO Auto-generated method stub
		columnName=columnName.substring(columnName.indexOf(".")+1);
		
		for(int i=0;i<Input.tablesInfo.get(Table).size();i++)
		{
			if(Input.tablesInfo.get(Table).get(i).equals(columnName))
			{
				for(int k=0;k<allRows.size();k++)
				{
					List<Integer> temp1 = allRows.get(k);
					if(rowValues.size()<=k)
					{
						List<Integer> temp2 = new ArrayList<Integer>();
						temp2.add(temp1.get(i));
						rowValues.add(temp2);
					}
					else
						rowValues.get(k).add(temp1.get(i));
				}
				
				String locateCol = Table+"."+columnName;
				columnNames.add(locateCol);
				checkColumnFlag=true;
				break;
			}
		}
	}
				
}
