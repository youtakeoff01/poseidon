package com.hand.bdss.web.common.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.TablesNamesFinder;

/**
 * sql解析工具类
 * 参考:https://github.com/JSQLParser/JSqlParser/blob/master/src/test/java/net/sf/jsqlparser/test/select/SelectASTTest.java
 * http://blog.csdn.net/u014297722/article/details/53256533
 * @author zk
 */
public class JsqlparserUtil {
	
	public static void main(String[] args) throws JSQLParserException{
		String sql = "SELECT * FROM MY_TABLE1, MY_TABLE2, (SELECT * FROM MY_TABLE3) LEFT OUTER JOIN MY_TABLE4 "+
				" WHERE ID = (SELECT MAX(ID) FROM MY_TABLE5) AND ID2 IN (SELECT * FROM MY_TABLE6)" ;
//		test_select_table(sql);
		System.out.println(selectTable(sql));
	}
	
    
    // *********select body items内容  
    public static List<String> selectItems(String sql)  
            throws JSQLParserException {  
        CCJSqlParserManager parserManager = new CCJSqlParserManager();  
        Select select = (Select) parserManager.parse(new StringReader(sql));  
        PlainSelect plain = (PlainSelect) select.getSelectBody();  
        List<SelectItem> selectitems = plain.getSelectItems();  
        List<String> str_items = new ArrayList<String>();  
        if (selectitems != null) {  
            for (int i = 0; i < selectitems.size(); i++) {  
                str_items.add(selectitems.get(i).toString());  
            }  
        }  
        return str_items;  
    }  
  
    // **********select table  
    // **********TablesNamesFinder:Find all used tables within an select  
    public static List<String> selectTable(String sql)  
            throws JSQLParserException {  
        Statement statement = (Statement) CCJSqlParserUtil.parse(sql);  
        Select selectStatement = (Select) statement;  
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();  
        List<String> tableList = tablesNamesFinder  
                .getTableList(selectStatement);  
        return tableList;  
    }  
  
    //******************* select join  
    public static List<String> selectJoin(String sql)  
            throws JSQLParserException {  
        Statement statement = (Statement) CCJSqlParserUtil.parse(sql);  
        Select selectStatement = (Select) statement;  
        PlainSelect plain = (PlainSelect) selectStatement.getSelectBody();  
        List<Join> joinList = plain.getJoins();  
        List<String> tablewithjoin = new ArrayList<String>();  
        if (joinList != null) {  
            for (int i = 0; i < joinList.size(); i++) {  
                tablewithjoin.add(joinList.get(i).toString());  
                //注意 ， leftjoin rightjoin 等等的to string()区别  
            }  
        }  
        return tablewithjoin;  
    }  
      
    // *******select where  
    public static String selectWhere(String sql)  
            throws JSQLParserException {  
        CCJSqlParserManager parserManager = new CCJSqlParserManager();  
        Select select = (Select) parserManager.parse(new StringReader(sql));  
        PlainSelect plain = (PlainSelect) select.getSelectBody();  
        Expression where_expression = plain.getWhere();  
        String str = where_expression.toString();  
        return str;  
    }  
  
    // ******select group by  
    public static List<String> selectGroupby(String sql)  
            throws JSQLParserException {  
        CCJSqlParserManager parserManager = new CCJSqlParserManager();  
        Select select = (Select) parserManager.parse(new StringReader(sql));  
        PlainSelect plain = (PlainSelect) select.getSelectBody();  
        List<Expression> GroupByColumnReferences = plain  
                .getGroupByColumnReferences();  
        List<String> str_groupby = new ArrayList<String>();  
        if (GroupByColumnReferences != null) {  
            for (int i = 0; i < GroupByColumnReferences.size(); i++) {  
                str_groupby.add(GroupByColumnReferences.get(i).toString());  
            }  
        }  
        return str_groupby;  
    }  
  
