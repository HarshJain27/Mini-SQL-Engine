package Common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import gudusoft.gsqlparser.stmt.TDeleteSqlStatement;

public class AnalyseDeleteTable {

	String tableName;
	public void AnalyseDeleteTableStmt(TDeleteSqlStatement stmt) {
		// TODO Auto-generated method stub
	if(stmt.getWhereClause()==null)
	{
		//Delete all rows
		tableName=stmt.getTargetTable().toString();
		String file="Resources/"+tableName+".csv";
		try{
		File f = new File(file);
		if(f.length()==0L) {System.out.println("No rows selected...");return;}
		PrintWriter pw = new PrintWriter(file);
		pw.close();
		if(f.length()==0L)System.out.println("Rows Deleted SuccessFully..");
		}
		catch(IOException e)
		{
			e.getMessage();
		}
	}
	else
	{
		List<String> columnNames = new ArrayList<String>();
		List<List<Integer>>rowValues = new ArrayList<List<Integer>>();
		tableName=stmt.getTargetTable().toString();
		columnNames=Input.tablesInfo.get(tableName);
		rowValues=Input.tuplesInfo.get(tableName);
		
		String whereStatement = stmt.getWhereClause().getCondition().toString();
		
		/*---------------------Checking for AND or OR connected where clause--------------*/
		boolean flagForCondition=false;
		String condition1,condition2;
		
		String connectiveOp=whereParser.checkContainsConnectiveOp(whereStatement);
		if(connectiveOp!=null)
		{
			flagForCondition = true;
			condition1 = whereStatement.substring(0,whereStatement.indexOf(connectiveOp));
			condition2 = whereStatement.substring(whereStatement.indexOf(connectiveOp)+5);	
		}
		else
		{
			condition1=whereStatement;
			condition2="";
		}
		/*---------------------Checking for AND or OR connected where clause--------------*/
		
		/*---------------------Parsing the sub-parts of where clause-----------------------*/
		String leftPart1=null,rightPart1=null,leftPart2=null,rightPart2=null;
		int leftPartColumnNumber1=-2,rightPartColumnNumber1=-2,leftPartColumnNumber2=-2,rightPartColumnNumber2=-2;
		int operationNumber1=-2,operationNumber2=-2;
		
		/*---------------*/
		String cmpOp=whereParser.checkContains(condition1);
		if(cmpOp!=null)
		{
			StringTokenizer st = new StringTokenizer(condition1,cmpOp);
			leftPart1=st.nextToken();
			rightPart1=st.nextToken();
			/*------------======================================---------------*/
			leftPartColumnNumber1 = whereParser.getColumnNumber(columnNames, leftPart1);
			rightPartColumnNumber1 = whereParser.getColumnNumber(columnNames, rightPart1);
			operationNumber1=whereParser.getoperatorNumber(cmpOp);
		}
		/*---------------*/
		
		boolean isRightPartNumber1=false,isRightPartNumber2=false;
		if(leftPartColumnNumber1==-1 || operationNumber1==-1)
		{
			System.out.println("Invalid Column Name or operation...");
			return ;
		}
		if(rightPartColumnNumber1==-1)
		{
			rightPartColumnNumber1=Integer.parseInt(rightPart1);
			isRightPartNumber1 = true;								
		}
		
		List<Integer> result = new ArrayList<Integer>();
		if(flagForCondition)
		{
			/*--------------------------------------------------*/
			String cmpOp1=whereParser.checkContains(condition2);
			if(cmpOp1!=null)
			{
				
				StringTokenizer st = new StringTokenizer(condition2,cmpOp1);
				leftPart2=st.nextToken();
				rightPart2=st.nextToken();
				leftPartColumnNumber2 = whereParser.getColumnNumber(columnNames, leftPart2);
				rightPartColumnNumber2 = whereParser.getColumnNumber(columnNames, rightPart2);
				operationNumber2=whereParser.getoperatorNumber(cmpOp1);
			}
			
			if(leftPartColumnNumber2==-1 || operationNumber2==-1)
			{
				System.out.print("Invalid Column Name or Operation");
				return ;
			}
			if(rightPartColumnNumber2==-1)
			{
				rightPartColumnNumber2=Integer.parseInt(rightPart2);
				isRightPartNumber2 = true;								
			}
						
			for(int i=0;i<rowValues.size();i++)
			{
				List<Integer> tempList = rowValues.get(i);				
				int operand1,operand2,operand3,operand4;
				operand1  = tempList.get(leftPartColumnNumber1);
				if(isRightPartNumber1)
					operand2=rightPartColumnNumber1;
				else
					operand2=tempList.get(rightPartColumnNumber1);
				
				operand3  = tempList.get(leftPartColumnNumber2);
				if(isRightPartNumber2)
					operand4=rightPartColumnNumber2;
				else
					operand4=tempList.get(rightPartColumnNumber2);

				
				boolean tempFlag1=false,tempFlag2=false;
				tempFlag1 = whereParser.check(operand1, operand2, operationNumber1);
				tempFlag2 = whereParser.check(operand3, operand4, operationNumber2);
				if(connectiveOp.toUpperCase().contains("AND")) 
				{
					if(tempFlag1 && tempFlag2) result.add(i);
				}
				else if(connectiveOp.toUpperCase().contains("OR"))
				{
					if(tempFlag1 || tempFlag2) result.add(i);
				}
										
			}
			
			/*--------------------------------------------------*/
		}
		else
		{ 
			for(int i=0;i<rowValues.size();i++)
			{
				List<Integer> tempList = rowValues.get(i);
						
				int operand1,operand2;
				operand1  = tempList.get(leftPartColumnNumber1);
				if(isRightPartNumber1)
					operand2=rightPartColumnNumber1;
				else
					operand2=tempList.get(rightPartColumnNumber1);
				
				boolean tempFlag=false;
				tempFlag = whereParser.check(operand1, operand2, operationNumber1);
												
				if(tempFlag) result.add(i);
										
			}						
		}
		DeleteTableStmt(result);

	}
		
	}
	public void DeleteTableStmt(List<Integer> rowNumbers)
	{
		if(rowNumbers.size()==0){System.out.println("No rows selected..."); return;}
		String OF="Resources/temp.csv";
		
		try{
		BufferedReader fileHandle = new BufferedReader(new FileReader("Resources/"+tableName+".csv"));
		
		File oldFile =new File(OF);
		
		if(!oldFile.exists())
			oldFile.createNewFile();
		
		FileWriter fileWritter = new FileWriter(oldFile,false);
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		
		String nextRow=fileHandle.readLine();
		int i=0,k=0;
		while(nextRow!=null)
		{
			if(k<rowNumbers.size()  && i== rowNumbers.get(k)) 
			{
				k++;
			}
			else
			{
				bufferWritter.write(nextRow);bufferWritter.newLine();
			}
			nextRow=fileHandle.readLine();
			i++;
		}
		bufferWritter.close();
		fileHandle.close();
		
		String NF="Resources/"+tableName+".csv";
		File newFile =new File(NF);
		newFile.delete();
		
		NF="Resources/"+tableName+".csv";
		newFile =new File(NF);
		
		if(oldFile.renameTo(newFile)) {
	         System.out.println(rowNumbers.size()+" "+"Rows Deleted");
	      } else {
	         System.out.println("Error while deleting....");
	      }
		
		}
		catch(IOException e)
		{
			e.getMessage();
		}
		        
	}
}
