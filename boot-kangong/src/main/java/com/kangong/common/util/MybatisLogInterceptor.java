package com.kangong.common.util;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Intercepts({ @Signature(type = StatementHandler.class, method = "update", args = { Statement.class }),
		@Signature(type = StatementHandler.class, method = "query", args = { Statement.class, ResultHandler.class }) })
public class MybatisLogInterceptor implements Interceptor {


	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		/*
		 * Object[] args = invocation.getArgs(); MappedStatement ms = (MappedStatement)
		 * args[0]; Object param = args[1]; BoundSql boundSql = ms.getBoundSql(param);
		 * String query_name = invocation.getMethod().getName(); String query_id =
		 * ms.getId(); System.out.println("query_id:" + query_id); // if
		 * (query_id.contains("xml맵퍼의 로그출력을 원하는 쿼리id값")) {
		 * System.out.println("====================================");
		 * System.out.println("1:" + invocation.getMethod().getName());
		 * System.out.println("====================================");
		 * System.out.println("2:" + ms.getId());
		 * System.out.println("====================================");
		 * System.out.println("3:" + boundSql.getSql());
		 * System.out.println("====================================");
		 * System.out.println("4:" + param);
		 * System.out.println("====================================");
		 * System.out.println("5:" + ms.getId()); // }
		 */
		StatementHandler handler = (StatementHandler) invocation.getTarget();

		String sql = bindSql(handler); // SQL 추출

		System.out.println("=====================================================================");
		System.out.println("{sql} " + sql);
		System.out.println("=====================================================================");

		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
	}

	/**
	 * <pre>
	 * bindSql
	 *
	 * <pre>
	 *
	 * @param boundSql
	 * @param sql
	 * @param param
	 * @return
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("rawtypes")
	private String bindSql(StatementHandler handler) throws NoSuchFieldException, IllegalAccessException {
		BoundSql boundSql = handler.getBoundSql();

		// 쿼리실행시 맵핑되는 파라미터를 구한다
		Object param = handler.getParameterHandler().getParameterObject();
		// 쿼리문을 가져온다(이 상태에서의 쿼리는 값이 들어갈 부분에 ?가 있다)
		String sql = boundSql.getSql();

		// 바인딩 파라미터가 없으면
		if (param == null) {
			sql = sql.replaceFirst("\\?", "''");
			return sql;
		}

		// 해당 파라미터의 클래스가 Integer, Long, Float, Double 클래스일 경우
		if (param instanceof Integer || param instanceof Long || param instanceof Float || param instanceof Double) {
			sql = sql.replaceFirst("\\?", param.toString());
		}
		// 해당 파라미터의 클래스가 String인 경우
		else if (param instanceof String) {
			sql = sql.replaceFirst("\\?", "'" + param + "'");
		}
		// 해당 파라미터의 클래스가 Map인 경우
		else if (param instanceof Map) {
			List<ParameterMapping> paramMapping = boundSql.getParameterMappings();
			for (ParameterMapping mapping : paramMapping) {
				String propValue = mapping.getProperty();
				Object value = ((Map) param).get(propValue);
				if (value == null) {
					continue;
				}

				if (value instanceof String) {
					sql = sql.replaceFirst("\\?", "'" + value + "'");
				} else {
					sql = sql.replaceFirst("\\?", value.toString());
				}
			}
		}
		// 해당 파라미터의 클래스가 사용자 정의 클래스인 경우
		else {
			List<ParameterMapping> paramMapping = boundSql.getParameterMappings();
			Class<? extends Object> paramClass = param.getClass();

			for (ParameterMapping mapping : paramMapping) {
				String propValue = mapping.getProperty();
				Field field = paramClass.getDeclaredField(propValue);
				field.setAccessible(true);
				Class<?> javaType = mapping.getJavaType();
				if (String.class == javaType) {
					sql = sql.replaceFirst("\\?", "'" + field.get(param) + "'");
				} else {
					sql = sql.replaceFirst("\\?", field.get(param).toString());
				}
			}
		}

		// return sql
		return sql;
	}
}
