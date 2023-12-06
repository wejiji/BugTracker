package com.example.security2pro.repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.envers.*;
import org.hibernate.envers.internal.entities.mapper.relation.lazy.proxy.SetProxy;
import org.hibernate.envers.query.AuditEntity;

import org.springframework.security.util.MethodInvocationUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Repository
public class AuditRepository {

    @PersistenceContext
    EntityManager entityManager;

    public <T> List<String> getHistory (Class<T> entityClass,Long id) throws InvocationTargetException, IllegalAccessException {

        System.out.println("how many queries??? ");
        List resultOfAll = getUpdatedDataListById(entityClass,id);

        List<String> ListOfAuditStrings = new ArrayList<>();

        for(Object[] object: (List<Object[]>) resultOfAll){
            String auditString ="";
            T t2=(T)object[0];
            DefaultRevisionEntity revision=(DefaultRevisionEntity) object[1];
            RevisionType revisionType = (RevisionType) object[2];
            HashSet<String> changedThings=(HashSet<String>)object[3];
            System.out.println("revision id is" + revision.getId());

            if(revisionType.name().equals("ADD")//if it was the first time the entity was saved
             ){ auditString += "revision at " + revision.getRevisionDate() +"{ created by " + MethodInvocationUtils.create(t2, "getCreatedBy", new Object[]{}).getMethod().invoke(t2).toString() +" }";}

            if(!revisionType.name().equals("ADD")//if it was not the first time the entity was saved
            ){
                if (changedThings.isEmpty()) continue;
                auditString += "revision at " + revision.getRevisionDate();
                for(String fieldName : changedThings){
                    auditString += " {";
                    System.out.println("filed name is " + fieldName);
                    String readMethodNameField = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    String readMethodNameLastModified = "getLastModifiedBy";
                    Object returnedFieldValue= MethodInvocationUtils.create(t2, readMethodNameField, new Object[]{}).getMethod().invoke(t2);

                    if(returnedFieldValue!=null)System.out.println(returnedFieldValue.getClass().getSimpleName() +" is the class name ");
                    if(returnedFieldValue!=null){
                        System.out.println("THIS IS EXECUTING >>!!!");
                        //?????????????????????????????????????CollectionProxy does not work ..why??because of generics?
                        if(returnedFieldValue.getClass().isAssignableFrom(SetProxy.class)
                                || (returnedFieldValue.getClass().isAssignableFrom(Collection.class))){
                            readMethodNameField +="Names";
                            System.out.println(readMethodNameField +" is the field name ");
                            returnedFieldValue = "to: "+MethodInvocationUtils.create(t2, readMethodNameField,  new Object[]{}).getMethod().invoke(t2);
                        } else if(returnedFieldValue.getClass()==String.class){
                            returnedFieldValue ="";
                        } else {
                            returnedFieldValue= "to "+returnedFieldValue;
                        }
                    }
                    auditString += " " + fieldName + " field was changed " + returnedFieldValue
                            + " by " + MethodInvocationUtils.create(t2, readMethodNameLastModified,  new Object[]{}).getMethod().invoke(t2).toString() + " }";
                }
            }
            if(StringUtils.hasText(auditString)) ListOfAuditStrings.add(auditString);
        }
        return ListOfAuditStrings;
    }

    public <T> List getUpdatedDataListById(Class<T> entityClass, Long issueId){
        return getAuditReader().createQuery().forRevisionsOfEntityWithChanges(entityClass, true)
                .add(AuditEntity.id().eq(issueId))
                .getResultList();
    }

    private AuditReader getAuditReader(){
        return AuditReaderFactory.get(entityManager);

    }


}
