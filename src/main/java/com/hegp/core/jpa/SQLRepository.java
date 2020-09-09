package com.hegp.core.jpa;

import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SQLRepository {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private SQLRepository sqlRepository;

    public Page<Map> assemblyPageData(String dataSQL, String countSQL, int page, int pagesize) {
        return assemblyPageData(dataSQL, countSQL, page, pagesize, new HashMap());
    }

    public Page<Map> assemblyPageData(String dataSQL, String countSQL, int page, int pagesize, Map<String,Object> params) {
        if (pagesize<1) {
            throw new RuntimeException("pagesize必须大于0");
        }

        int totalCount = queryResultCount(countSQL, params);
        List<Map> data = new ArrayList<>();
        if (totalCount!=0) {
            data = queryPageResultList(dataSQL, page, pagesize, params);
        }
        return new PageImpl(data, PageRequest.of(page, pagesize), totalCount);
    }

    public List<Map> queryPageResultList(String sql, int page, int pagesize) {
        return queryPageResultList(sql, page, pagesize, new HashMap<>());
    }

    /** 前端传过来的页码是从1开始的，entityManager.createNativeQuery的查询的*/
    public List<Map> queryPageResultList(String sql, int page, int pagesize, Map<String,Object> params) {
        Query dataQuery = entityManager.createNativeQuery(sql);
        assemblyParam(dataQuery, params).unwrap(NativeQuery.class)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .setFirstResult((page-1)*pagesize)
                .setMaxResults(pagesize);
        return (List<Map>) dataQuery.getResultList()
                .stream().map(item -> convertKeyToCamel((Map<String, Object>) item))
                .collect(Collectors.toList());
    }

    public List<Map> queryResultList(String sql) {
        return queryResultList(sql, new HashMap<>());
    }

    /** 装配Sql,返回全部结果 */
    public List<Map> queryResultList(String sql, Map<String,Object> params) {
        Query dataQuery = entityManager.createNativeQuery(sql);
        assemblyParam(dataQuery, params).unwrap(NativeQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return (List<Map>)dataQuery.getResultList()
                .stream().map(item -> convertKeyToCamel((Map<String, Object>) item))
                .collect(Collectors.toList());
    }

    public Map queryResultMap(String sql) {
        return queryResultMap(sql, new HashMap<>());
    }

    public Map queryResultMap(String sql, Map<String,Object> params) {
        List<Map> data = queryResultList(sql, params);
        if(data.size() != 0) {
            return data.get(0);
        }
        return null;
    }

    public Integer queryResultCount(String sql) {
        return queryResultCount(sql, new HashMap<>());
    }

    /** 返回SQL查询的数据条数 */
    public Integer queryResultCount(String sql, Map<String,Object> params) {
        Query countQuery = entityManager.createNativeQuery(sql);
        assemblyParam(countQuery, params);
        return Integer.parseInt(countQuery.getSingleResult().toString());
    }

    public int queryModifyCount(String sql) {
        // 直接通过 类内部方法调用，@Transactional注解的方法，不会通过aop切面启动事务，必须通过注入对象的调用方法，才会走aop切面事务@Transactional
        return sqlRepository.queryModifyCount(sql, new HashMap<>());
    }

    /** 执行修改语句，返回受影响行数 */
    @Transactional
    public int queryModifyCount(String sql, Map<String,Object> params) {
        Query dataQuery = entityManager.createNativeQuery(sql);
        assemblyParam(dataQuery, params);
        return dataQuery.executeUpdate();
    }

    /**
     * 为Sql填充参数
     */
    private static Query assemblyParam(Query query, Map<String,Object> params) {
        for (String key:params.keySet()) {
            Object value = params.get(key);
            query.setParameter(key, value);
        }
        return query;
    }

    /**
     * 把map的key转换为驼峰命名
     * @param map map对象
     * @return 返回转换后的值
     */
    private static Map convertKeyToCamel(Map map){
        if(map==null) {
            return null;
        }
        Map linkedHashMap = new LinkedHashMap();
        map.forEach((key, value) -> linkedHashMap.put(convert(key.toString()), value));
        return linkedHashMap;
    }

    /**
     * 数据库查出来的字段，下划线转驼峰
     * @param defaultName
     * @return
     */
    private static String convert(String defaultName) {
        char[] arr = defaultName.toCharArray();
        StringBuilder nameToReturn = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == '_') {
                nameToReturn.append(Character.toUpperCase(arr[++i]));
            } else {
                nameToReturn.append(arr[i]);
            }
        }
        return nameToReturn.toString();
    }
}
