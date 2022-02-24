package com.example.demo.repository;
import com.example.demo.domain.Hashtag;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;

import static com.example.demo.domain.QHashtag.hashtag;

@Repository
@RequiredArgsConstructor
public class CustomHashtagRepositoryImpl implements CustomHashtagRepository{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Hashtag> findByKeyword(String keyword){
        List<Hashtag> hashtagList = jpaQueryFactory
                .selectFrom(hashtag)
                .where(hashtag.hashtagName.contains(keyword))
                .fetch();
        return hashtagList;
    }
}
