package com.example.demo.repository;

import com.example.demo.domain.StudyPost;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
@AllArgsConstructor
public class StudyPostRepositoryCustomImpl implements StudyPostRepositoryCustom{
    @PersistenceContext
    private final EntityManager em;

    @Override
    public List<StudyPost> findPostsByCategory(String[] categories) { //카테고리가 몇 개가 넘어올지 모르는 상황에서 수행할 수 있는 동적쿼리
        CriteriaBuilder cb=em.getCriteriaBuilder();
        CriteriaQuery<StudyPost> query=cb.createQuery(StudyPost.class);
        Root<StudyPost> post=query.from(StudyPost.class);

        Path<String> categoryPath=post.get("studyCategoryName"); // StudyPost 엔티티의 studyCategoryName 필드를 기반으로 조건 검색
        List<Predicate> predicates=new ArrayList<>();

        for(String category:categories){
            System.out.println("category = " + category);
            predicates.add(cb.equal(categoryPath,category)); // 모든 선택된 categories 에 대해 조건 탐색을 적용하겠다는 것
        }

        query.select(post).where(cb.or(predicates.toArray(new Predicate[predicates.size()]))); //StudyPost 엔티티에 대해 위의 equal 조건을 걸어 select 쿼리,
        //모든 categories 중에 하나만 매칭해도 되는 것이므로 or을 적용

        return em.createQuery(query).getResultList();
    }
}
