package com.rpc.util;

import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

public class SchemaCache {
	
	private static final LRUMap<String, Schema<?>> SCHEMA_CACHE = new LRUMap<String, Schema<?>>(
			4096);

	@SuppressWarnings("unchecked")
	public static <T> Schema<T> getSchema(Class<T> clazz) {
		String className = clazz.getName();
		Schema<T> schema = (Schema<T>) SCHEMA_CACHE.get(className);
		if (null != schema) {
			return schema;
		}
		synchronized (SCHEMA_CACHE) {
			if (null == SCHEMA_CACHE.get(className)) {
				schema = RuntimeSchema.getSchema(clazz);
				SCHEMA_CACHE.put(className, schema);
				return schema;
			} else {
				return (Schema<T>) SCHEMA_CACHE.get(className);
			}
		}
	}
}
