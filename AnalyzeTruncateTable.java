package Common;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.stmt.TTruncateStatement;

public class AnalyzeTruncateTable {

	public void AnalyzeTruncateTableStmt(TTruncateStatement stmt) {
		// TODO Auto-generated method stub
		TObjectName name= stmt.getTableName();
		String tableName=name.toString();
		
		Set<String> allColumns=Input.tablesInfo.keySet();
		if(allColumns.contains(tableName))
		{
		String file="Resources/"+tableName+".csv";
		try{
		File f = new File(file);
		PrintWriter pw = new PrintWriter(file);
		pw.close();
		if(f.length()==0L)System.out.println("Truncated SuccessFully..");
		}
		catch(IOException e)
		{
			e.getMessage();
		}
		}
		else System.out.println("Table doesn't exist...");
	}

}
