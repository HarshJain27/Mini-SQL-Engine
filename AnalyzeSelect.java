package Common;



import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.nodes.TJoin;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

public class AnalyzeSelect {

	public void analyzeSelectStmt(TSelectSqlStatement stmt) {
		// TODO Auto-generated method stub
		boolean selectAllFlag=false;
		if(stmt.joins.size()==1) //"Queries of type "select f from t1 join t2 on t1.f1 = t2.f1" or "select f from t1" or "select f from t1 join t2 on t1.f1 = t2.f1 join t3 on t1.f1 = t3.f1"
		{	
			TJoin join = stmt.joins.getJoin(0); //Gives table name from "from" clause of select
			if(join.getKind()==TBaseType.join_source_fake) //represents a fake join of type "select f from t1" 
			{   
				String tableName = join.getTable().toString();
				List<String> check=Input.tablesInfo.get(tableName);
				if(check==null) { System.out.println("Table doesn't exist...."); return;}
			
				if(stmt.getResultColumnList().size()==1) /*getResultColumnList gives all columns specified in select clause*/// 1 size can be due to * or single column specified
				{
					TResultColumn resultColumn =stmt.getResultColumnList().getResultColumn(0);
					String tempStr = resultColumn.getExpr().toString();
					if(tempStr.toString().equals("*")) //if "*" has been selected
					{
						selectAllFlag=true;
						
						/*------------------------------------------------------------------------*/
						List<String> columnNames = new ArrayList<String>();
						List<List<Integer>>rowValues = new ArrayList<List<Integer>>();
						columnNames=check;
						rowValues=Input.tuplesInfo.get(tableName);
						
						if(stmt.getWhereClause() == null)
						{
							PrintTuples.printDataFromSingletable(columnNames, rowValues, tableName);
						}
						else
						{
							String whereStatement = stmt.getWhereClause().getCondition().toString();
							/*---------------------Checking for AND or OR connected where clause--------------*/
							boolean flagForCondition=false;
							String condition1=null,condition2=null;
							
							String connectiveOp=whereParser.checkContainsConnectiveOp(whereStatement);
							if(connectiveOp!=null)
							{
								flagForCondition = true;
								condition1 = whereStatement.substring(0,whereStatement.indexOf(connectiveOp));
								condition2 = whereStatement.substring(whereStatement.indexOf(connectiveOp)+connectiveOp.length());
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
							String cmpOp1=whereParser.checkContains(condition1);
							
							if(cmpOp1!=null)
							{
								StringTokenizer st = new StringTokenizer(condition1,cmpOp1);
								leftPart1=st.nextToken();
								rightPart1=st.nextToken();
								leftPartColumnNumber1 = whereParser.getColumnNumber(columnNames, leftPart1);
								rightPartColumnNumber1 = whereParser.getColumnNumber(columnNames, rightPart1);
								operationNumber1=whereParser.getoperatorNumber(cmpOp1);
							}
							/*---------------*/
							
							boolean isRightPartNumber1=false,isRightPartNumber2=false;
							if(leftPartColumnNumber1==-1 || operationNumber1==-1)
							{
								System.out.print("Invalid Column Name or Operation");
								return ;
							}
							if(rightPartColumnNumber1==-1)
							{
								rightPartColumnNumber1=Integer.parseInt(rightPart1);
								isRightPartNumber1 = true;								
							}
							
							List<List<Integer>> result = new ArrayList<List<Integer>>();
							
							if(flagForCondition)
							{
								/*--------------------------------------------------*/
								String cmpOp2=whereParser.checkContains(condition2);
								if(cmpOp2!=null)
								{
									StringTokenizer st = new StringTokenizer(condition2,cmpOp2);
									leftPart2=st.nextToken();
									rightPart2=st.nextToken();
									leftPartColumnNumber2 = whereParser.getColumnNumber(columnNames, leftPart2);
									rightPartColumnNumber2 = whereParser.getColumnNumber(columnNames, rightPart2);
									operationNumber2=whereParser.getoperatorNumber(cmpOp2);
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
										if(tempFlag1 && tempFlag2) result.add(tempList);
									}
									else if(connectiveOp.toUpperCase().contains("OR"))
									{
										if(tempFlag1 || tempFlag2) result.add(tempList);
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
																	
									if(tempFlag) result.add(tempList);
															
								}						
							}
							PrintTuples.printDataFromSingletable(columnNames, result, tableName);
							/*---------------------Parsing the sub-parts of where clause-----------------------*/
						}
																																																																																																																																																																					/*------------------------------------------------------------------------*/
					}
				}
				
				String isDistinct=null;
				boolean isColumnPresent=true;
				String AggregateFuncFlag=null;
				List<Integer> columnOutputOrder = new ArrayList<Integer>();
				
				if(!selectAllFlag)
				{
					//if not all columns(i.e. *) have to be selected
					
					for(int j=0; j < stmt.getResultColumnList().size();j++)
					{
						String isDistinctLocal=null;boolean isColumnPresentLocal=false;
						
						TResultColumn resultColumn =stmt.getResultColumnList().getResultColumn(j);
						String columnName = resultColumn.getExpr().toString();
						
						AggregateFuncFlag=whereParser.containsAggregateFunc(columnName);
						//isDistinctLocal=whereParser.containsDistinct(columnName);
						isDistinctLocal=whereParser.containsDistinct(stmt);
						if(AggregateFuncFlag!=null)
						{
							columnName = columnName.substring(columnName.indexOf("(")+1);
							columnName = columnName.substring(0,columnName.length()-1);
						}
						
						for(int k=0;k<check.size();k++)
						{
							if(check.get(k).equals(columnName) || check.get(k).equals(isDistinctLocal))
							{
								isColumnPresentLocal=true;
								columnOutputOrder.add(k);
								break;
							}								
						}
						
						if(!isColumnPresentLocal) {isColumnPresent=false;break;}
						if(isDistinctLocal!=null) isDistinct=isDistinctLocal;
					}
					
					if(!isColumnPresent) System.out.println("Invalid Column Name");
					else if(AggregateFuncFlag!=null)
					{
						List<String> resultColumns = new ArrayList<String>();
						List<Integer> resultRow = new ArrayList<Integer>();
						
						for(int j=0; j < stmt.getResultColumnList().size();j++)
						{
							TResultColumn resultColumn =stmt.getResultColumnList().getResultColumn(j);
							String columnName = resultColumn.getExpr().toString();
							resultColumns.add(columnName);
							
							columnName = columnName.substring(columnName.indexOf("(")+1);
							columnName = columnName.substring(0,columnName.length()-1);
							
							List<String> allColumns = Input.tablesInfo.get(tableName);
							List<List<Integer>> allRows = Input.tuplesInfo.get(tableName);
							
							int locateColumn=-1;
							for(int i=0;i<allColumns.size();i++)
							{
								if(allColumns.get(i).equals(columnName)) {locateColumn = i;break;}
							}
							
							List<Integer> temp1 = new ArrayList<Integer>();
							for(int i=0;i<allRows.size();i++)
							{
								List<Integer> localList = allRows.get(i);
								temp1.add(localList.get(locateColumn));
							}
							resultRow.add(AggregateEvaluator.findAggregateFuncValue(AggregateFuncFlag,temp1));
						}	
							/*-------------------Printing Aggr Func value----------------*/
							for(int i=0;i<resultColumns.size();i++)
							{
								System.out.print(resultColumns.get(i));
								if(i!=resultColumns.size()-1)
									System.out.print(",");
								else
									System.out.print("\n");
							}
							for(int i=0;i<resultRow.size();i++)
							{
								System.out.print(resultRow.get(i));
								if(i!=resultRow.size()-1)
									System.out.print(",");
									
							}
							/*-------------------Printing Aggr Func value----------------*/
							
					}
					else if(isDistinct!=null) 
					{
						
						List<String> allColumns = Input.tablesInfo.get(tableName);
						List<List<Integer>> allRows = Input.tuplesInfo.get(tableName);
						int locateColumn=-1;
						for(int i=0;i<allColumns.size();i++)
						{	
							if(allColumns.get(i).equals(isDistinct)) {locateColumn = i;break;}
						}
						
						List<Integer> temp1 = new ArrayList<Integer>();
						Set<Integer> temp2=new HashSet<Integer>();
						for(int i=0;i<allRows.size();i++)
						{
							List<Integer> localList = allRows.get(i);
							if(temp2.contains(localList.get(locateColumn))){}
							else 
							{
							temp2.add(localList.get(locateColumn));
							temp1.add(localList.get(locateColumn));
							}
						}
						
						System.out.println(tableName+"."+isDistinct);
						for(int i=0;i<temp1.size();i++)
						{
							System.out.println(temp1.get(i));								
						}
					}
					else
					{
						//System.out.println("Projection w/o distinct or aggregate funcs....");
						List<String> columnNames = new ArrayList<String>();
						for(int j=0; j < stmt.getResultColumnList().size();j++)
						{
							TResultColumn resultColumn =stmt.getResultColumnList().getResultColumn(j);
							String columnName = resultColumn.getExpr().toString();
							columnNames.add(columnName);
						}
						List<List<Integer>> rowValues = Input.tuplesInfo.get(tableName);
						
						if(stmt.getWhereClause() != null)
						{
							List<String> allColumns = Input.tablesInfo.get(tableName);
							/*------------------------------------------------------------------------------------------------------------------*/
							String whereStatement = stmt.getWhereClause().getCondition().toString();
							
							/*---------------------Checking for AND or OR connected where clause--------------*/
							boolean flagForCondition=false;
							String condition1,condition2;
							
							String connectiveOp=whereParser.checkContainsConnectiveOp(whereStatement);
							if(connectiveOp!=null)
							{
								flagForCondition = true;
								condition1 = whereStatement.substring(0,whereStatement.indexOf(connectiveOp));
								condition2 = whereStatement.substring(whereStatement.indexOf(connectiveOp)+connectiveOp.length());	
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
								leftPartColumnNumber1 = whereParser.getColumnNumber(allColumns, leftPart1);

								rightPartColumnNumber1 = whereParser.getColumnNumber(allColumns, rightPart1);
								operationNumber1=whereParser.getoperatorNumber(cmpOp);
							}
							/*---------------*/
							
							boolean isRightPartNumber1=false,isRightPartNumber2=false;
							
							if(leftPartColumnNumber1==-1 || operationNumber1==-1)
							{
								System.out.print("Invalid Column Name or Operation..");
								return ;
							}
							if(rightPartColumnNumber1==-1)
							{
								rightPartColumnNumber1=Integer.parseInt(rightPart1);
								isRightPartNumber1 = true;								
							}
							
							List<List<Integer>> result = new ArrayList<List<Integer>>();
							
							if(flagForCondition)
							{
								
								/*--------------------------------------------------*/
								String cmpOp1=whereParser.checkContains(condition2);
								
								if(cmpOp1!=null)
								{
									
									StringTokenizer st = new StringTokenizer(condition2,cmpOp1);
									leftPart2=st.nextToken();
									rightPart2=st.nextToken();
									System.out.println(allColumns);
									leftPartColumnNumber2 = whereParser.getColumnNumber(allColumns, leftPart2);
									rightPartColumnNumber2 = whereParser.getColumnNumber(allColumns, rightPart2);
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
						
									/*----------ADDED------------*/
									List<Integer>finalResult = new ArrayList<Integer>();
									for(int j=0;j<columnOutputOrder.size();j++)
									{
										int locate = columnOutputOrder.get(j);
										finalResult.add(tempList.get(locate));
									}
									/*----------ADDED------------*/
									
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
										if(tempFlag1 && tempFlag2) result.add(finalResult);
									}
									else if(connectiveOp.toUpperCase().contains("OR"))
									{
										if(tempFlag1 || tempFlag2) result.add(finalResult);
									}
								}
								
								/*--------------------------------------------------*/
							}
							else
							{  
								for(int i=0;i<rowValues.size();i++)
								{
									List<Integer> tempList = rowValues.get(i);
							
									/*----------ADDED------------*/
									List<Integer>finalResult = new ArrayList<Integer>();
									for(int j=0;j<columnOutputOrder.size();j++)
									{
										int locate = columnOutputOrder.get(j);
										finalResult.add(tempList.get(locate));
									}
									/*----------ADDED------------*/
									
									int operand1,operand2;
									operand1  = tempList.get(leftPartColumnNumber1);
									if(isRightPartNumber1)
										operand2=rightPartColumnNumber1;
									else
										operand2=tempList.get(rightPartColumnNumber1);
									
									
									boolean tempFlag=false;
									tempFlag = whereParser.check(operand1, operand2, operationNumber1);
																	
									if(tempFlag) result.add(finalResult);
															
								}						
							}
								PrintTuples.printDataFromSingletable(columnNames, result, tableName);
							/*------------------------------------------------------------------------------------------------------------------*/
						}
						else
						{
							// w/o where
							for(int i=0;i<columnOutputOrder.size();i++)
							{
								System.out.print(tableName+"."+columnNames.get(i));
								if(i!=columnOutputOrder.size()-1)
									System.out.print(",");
								else
									System.out.print("\n");
							}
							
							for(int i=0;i<rowValues.size();i++)
							{	
								List<Integer> cur = rowValues.get(i);
								for(int j=0;j<columnOutputOrder.size();j++)
								{
									int val = columnOutputOrder.get(j);
									System.out.print(cur.get(val));
									if(j!=columnOutputOrder.size()-1)
										System.out.print(",");
								}
								if(i!=rowValues.size()-1)
									System.out.print("\n");
								
							}
						}

					}
				}
				
			}
		}
		else	//System.out.println("Multiple Table Queries like select f from t1,t2"); 
		{
			
			if(stmt.getWhereClause()!=null) //With where clause
			{
				TwoTablesWithWhereClause.AnalyseSelect(stmt);
			}
			else //Without where clause
			{
				TwoTablesWithoutWhere.AnalyseSelect(stmt);
			}
		}
		
	}

}
