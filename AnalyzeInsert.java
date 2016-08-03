package Common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import gudusoft.gsqlparser.nodes.TInsertIntoValue;
import gudusoft.gsqlparser.nodes.TMultiTargetList;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TObjectNameList;
import gudusoft.gsqlparser.nodes.TPTNodeList;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;

public class AnalyzeInsert {

	public void analyzeInsertStmt(TInsertSqlStatement stmt) {
	
		String values=stmt.getValues().toString();
		int count=0;
		values = values.substring(1);
		values=values.substring(0, values.length()-1);
		
		StringTokenizer st = new StringTokenizer(values,",");		
		
		while(st.hasMoreElements())
		{
				count++;
				try{
					String token = st.nextToken();
					Integer.parseInt(token);
				}
				catch(Exception e)
				{					
					System.out.println("Invalid row value...Datatype Mismatch");
				}
		}
		
		/*==================================*/
		
		String tableName=stmt.getTargetTable().toString();
		
		if(count!=Input.tablesInfo.get(tableName).size()) {System.out.println("Number of arguments mismatch...");return;}
		
		String fileName="Resources/"+tableName+".csv";
		File fileHandle =new File(fileName);
		if(!fileHandle.exists()) {System.out.println("file doesn't exist.....");}
		
		FileWriter fileWritter;
		try {
			fileWritter = new FileWriter(fileHandle,true);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.write(values);bufferWritter.newLine();
			System.out.println("Row added Successfully...");
			bufferWritter.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*==================================*/
	}

}
