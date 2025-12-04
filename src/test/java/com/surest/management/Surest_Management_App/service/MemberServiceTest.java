package com.surest.management.Surest_Management_App.service;

import com.surest.management.Surest_Management_App.dto.MemberCreateDto;
import com.surest.management.Surest_Management_App.dto.MemberDto;
import com.surest.management.Surest_Management_App.entity.Member;
import com.surest.management.Surest_Management_App.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MemberServiceTest {
    @Mock
    MemberRepository repo;

    @InjectMocks
    MemberService service;

    @BeforeEach
    void setup() { MockitoAnnotations.openMocks(this); }

    @Test
    void findById_returnsMember() {
        UUID id = UUID.randomUUID();
        Member m = new Member();
        m.setId(id);
        m.setFirstName("John");
        when(repo.findById(id)).thenReturn(Optional.of(m));

        Optional<MemberDto> res = service.getById(id);
        assertTrue(res.isPresent());
        assertEquals("John", res.get().getFirstName());
        verify(repo, times(1)).findById(id);
    }

    @Test
    void findById_returnsEmpty_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.empty());

        Optional<MemberDto> res = service.getById(id);
        assertTrue(res.isEmpty());
        verify(repo, times(1)).findById(id);
    }

    @Test
    void create_savesAndReturnsMember() {
        MemberCreateDto dto = new MemberCreateDto();
        dto.setFirstName("Alice");
        dto.setLastName("Smith");
        dto.setDateOfBirth(LocalDate.of(1992, 3, 10));
        dto.setEmail("alice@example.com");

        when(repo.save(any(Member.class))).thenAnswer(invocation -> {
            Member arg = invocation.getArgument(0);
            arg.setId(UUID.randomUUID());
            return arg;
        });

        MemberDto created = service.create(dto);
        assertNotNull(created.getId());
        assertEquals("Alice", created.getFirstName());
        assertEquals("alice@example.com", created.getEmail());
        verify(repo, times(1)).save(any(Member.class));
    }

    @Test
    void update_existingMember_updatesAndReturns() {
        UUID id = UUID.randomUUID();

        Member existing = new Member();
        existing.setId(id);
        existing.setFirstName("Old");
        existing.setLastName("Name");
        existing.setEmail("old@example.com");
        existing.setDateOfBirth(LocalDate.of(1980,1,1));

        MemberCreateDto dto = new MemberCreateDto();
        dto.setFirstName("New");
        dto.setLastName("Name");
        dto.setDateOfBirth(LocalDate.of(1985,5,5));
        dto.setEmail("new@example.com");

        when(repo.findById(id)).thenReturn(Optional.of(existing));
        when(repo.save(any(Member.class))).thenAnswer(i -> i.getArgument(0));

        Optional<MemberDto> opt = service.update(id, dto);
        assertTrue(opt.isPresent());
        MemberDto updated = opt.get();
        assertEquals("New", updated.getFirstName());
        assertEquals("new@example.com", updated.getEmail());
        verify(repo, times(1)).findById(id);
        verify(repo, times(1)).save(any(Member.class));
    }

    @Test
    void update_nonExisting_returnsEmpty() {
        UUID id = UUID.randomUUID();
        MemberCreateDto dto = new MemberCreateDto();
        dto.setFirstName("Does");
        dto.setLastName("NotExist");
        dto.setDateOfBirth(LocalDate.of(1985,5,5));
        dto.setEmail("noone@example.com");

        when(repo.findById(id)).thenReturn(Optional.empty());

        Optional<MemberDto> opt = service.update(id, dto);
        assertTrue(opt.isEmpty());
        verify(repo, times(1)).findById(id);
        verify(repo, never()).save(any());
    }

    @Test
    void delete_callsRepository() {
        UUID id = UUID.randomUUID();
        doNothing().when(repo).deleteById(id);
        service.delete(id);
        verify(repo, times(1)).deleteById(id);
    }

    @Test
    void search_returnsPagedResults() {
        Member m1 = new Member();
        m1.setFirstName("A");
        Member m2 = new Member();
        m2.setFirstName("B");

        Page<Member> page = new PageImpl<>(List.of(m1, m2));
        when(repo.findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(page);

        Page<MemberDto> result = service.search("a", "", PageRequest.of(0, 10, Sort.by("firstName")));
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(repo, times(1)).findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(anyString(), anyString(), any(Pageable.class));
    }

}
