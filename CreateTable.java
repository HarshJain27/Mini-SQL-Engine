package Common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import gudusoft.gsqlparser.nodes.TColumnDefinition;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;

public class CreateTable {

	public void analyzeCreateTableStmt(TCreateTableSqlStatement stmt) {
		// TODO Auto-generated method stub
		String tableName=stmt.getTargetTable().toString();
		if(Input.tablesInfo.get(tableName)!=null){System.out.println("Table already exists.."); return;}
		TColumnDefinition column;
		try 
		{
		File file =new File("Resources/metadata.txt");
		String NF="Resources/"+tableName+".csv";
		File newFile =new File(NF);
		if(!newFile.exists())
			newFile.createNewFile();
		if(!file.exists())
				file.createNewFile();
				
			FileWriter fileWritter = new FileWriter(file,true);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			//bufferWritter.newLine(); 
			bufferWritter.write("<begin_table>".trim());
			bufferWritter.newLine(); bufferWritter.write(tableName);
		
			for(int i=0;i<stmt.getColumnList().size();i++)
		{
            bufferWritter.newLine();
            column = stmt.getColumnList().getColumn(i);
            bufferWritter.write(column.getColumnName().toString().trim());
            
        }
		
		bufferWritter.newLine();
		bufferWritter.write("<end_table>".trim());bufferWritter.newLine(); 
		bufferWritter.close();
		System.out.println("Table created successfully...");
		}	
		 catch (IOException e) {
			e.printStackTrace();
		}

		
	}


}
