package Common;

import java.util.List;

public class PrintTuples {

	public static void printDataFromSingletable(List<String> columnNames, List<List<Integer>> rowValues,
			String tableName) {
		// TODO Auto-generated method stub
		/*---------------------------------------------------------------*/
		for(int i=0;i<columnNames.size();i++)
		{
			System.out.print(tableName+"."+columnNames.get(i));
			if(i!=columnNames.size()-1) System.out.print(",");
			else System.out.print("\n");
		}
		
		for(int i=0;i<rowValues.size();i++)
		{
			List<Integer> l = rowValues.get(i);
			for(int j=0;j<l.size();j++)
			{
				System.out.print(l.get(j));
				if(j!=columnNames.size()-1) System.out.print(",");
			}
			if(i!=rowValues.size()-1) System.out.print("\n");
		}
		/*---------------------------------------------------------------*/
	}

	public static void printDataFromTwoTablesWithoutWhere(List<String> columnNames, List<List<Integer>> result) {
		// TODO Auto-generated method stub
		for(int i=0;i<columnNames.size();i++)
		{
			System.out.print(columnNames.get(i));
			if(i!=columnNames.size()-1) System.out.print(",");
			else System.out.print("\n");
		}
		
		for(int i=0;i<result.size();i++)
		{
			List<Integer> l = result.get(i);
			for(int j=0;j<l.size();j++)
			{
				System.out.print(l.get(j));
				if(j!=result.size()-1) System.out.print(",");		
			}
			if(i!=result.size()-1) System.out.print("\n");
		}

		
	}
	

}
