package Common;
import java.io.*;
import java.util.*;
import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TDeleteSqlStatement;
import gudusoft.gsqlparser.stmt.TDropTableSqlStatement;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.TTruncateStatement;

public class Input {
	public static Map <String, List<String>> tablesInfo = new HashMap<String, List<String>>();
	public static Map <String, List<List<Integer>>> tuplesInfo = new HashMap<String,List<List<Integer>>>();
	static String query=null;
	
	public static void main(String[] args) throws IOException
	{
		while(true)
		{
			System.out.print("sql>");
			Scanner sc=new Scanner(System.in);
		BufferedReader metadataHandle = new BufferedReader(new FileReader("Resources/metadata.txt"));
		String nextRow=metadataHandle.readLine();
		while(nextRow!=null && nextRow.equals("<begin_table>"))
		{
			String tableName = metadataHandle.readLine();
			List <String> list = new ArrayList<String>();
			
			nextRow=metadataHandle.readLine();
			while( !nextRow.equals("<end_table>"))
			{
				list.add(nextRow);
				nextRow=metadataHandle.readLine();	
			}
			tablesInfo.put(tableName, list);
			String csvFileName = "Resources/"+tableName+".csv";
			List <List<Integer>> list1 = new ArrayList<List<Integer>>();
			BufferedReader csvFileHandle = new BufferedReader(new FileReader(csvFileName));
			nextRow=csvFileHandle.readLine();

			while(nextRow!=null)
			{
				List <Integer> list2 = new ArrayList<Integer>();
				
				StringTokenizer st = new StringTokenizer(nextRow,",");
				while(st.hasMoreElements())
				{
					String token = st.nextToken();
					if(token.startsWith("\""))
					{
						token = token.substring(1);
						token=token.substring(0, token.length()-1);
						int num = Integer.parseInt(token);
						list2.add(num);
					}
					else
					{
						int num = Integer.parseInt(token);
						list2.add(num);
					}
				}
				list1.add(list2);
				nextRow = csvFileHandle.readLine();
			}
			tuplesInfo.put(tableName, list1);	
			csvFileHandle.close();
			nextRow=metadataHandle.readLine();
		}
	
		metadataHandle.close();
				
		TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
		sqlparser.sqltext=sc.nextLine();
		query=sqlparser.sqltext;
		int val = sqlparser.parse();
		if(val == 0)
		{
			for(int i=0;i<sqlparser.sqlstatements.size();i++)
			{
				analyzeStmt(sqlparser.sqlstatements.get(i));
			}
		}
		else
		{
			System.out.println(sqlparser.getErrormessage());
		}
		System.out.println();
		}
		
	}

protected static void analyzeStmt(TCustomSqlStatement stmt) throws IOException
{
	switch(stmt.sqlstatementtype)
	{
		case sstselect:
			AnalyzeSelect as = new AnalyzeSelect();
			as.analyzeSelectStmt((TSelectSqlStatement)stmt);
			break;
		case sstcreatetable:
			CreateTable ct=new CreateTable();
			ct.analyzeCreateTableStmt((TCreateTableSqlStatement)stmt);
			break;
		case sstinsert:
			AnalyzeInsert ai=new AnalyzeInsert();
            ai.analyzeInsertStmt((TInsertSqlStatement)stmt);
            break;	
		case sstTruncate:
			AnalyzeTruncateTable tc=new AnalyzeTruncateTable();
			tc.AnalyzeTruncateTableStmt((TTruncateStatement)stmt);
			break;
		case sstdelete:
			AnalyseDeleteTable delT=new AnalyseDeleteTable();
			delT.AnalyseDeleteTableStmt((TDeleteSqlStatement)stmt);
			break;
		case sstdroptable:
			AnalyzeDroptable dt=new AnalyzeDroptable();
			dt.AnalyzeDroptableStmt((TDropTableSqlStatement)stmt);
			break;	
		default:
			System.out.println(stmt.sqlstatementtype.toString());
	}

}
}