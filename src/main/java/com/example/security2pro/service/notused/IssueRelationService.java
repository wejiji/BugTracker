//package com.example.security2pro.service;
//
//import com.example.security2pro.domain.model.Issue;
//import com.example.security2pro.domain.model.IssueRelation;
//import com.example.security2pro.dto.issue.onetomany.IssueRelationDto;
//import com.example.security2pro.repository.repository_interfaces.IssueRelationRepository;
//import com.example.security2pro.repository.repository_interfaces.IssueRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//import static java.util.stream.Collectors.groupingBy;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class IssueRelationService {
//
//    private final IssueRelationRepository issueRelationRepository;
//    private final IssueRepository issueRepository;
//
//    public IssueRelationDto createOrUpdateIssueRelation(Long issueId, IssueRelationDto issueRelationDto){
//         Issue issue= issueRepository.findByIdWithIssueRelationSet(issueId);
//         Issue causeIssue = issueRepository.getReferenceById(issueRelationDto.getCauseIssueId());
//         issue.addIssueRelation(IssueRelation.createIssueRelation(issue,causeIssue,issueRelationDto.getRelationDescription()));
//         return issueRelationDto;
//    }
//
//    public void deleteIssueRelation(Long issueId,IssueRelationDto issueRelationDto){
//        Issue issue= issueRepository.findByIdWithIssueRelationSet(issueId);
//        issue.deleteIssueRelation(issueRelationDto.getCauseIssueId());
//    }
//
//    public Set<IssueRelationDto> findAllByAffectedIssueId(Long affectedIssueId){
//        Issue issue= issueRepository.findByIdWithIssueRelationSet(affectedIssueId);
//        return issue.getIssueRelationSet().stream().map(IssueRelationDto::new).collect(Collectors.toSet());
//       // return issueRelationRepository.findAllByAffectedIssueId(affectedIssueId).stream().map(IssueRelationDto::new).collect(Collectors.toSet());
//    }


//    public IssueRelationDto createOrUpdateIssueRelation(Long issueId, IssueRelationDto issueRelationDto){
//
//        Map<Long,List<Issue>> issues = issueRepository.findAllById(Set.of(issueId, issueRelationDto.getCauseIssueId())).stream().collect(groupingBy(Issue::getId));
//        if(issues.size()<2){
//            throw new IllegalArgumentException("cause issue not found within the project / an issue cannot set itself as a cause issue");
//        }
//
//        Optional<IssueRelation> existingRelation = issueRelationRepository.findByAffectedIssueIdAndCauseIssueId(issueId,issueRelationDto.getCauseIssueId());
//        if(existingRelation.isPresent()){
//            return new IssueRelationDto(existingRelation.get().update(issueRelationDto.getRelationDescription()));
//        }
//
//        IssueRelation issueRelationOptional = IssueRelation.createIssueRelation(
//                issues.get(issueId).get(0)
//                , issues.get(issueRelationDto.getCauseIssueId()).get(0)
//                , issueRelationDto.getRelationDescription());
//        return new IssueRelationDto(issueRelationRepository.save(issueRelationOptional));
//    }
//
//
//    public void deleteIssueRelation(Long issueId,IssueRelationDto issueRelationDto){
//        Optional<IssueRelation> existingRelation = issueRelationRepository.findByAffectedIssueIdAndCauseIssueId(issueId,issueRelationDto.getCauseIssueId());
//        if(existingRelation.isEmpty()){throw new IllegalArgumentException("issue relation not found");}
//
//        issueRelationRepository.deleteByAffectedIssueIdAndCauseIssueId(issueId,issueRelationDto.getCauseIssueId());
//    }






//}
