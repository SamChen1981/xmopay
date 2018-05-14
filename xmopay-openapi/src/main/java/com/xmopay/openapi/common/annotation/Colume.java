package com.xmopay.openapi.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Colume {
	String value() default "";

	boolean changeCamelToHungary() default true;

	String title() default "";

	JdbcType jdbcType() default JdbcType.EMPTY;

	enum JdbcType {
		EMPTY, ARRAY, BIT, TINYINT, SMALLINT, INTEGER, BIGINT, FLOAT, REAL, DOUBLE, NUMERIC, DECIMAL, CHAR, VARCHAR, LONGVARCHAR, DATE, TIME, TIMESTAMP, BINARY, VARBINARY, LONGVARBINARY, NULL, OTHER, BLOB, CLOB, BOOLEAN, CURSOR, UNDEFINED, NVARCHAR, NCHAR, NCLOB, STRUCT
	}
}
