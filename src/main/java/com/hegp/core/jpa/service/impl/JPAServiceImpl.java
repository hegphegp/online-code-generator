package com.hegp.core.jpa.service.impl;

import com.hegp.core.jpa.service.JPAService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.ResolvableType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import java.util.*;

@Transactional(readOnly = true)
public class JPAServiceImpl<T, ID> implements JPAService<T, ID>, ApplicationContextAware {
    public EntityManager entityManager;
    public SimpleJpaRepository<T, ID> simpleJpaRepository;
    public ApplicationContext applicationContext;
    public Class currentEntityClass;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        /**
        String[] ret=applicationContext.getBeanDefinitionNames();
        for(String curr:ret){
            System.out.println(curr);
        }
         */
        this.applicationContext = applicationContext;
        synchronized (JPAServiceImpl.class) {
            if (simpleJpaRepositoryMap.size()==0) {
                Map<String, EntityManager> map = applicationContext.getBeansOfType(EntityManager.class);
                for (String key : map.keySet()) {
                    Set<EntityType<?>> set = map.get(key).getMetamodel().getEntities();
                    for (EntityType entityType : set) {
                        entityManagerMap.put(entityType.getJavaType(), map.get(key));
                        simpleJpaRepositoryMap.put(entityType.getJavaType(), new SimpleJpaRepository(entityType.getJavaType(), map.get(key)));
                    }
                }
            }
        }
        entityManager = entityManagerMap.get(getGenericEntityClass());
        simpleJpaRepository = simpleJpaRepositoryMap.get(getGenericEntityClass());
    }

    // 获取泛型的具体实现类,Spring的提供工具类,用于获取继承的父类是泛型的信息
    public Class getGenericEntityClass() {
        if (currentEntityClass==null) {
            ResolvableType resolvableType = ResolvableType.forClass(getClass());
            currentEntityClass = resolvableType.getSuperType().getGeneric(0).resolve();
        }
        return currentEntityClass;
    }

    @Override
    public T find(ID id) {
        Assert.notNull(id, "id must not be null!");
        Optional<T> optional = simpleJpaRepository.findById(id);
        return optional.isPresent()? optional.get():null;
    }

    @Override
    @Transactional
    public T save(T entity) {
        Assert.notNull(entity, "entity must not be null!");
        return simpleJpaRepository.save(entity);
    }

    @Override
    @Transactional
    public List<T> save(T[] entities) {
        Assert.notNull(entities, "entities must not be null!");
        return simpleJpaRepository.saveAll(Arrays.asList(entities));
    }

    @Override
    @Transactional
    public List<T> save(List<T> entities) {
        Assert.notNull(entities, "entities must not be null!");
        return simpleJpaRepository.saveAll(entities);
    }

    @Override
    @Transactional
    public void delete(T entity) {
        Assert.notNull(entity, "entity must not be null!");
        simpleJpaRepository.delete(entity);
    }

    @Override
    @Transactional
    public void delete(T[] entities) {
        Assert.notNull(entities, "entities must not be null!");
        simpleJpaRepository.deleteInBatch(Arrays.asList(entities));
    }

    @Override
    @Transactional
    public void delete(List<T> entities) {
        simpleJpaRepository.deleteInBatch(entities);
    }

    @Override
    @Transactional
    public void deleteById(ID id) {
        simpleJpaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteById(ID[] ids) {
        Assert.notNull(ids, "Ids must not be null!");
        List<T> entities = simpleJpaRepository.findAllById(Arrays.asList(ids));
        simpleJpaRepository.deleteInBatch(entities);
    }

    @Override
    @Transactional
    public void deleteById(List<ID> ids) {
        Assert.notNull(ids, "Ids must not be null!");
        List<T> entities = simpleJpaRepository.findAllById(ids);
        simpleJpaRepository.deleteInBatch(entities);
    }

    @Override
    public Page<T> page(Pageable pageable) {
        return simpleJpaRepository.findAll(pageable);
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public EntityManager getEntityManager(Class clazz) {
        return entityManagerMap.get(clazz);
    }

    @Override
    public SimpleJpaRepository<T, ID> getRepository() {
        return simpleJpaRepository;
    }

    @Override
    public SimpleJpaRepository<T, ID> getRepository(Class clazz) {
        return simpleJpaRepositoryMap.get(clazz);
    }
}
