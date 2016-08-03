package Common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TDropTableSqlStatement;

public class AnalyzeDroptable {
	TObjectName name;
	String tableName;
	public void AnalyzeDroptableStmt(TDropTableSqlStatement stmt) throws IOException {
		// TODO Auto-generated method stub
		
		name= stmt.getTableName();
		tableName=name.toString();
		Set<String> allColumns=Input.tablesInfo.keySet();
		if(allColumns.contains(tableName)) {
		
		String fileName="Resources/"+tableName+".csv";
		try{
		File f = new File(fileName);
		if(f.length() == 0L) 
			{
			boolean success = (new File(fileName)).delete();
			if(success) {deleteTableEntry(stmt); }
			else System.out.println("Error while deleting file..");
			
			}
		else System.out.println("Table Not Empty...");
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		}
		else 
		{
			System.out.println("Table Doesn't Exists..");
		}
	}
	
	void deleteTableEntry(TDropTableSqlStatement stmt) throws IOException
	{
		List<Integer> index = new ArrayList<Integer>();
		name= stmt.getTableName();
		tableName=name.toString();
		BufferedReader metadataHandle;
		try {
			metadataHandle = new BufferedReader(new FileReader("Resources/metadata.txt"));
			String nextRow=metadataHandle.readLine();
			int i=0,min=-1,max=-1,flag=0;
			while(nextRow!=null)
			{
				if(nextRow.equals(tableName)) {min=i-1;flag=1;}
				if(flag==1 && nextRow.equals("<end_table>")){max=i;break;}
				nextRow=metadataHandle.readLine();
				i++;
			}
		
			index.add(min);
			index.add(max);
			deleteTableFromMeta(index);
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	private void deleteTableFromMeta(List<Integer> index) {
		
		String OF="Resources/temp.csv";
		File oldFile =new File(OF);
		
		if(!oldFile.exists())
			try {
				oldFile.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		
		BufferedWriter bufferWritter=null;
		try {
			bufferWritter = new BufferedWriter(new FileWriter(oldFile,false));
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}
		
		try {
			BufferedReader fileHandle = new BufferedReader(new FileReader("Resources/metadata.txt"));
			String nextRow=fileHandle.readLine();
			int i=0;
			while(nextRow!=null)
			{
				if(i>=index.get(0) && i<=index.get(1)) 
				{
					//do nothing....
				}
				else
				{
					bufferWritter.write(nextRow.trim()) ;bufferWritter.newLine();
				}
				
				nextRow=fileHandle.readLine();
				i++;
			}
			bufferWritter.close();
			fileHandle.close();
			
			String NF="Resources/metadata.txt";
			File newFile =new File(NF);
			newFile.delete();
			
			NF="Resources/metadata.txt";
			newFile =new File(NF);
			
			if(oldFile.renameTo(newFile)) {
		         System.out.println("Table Deleted");
		      } else {
		         System.out.println("Error while deleting the table....");
		      }
		}
		catch(Exception e)
		{
			e.getMessage();
		}
	}
}
