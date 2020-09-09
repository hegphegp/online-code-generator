package com.hegp.core.utils;

import org.springframework.cglib.beans.BeanMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * 这个类存在大量的逻辑问题，不纠结还可以，如果要纠结，追求不了完美
 */
public class TreeListUtil {

    public static void main(String[] args) {
        List list = new ArrayList<>();
        list.add(new HashMap(){{put("id", 7); put("pid", 0); put("name", "北京市"); put("sort", 7); }});
        list.add(new HashMap(){{put("id", 5); put("pid", 4); put("name", "昌平区"); put("sort", 5); }});
        list.add(new HashMap(){{put("id", 4); put("pid", null); put("name", "北京市"); put("sort", 4); }});
        list.add(new HashMap(){{put("id", 3); put("pid", 2); put("name", "秦州区"); put("sort", 3); }});
        list.add(new HashMap(){{put("id", 2); put("pid", null); put("name", "北京市"); put("sort", 4); }});
        list.add(new HashMap(){{put("id", 1); put("pid", null); put("name", "甘肃省"); put("sort", 1); }});
        list.add(new HashMap(){{put("id", 6); put("pid", 5); put("name", "昌平区"); put("sort", 6); }});
    }

    /**
     * @param list
     * @param idField
     * @param parentIdField
     * @param childrenField
     * @param sortField 排序列可以为空, 不为空必须是数字类型
     * @param <T>
     * @return
     */
    public static <T> List<T> buildAscTreeList(List<T> list, String idField, String parentIdField, String childrenField, String sortField) {
        Map<Object, List<T>> parentIdAndChildrenListMap = new HashMap<>();
        List<T> rootList = buildTreeList(list, idField, parentIdField, childrenField, parentIdAndChildrenListMap);
        if (ObjectUtils.isEmpty(list) || StringUtils.isEmpty(sortField)) return rootList;
        if (list.get(0) instanceof Map) {
            for (Object key : parentIdAndChildrenListMap.keySet()) {
                sortAscMap((List<Map<Object, Object>>)parentIdAndChildrenListMap.get(key), sortField);
            }
            sortAscMap((List<Map<Object, Object>>)rootList, sortField);
        } else {
            for (Object key : parentIdAndChildrenListMap.keySet()) {
                sortAsc(parentIdAndChildrenListMap.get(key), sortField);
            }
            sortAsc(rootList, sortField);
        }
        return rootList;
    }

    /**
     * @param list
     * @param idField
     * @param parentIdField
     * @param childrenField
     * @param sortField 排序列可以为空, 不为空必须是数字类型
     * @param <T>
     * @return
     */
    public static <T> List<T> buildDescTreeList(List<T> list, String idField, String parentIdField, String childrenField, String sortField) {
        Map<Object, List<T>> parentIdAndParentObjectMap = new HashMap<>();
        List<T> rootList = buildTreeList(list, idField, parentIdField, childrenField, parentIdAndParentObjectMap);
        if (ObjectUtils.isEmpty(list) || StringUtils.isEmpty(sortField)) return rootList;
        if (list.get(0) instanceof Map) {
            for (Object key : parentIdAndParentObjectMap.keySet()) {
                sortDescMap((List<Map<Object, Object>>)parentIdAndParentObjectMap.get(key), sortField);
            }
            sortDescMap((List<Map<Object, Object>>)rootList, sortField);
        } else {
            for (Object key:parentIdAndParentObjectMap.keySet()) {
                sortDesc(parentIdAndParentObjectMap.get(key), sortField);
            }
            sortDesc(rootList, sortField);
        }
        return rootList;
    }

    private static <T> List<T> buildTreeList(List<T> list, String idField, String parentIdField, String childrenField, Map<Object, List<T>> parentIdAndChildrenListMap) {
        // 非空校验,冗余的垃圾校验代码
        if (ObjectUtils.isEmpty(list)) return new ArrayList<>();
        list.remove(null);
        if (ObjectUtils.isEmpty(list)) return new ArrayList<>();

        if (list.get(0) instanceof Map) {
            return buildMapTreeList(list, idField, parentIdField, childrenField, parentIdAndChildrenListMap);
        } else {
            return buildObjectTreeList(list, idField, parentIdField, childrenField, parentIdAndChildrenListMap);
        }
    }

