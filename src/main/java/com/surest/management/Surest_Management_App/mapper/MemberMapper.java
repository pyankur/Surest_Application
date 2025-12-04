package com.surest.management.Surest_Management_App.mapper;


import com.surest.management.Surest_Management_App.dto.MemberCreateDto;
import com.surest.management.Surest_Management_App.dto.MemberDto;
import com.surest.management.Surest_Management_App.entity.Member;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface MemberMapper {
    MemberDto toDto(Member m);
//    Member toEntity(MemberCreateDto dto);
}
