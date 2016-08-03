package Common;

import java.util.List;

public class AggregateEvaluator {

	public static Integer findAggregateFuncValue(String aggregateFuncFlag, List<Integer> temp1) {
		// TODO Auto-generated method stub
		int result;
		switch(aggregateFuncFlag)
		{
		case "MAX": result=findMax(temp1);break;
		case "MIN": result=findMin(temp1);break;
		case "COUNT": result=findCount(temp1);break;
		case "AVG": result=findAvg(temp1);break;
		case "SUM": result=findSum(temp1);break;
		default:result=-1;
		}
		return result;
	}

	private static int findSum(List<Integer> temp1) {
		// TODO Auto-generated method stub
		int i,sum=0;
		for(i=0;i<temp1.size();i++)
		{
			sum=sum+temp1.get(i);
		}
		return sum;
	}

	private static int findAvg(List<Integer> temp1) {
		// TODO Auto-generated method stub
		int i,sum=0;
		for(i=0;i<temp1.size();i++)
		{
			sum=sum+temp1.get(i);
		}
		return sum/temp1.size();
	}

	private static int findCount(List<Integer> temp1) {
		// TODO Auto-generated method stub
		return temp1.size();
	}

	private static int findMin(List<Integer> temp1) {
		// TODO Auto-generated method stub
		int i,min=temp1.get(0);
		for(i=0;i<temp1.size();i++)
		{
			if(temp1.get(i)<min) min=temp1.get(i);
		}
		return min;
	}

	private static int findMax(List<Integer> temp1) {
		// TODO Auto-generated method stub
		int i,max=temp1.get(0);
		for(i=0;i<temp1.size();i++)
		{
			if(temp1.get(i)>max) max=temp1.get(i);
		}
		return max;
	}

}