    private static <T> List<T> buildMapTreeList(List<T> list, String idField, String parentIdField, String childrenField, Map<Object, List<T>> parentIdAndChildrenListMap) {
        List<Map> rootList = new ArrayList<>();
        List<Map> listMap = (List<Map>)list;
        Map<Object, List<Map>> childrenMapObject = new HashMap<>();
        Map<Object, Map> map = new HashMap<>();
        for (Map t : listMap) {
            map.put(t.get(idField), t);
        }
        for (Map t : listMap) {
            Map parent = map.get(t.get(parentIdField));
            if (parent == null) {
                rootList.add(t);
            } else {
                notExistsThenAdd(childrenMapObject, t, parentIdField, t);
            }
        }
        for (Object key:childrenMapObject.keySet()) {
            map.get(key).put(childrenField, childrenMapObject.get(key));
            parentIdAndChildrenListMap.put(key, (List<T>)childrenMapObject.get(key));
        }
        return (List<T>)rootList;
    }

    private static <T> List<T> buildObjectTreeList(List<T> list, String idField, String parentIdField, String childrenField, Map<Object, List<T>> parentIdAndChildrenListMap) {
        List<T> rootList = new ArrayList<>();
        Map<Object, T> map = new HashMap<>();
        for (T t : list) {
            map.put(BeanMap.create(t).get(idField), t);
        }
        for (T t : list) {
            BeanMap beanMap = BeanMap.create(t);
            T parent = map.get(beanMap.get(parentIdField));
            if (parent == null) {
                rootList.add(t);
            } else {
                List<T> children = notExistsThenAdd(parentIdAndChildrenListMap, beanMap, parentIdField, t);
                BeanMap.create(parent).put(childrenField, children);
            }
        }
        return rootList;
    }

    private static <T> List<T> notExistsThenAdd(Map<Object, List<T>> mapList, Map map, String parentIdField, T item) {
        List<T> children = mapList.get(map.get(parentIdField));
        if (ObjectUtils.isEmpty(children)) {
            children = new ArrayList<>();
            mapList.put(map.get(parentIdField), children);
        }
        children.add(item);
        return children;
    }

    private static <T> void sortAsc(List<T> list, String sortField) {
        Collections.sort(list, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return compareAsc((Number) BeanMap.create(o1).get(sortField), (Number) BeanMap.create(o2).get(sortField));
            }
        });
    }

    private static int compareAsc(Number number1, Number number2) {
        if (number1==null) return -1;
        if (number2==null) return 1;
        BigDecimal number1Value = new BigDecimal(number1.toString());
        BigDecimal number2Value = new BigDecimal(number2.toString());
        return number1Value.compareTo(number2Value);
    }

    private static void sortAscMap(List<Map<Object, Object>> list, String sortField) {
        Collections.sort(list, new Comparator<Map<Object, Object>>() {
            @Override
            public int compare(Map<Object, Object> map1, Map<Object, Object> map2) {
                return compareAsc((Number)map1.get(sortField), (Number)map2.get(sortField));
            }
        });
    }

    private static int compareDesc(Number number1, Number number2) {
        if (number1==null) return 1;
        if (number2==null) return -1;
        BigDecimal number1Value = new BigDecimal(number1.toString());
        BigDecimal number2Value = new BigDecimal(number2.toString());
        return number2Value.compareTo(number1Value);
    }

    private static <T> void sortDesc(List<T> list, String sortField) {
        Collections.sort(list, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return compareDesc((Number) BeanMap.create(o1).get(sortField), (Number) BeanMap.create(o2).get(sortField));
            }
        });
    }

    private static void sortDescMap(List<Map<Object, Object>> list, String sortField) {
        Collections.sort(list, new Comparator<Map<Object, Object>>() {
            @Override
            public int compare(Map<Object, Object> map1, Map<Object, Object> map2) {
                return compareDesc((Number)map1.get(sortField), (Number)map2.get(sortField));
            }
        });
    }
}