    // **************select order by  
    public static List<String> selectOrderby(String sql)  
            throws JSQLParserException {  
        CCJSqlParserManager parserManager = new CCJSqlParserManager();  
        Select select = (Select) parserManager.parse(new StringReader(sql));  
        PlainSelect plain = (PlainSelect) select.getSelectBody();  
        List<OrderByElement> OrderByElements = plain.getOrderByElements();  
        List<String> str_orderby = new ArrayList<String>();  
        if (OrderByElements != null) {  
            for (int i = 0; i < OrderByElements.size(); i++) {  
                str_orderby.add(OrderByElements.get(i).toString());  
            }  
        }  
        return str_orderby;  
    }  
  
  
  
    // ****insert table  
    public static String insertTable(String sql)  
            throws JSQLParserException {  
        Statement statement = CCJSqlParserUtil.parse(sql);  
        Insert insertStatement = (Insert) statement;  
        String string_tablename = insertStatement.getTable().getName();  
        return string_tablename;  
    }  
  
    // ********* insert table column  
    public static List<String> insertColumn(String sql)  
            throws JSQLParserException {  
        Statement statement = CCJSqlParserUtil.parse(sql);  
        Insert insertStatement = (Insert) statement;  
        List<Column> table_column = insertStatement.getColumns();  
        List<String> str_column = new ArrayList<String>();  
        for (int i = 0; i < table_column.size(); i++) {  
            str_column.add(table_column.get(i).toString());  
        }  
        return str_column;  
    }  
  
    // ********* Insert values ExpressionList  
    public static List<String> insertValues(String sql)  
            throws JSQLParserException {  
        Statement statement = CCJSqlParserUtil.parse(sql);  
        Insert insertStatement = (Insert) statement;  
        List<Expression> insert_values_expression = ((ExpressionList) insertStatement  
                .getItemsList()).getExpressions();  
        List<String> str_values = new ArrayList<String>();  
        for (int i = 0; i < insert_values_expression.size(); i++) {  
            str_values.add(insert_values_expression.get(i).toString());  
        }  
        return str_values;  
    }  
  
    // *********update table name  
    public static List<String> updateTable(String sql)  
            throws JSQLParserException {  
        Statement statement = CCJSqlParserUtil.parse(sql);  
        Update updateStatement = (Update) statement;  
        List<Table> update_table = updateStatement.getTables();  
        List<String> str_table = new ArrayList<String>();  
        if (update_table != null) {  
            for (int i = 0; i < update_table.size(); i++) {  
                str_table.add(update_table.get(i).toString());  
            }  
        }  
        return str_table;  
  
    }  
  
    // *********update column  
    public static List<String> updateColumn(String sql)  
            throws JSQLParserException {  
        Statement statement = CCJSqlParserUtil.parse(sql);  
        Update updateStatement = (Update) statement;  
        List<Column> update_column = updateStatement.getColumns();  
        List<String> str_column = new ArrayList<String>();  
        if (update_column != null) {  
            for (int i = 0; i < update_column.size(); i++) {  
                str_column.add(update_column.get(i).toString());  
            }  
        }  
        return str_column;  
  
    }  
  
    // *********update values  
    public static List<String> updateValues(String sql)  
            throws JSQLParserException {  
        Statement statement = CCJSqlParserUtil.parse(sql);  
        Update updateStatement = (Update) statement;  
        List<Expression> update_values = updateStatement.getExpressions();  
        List<String> str_values = new ArrayList<String>();  
        if (update_values != null) {  
            for (int i = 0; i < update_values.size(); i++) {  
                str_values.add(update_values.get(i).toString());  
            }  
        }  
        return str_values;  
  
    }  
  
    // *******update where  
    public static String updateWhere(String sql)  
            throws JSQLParserException {  
        Statement statement = CCJSqlParserUtil.parse(sql);  
        Update updateStatement = (Update) statement;  
        Expression where_expression = updateStatement.getWhere();  
        String str = where_expression.toString();  
        return str;  
    }  
  
}
