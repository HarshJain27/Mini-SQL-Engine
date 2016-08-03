package Common;

import java.util.List;

import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

public class whereParser {

	public static int getColumnNumber(List<String> columnNames, String leftPart) {
		// TODO Auto-generated method stub
		for(int i=0;i<columnNames.size();i++)
		{
			if(leftPart.equals(columnNames.get(i)))
				return i;
		}
		
		return -1;
	}

	public static int getoperatorNumber(String string) {
		// TODO Auto-generated method stub		
		int flag=-1;
		switch(string)
		{
		case "=": flag=0; break;
		case "<": flag=1;break;
		case ">":  flag=2;break;
		case ">=":  flag=3;break;
		case "<=": flag=4;break;
		}
		return flag;
	}

	public static boolean check(int operand1, int operand2, int operationNumber1) {
		// TODO Auto-generated method stub
		boolean flag=false;
		switch(operationNumber1)
		{
		case 0: if(operand1==operand2) flag=true; break;
		case 1: if(operand1<operand2) flag=true;break;
		case 2: if(operand1>operand2) flag=true;break;
		case 3: if(operand1>=operand2) flag=true;break;
		case 4: if(operand1<=operand2) flag=true;break;
		}
		
		return flag;

	}

	public static String checkContains(String condition1) {
		// TODO Auto-generated method stub
		String flag=null;
		if(condition1.contains(">=")) flag=">=";
		else if(condition1.contains(">")) flag=">";
		else if(condition1.contains("<=")) flag="<=";
		else if(condition1.contains("=")) flag="=";
		else if(condition1.contains("<")) flag="<";
		return flag;
	}

	public static String checkContainsConnectiveOp(String whereStatement) {
		// TODO Auto-generated method stub
		String flag=null;
		if(whereStatement.contains(" AND ")) flag=" AND ";
		else if(whereStatement.contains(" and ")) flag=" and ";
		else if(whereStatement.contains(" And ")) flag=" And ";
		else if(whereStatement.contains(" OR ")) flag=" OR ";
		else if(whereStatement.contains(" or ")) flag=" or ";
		else if(whereStatement.contains(" Or ")) flag=" Or ";
		return flag;
	
	}

	public static String containsAggregateFunc(String columnName) {
		// TODO Auto-generated method stub
		String flag=null;
		if(columnName.contains("max(")|| columnName.contains("MAX(") || columnName.contains("Max(")) flag="MAX";
		else if(columnName.contains("min(")|| columnName.contains("MIN(") || columnName.contains("Min(")) flag="MIN";
		else if(columnName.contains("avg(")|| columnName.contains("AVG(") || columnName.contains("Avg(")) flag="AVG";
		else if(columnName.contains("count(")|| columnName.contains("COUNT(") || columnName.contains("Count(")) flag="COUNT";
		else if(columnName.contains("sum(")|| columnName.contains("SUM(") || columnName.contains("Sum(")) flag="SUM";
		return flag;
	}

	public static String containsDistinct(TSelectSqlStatement stmt) {
		// TODO Auto-generated method stub
		
		String column=stmt.getResultColumnList().toString();
		if(stmt.toString().contains("DISTINCT") || stmt.toString().contains("distinct") || stmt.toString().contains("Distinct"))
		{
		if(column.contains("("))
		{
			column=column.substring(column.indexOf("(")+1);
			column=column.substring(0,column.indexOf(")"));
		}
		return column;
		}
		else return null;
		
	}

}
