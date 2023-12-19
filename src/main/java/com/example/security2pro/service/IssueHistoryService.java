package com.example.security2pro.service;
import com.example.security2pro.domain.model.Issue;
import com.example.security2pro.dto.issue.IssueHistoryDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.*;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class IssueHistoryService {

    @PersistenceContext
    EntityManager entityManager;

    public List<IssueHistoryDto> getIssueHistories (Long issueId){
        List resultOfAll = getUpdatedDataListById(Issue.class, issueId);
        List<IssueHistoryDto> historyList = new ArrayList<>();

        for(Object[] object: (List<Object[]>) resultOfAll){
            Issue issueObject=(Issue)object[0];
            DefaultRevisionEntity revision=(DefaultRevisionEntity) object[1];

            RevisionType revisionType = (RevisionType) object[2];
            HashSet<String> changedThings=(HashSet<String>)object[3];

            Map<String, String> functionMap = Map.of(
                    "assignees", String.join(",",issueObject.getAssigneesNames())
                    ,"title",issueObject.getTitle()
                    ,"description",issueObject.getDescription()
                    ,"completeDate",issueObject.getCompleteDate().toString()
                    ,"priority", issueObject.getPriority().name()
                    ,"status", issueObject.getStatus().name()
                    ,"type", issueObject.getStatus().name()
                    ,"currentSprint", issueObject.getCurrentSprintIdInString()
                    ,"archived",issueObject.isArchived()?"yes":"no"
            );

            if(revisionType.name().equals("ADD")){ // when entity is first created
                historyList.add(new IssueHistoryDto(revision.getId(),"","create","",issueObject.getLastModifiedBy(),issueObject.getLastModifiedDate()));

            } else { // entity is updated
                if (changedThings.isEmpty()) continue; //no modification then go to the next revision

                for(String fieldName : changedThings){
                    historyList.add(new IssueHistoryDto(revision.getId(),fieldName,"update",functionMap.get(fieldName),issueObject.getLastModifiedBy(),issueObject.getLastModifiedDate()));
                }
            }
        }
        return historyList;
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
