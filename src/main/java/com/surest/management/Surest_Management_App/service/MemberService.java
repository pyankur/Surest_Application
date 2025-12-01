package com.surest.management.Surest_Management_App.service;

import com.surest.management.Surest_Management_App.dto.MemberCreateDto;
import com.surest.management.Surest_Management_App.dto.MemberDto;
import com.surest.management.Surest_Management_App.entity.Member;
import com.surest.management.Surest_Management_App.exception.BusinessServiceException;
import com.surest.management.Surest_Management_App.mapper.MemberMapper;
import com.surest.management.Surest_Management_App.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    private MemberMapper mapper;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MemberService.class);


    public MemberService(MemberRepository memberRepository,MemberMapper mapper) {
        this.memberRepository = memberRepository;
        this.mapper = mapper;
    }

    @Transactional
    public Page<MemberDto> search(String firstName, String lastName, Pageable pageable) {
        log.info("Searching members with firstName='{}', lastName='{}', page={}, size={}, sort={}",
                firstName, lastName,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        if (firstName == null) firstName = "";
        if (lastName == null) lastName = "";

        Page<Member> result = memberRepository
                .findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(firstName, lastName, pageable);

        log.info("Search completed. Found {} members.", result.getTotalElements());
        return result.map(mapper::toDto);
    }

    @Transactional
    @Cacheable(value = "members", key = "#id")
    public Optional<MemberDto> getById(UUID id) {
        log.info("Fetching member with ID: {}", id);
        Optional<Member> member = memberRepository.findById(id);

        if (member.isPresent()) {
            log.info("Member found: {}", member.get().getEmail());
        } else {
            log.warn("Member with ID {} not found", id);
            throw new BusinessServiceException("Member not found", HttpStatus.NOT_FOUND);
        }

        return member.map(mapper::toDto);
    }

    @Transactional
    public MemberDto create(MemberCreateDto dto) {
        log.info("Creating new member with email: {}", dto.getEmail());
        if (memberRepository.existsByEmail(dto.getEmail())) {
            log.warn("Email already exists", dto.getEmail());
            throw new BusinessServiceException("Email already exists", HttpStatus.CONFLICT);
        }

        Member m = new Member();
        m.setFirstName(dto.getFirstName());
        m.setLastName(dto.getLastName());
        m.setDateOfBirth(dto.getDateOfBirth());
        m.setEmail(dto.getEmail());

        Member saved = memberRepository.save(m);

        log.info("Member created successfully with ID: {}", saved.getId());

        return mapper.toDto(saved);

    }

    @Transactional
    @CachePut(value = "members", key = "#id")
    public Optional<MemberDto> update(UUID id, MemberCreateDto dto) {
        log.info("Updating member ID {} with new data: {}", id, dto.getEmail());

        return memberRepository.findById(id).map(existing -> {
            existing.setFirstName(dto.getFirstName());
            existing.setLastName(dto.getLastName());
            existing.setDateOfBirth(dto.getDateOfBirth());
            existing.setEmail(dto.getEmail());
            Member updated = memberRepository.save(existing);
            log.info("Member with ID {} updated successfully", id);

            return mapper.toDto(updated);
        }).or(() -> {
            log.warn("Cannot update. Member with ID {} not found", id);
            return Optional.empty();
        });
    }

    @Transactional
    @CacheEvict(value = "members", key = "#id")
    public void delete(UUID id) {
        log.info("Deleting member with ID: {}", id);

        if (memberRepository.existsById(id)) {
            memberRepository.deleteById(id);
            log.info("Member with ID {} deleted successfully", id);
            throw new BusinessServiceException("Member not found", HttpStatus.NOT_FOUND);
        } else {
            log.warn("Delete failed. Member with ID {} does not exist", id);
        }
    }
}
