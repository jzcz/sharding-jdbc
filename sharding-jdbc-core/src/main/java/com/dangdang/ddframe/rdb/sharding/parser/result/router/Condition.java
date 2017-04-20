/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.dangdang.ddframe.rdb.sharding.parser.result.router;

import com.dangdang.ddframe.rdb.sharding.parser.sql.expr.AbstractSQLTextLiteralExpr;
import com.dangdang.ddframe.rdb.sharding.parser.sql.expr.SQLExpr;
import com.dangdang.ddframe.rdb.sharding.parser.sql.expr.SQLNumberExpr;
import com.dangdang.ddframe.rdb.sharding.parser.sql.expr.SQLPlaceholderExpr;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * 条件对象.
 * 
 * @author gaohongtao
 */
@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public final class Condition {
    
    private final Column column;
    
    private final BinaryOperator operator;
    
    private final List<Comparable<?>> values = new ArrayList<>();
    
    private final List<Integer> valueIndices = new ArrayList<>();
    
    public Condition(final Column column, final SQLExpr sqlExpr) {
        this(column, Condition.BinaryOperator.EQUAL);
        initSQLExpr(sqlExpr);
    }
    
    public Condition(final Column column, final SQLExpr beginSqlExpr, final SQLExpr endSqlExpr) {
        this(column, BinaryOperator.BETWEEN);
        initSQLExpr(beginSqlExpr);
        initSQLExpr(endSqlExpr);
    }
    
    public Condition(final Column column, final List<SQLExpr> sqlExprs) {
        this(column, Condition.BinaryOperator.IN);
        for (SQLExpr each : sqlExprs) {
            initSQLExpr(each);
        }
    }
    
    private void initSQLExpr(final SQLExpr sqlExpr) {
        if (sqlExpr instanceof SQLPlaceholderExpr) {
            values.add((Comparable) ((SQLPlaceholderExpr) sqlExpr).getValue());
            valueIndices.add(((SQLPlaceholderExpr) sqlExpr).getIndex());
        } else if (sqlExpr instanceof AbstractSQLTextLiteralExpr) {
            values.add(((AbstractSQLTextLiteralExpr) sqlExpr).getText());
        } else if (sqlExpr instanceof SQLNumberExpr) {
            values.add((Comparable) ((SQLNumberExpr) sqlExpr).getNumber());
        }
    }
    
    /**
     * 列对象.
     * 
     * @author gaohongtao
     * @author zhangliang
     */
    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode(exclude = "autoIncrement")
    @ToString(exclude = "autoIncrement")
    public static final class Column {
        
        private final String columnName;
        
        private final String tableName;
    
        private final boolean autoIncrement;
        
        public Column(final String columnName, final String tableName) {
            this(columnName, tableName, false);
        }
    }
    
    /**
     * 操作符枚举.
     * 
     * @author gaohongtao
     * @author zhangliang
     */
    @RequiredArgsConstructor
    public enum BinaryOperator {
        
        EQUAL("="), BETWEEN("BETWEEN"), IN("IN");
        
        @Getter
        private final String expression;
        
        @Override
        public String toString() {
            return expression;
        }
    }
}