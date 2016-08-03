# Mini-SQL-Engine
Mini SQL Engine supports a subset of SQL command.

The mini­sql engine supports a subset of SQL Queries using ​command line interface.
Dataset​:
1.  csv files for tables. 
a.  If a file is : ​File1.csv, the table name would be File1. 
b. There will be no tab­ separation or space ­separation,but values may be in double quotes or without quotes. 
2.  All the elements in files would be ​only INTEGERS​
3.  A file named: ​metadata.txt​(note the extension) would be given which will have the following structure for each table: 
<begin_table> 
<table_name> 
<attribute1> 
.... 
 
<attributeN> 
<end_table> 

Type of Queries Supported by engine:

1.Select all records​: 
Select * from table_name;  
2.Aggregate functions:​
Simple aggregate functions on a single column. Sum, average, max and min. 
3.Project Columns​ (could be any number of columns) from one or more tables : 
Select col1, col2 from table_name; 
4.Project with distinct from one table: ​
select distinct(col1) from table_name; 
5.Select with where from one or more tables(In the where queries, there would be a maximum of one AND/OR operator with 
no NOT operators):​
select col1,col2 from table1,table2 where col1 = 10 AND col2 = 20;  
6.Create command:​ Required to modify the ​metadata.txt ​and create a new csv file in the working directory. 
CREATE TABLE table_name(column1 datatype, column2 datatype, ..... columnN datatype) 
7. ​Insert Command: ​
Insert into <table­name> values(v​1,..vN​); 
8.Delete Command:​Delete a single record from a given table. Only one where condition would be given.Multiple conditions in where clause is not handled.  
Delete from <table­name> where <attribute> = <some­value>
9. ​Truncate Command:​
Delete all records from a table. 
TRUNCATE TABLE  table_name; 
10. ​Drop Table:​Delete an empty table. 
DROP TABLE table_name;
