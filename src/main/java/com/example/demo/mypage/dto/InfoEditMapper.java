package com.example.demo.mypage.dto;

import com.example.demo.user.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

//@Mapper(componentModel = "spring")
//public interface InfoEditMapper {
//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    // source의 필드가 null 일 때 정책으로 null인 값은 무시한다.
//    void updateUserProfile(MyInfoEditDto myInfoEditDto, @MappingTarget User user);
//}