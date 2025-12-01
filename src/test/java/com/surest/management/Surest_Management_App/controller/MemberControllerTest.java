package com.surest.management.Surest_Management_App.controller;

import com.surest.management.Surest_Management_App.dto.MemberCreateDto;
import com.surest.management.Surest_Management_App.dto.MemberDto;
import com.surest.management.Surest_Management_App.entity.Member;
import com.surest.management.Surest_Management_App.mapper.MemberMapper;
import com.surest.management.Surest_Management_App.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MemberControllerTest {

    @Mock
    private MemberService memberService;

    @Mock
    private MemberMapper mapper;

    @InjectMocks
    private MemberController memberController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // list_returnsPagedMemberDto
    @Test
    void list_returnsPagedMemberDto() {
        MemberDto dto1 = new MemberDto();
        dto1.setId(UUID.randomUUID());
        dto1.setFirstName("A");
        dto1.setLastName("One");
        dto1.setEmail("a@x.com");

        MemberDto dto2 = new MemberDto();
        dto2.setId(UUID.randomUUID());
        dto2.setFirstName("B");
        dto2.setLastName("Two");
        dto2.setEmail("b@x.com");

        Page<MemberDto> page = new PageImpl<>(Arrays.asList(dto1, dto2), PageRequest.of(0, 2), 2);

        // service now returns Page<MemberDto>
        when(memberService.search(anyString(), anyString(), any(Pageable.class))).thenReturn(page);

        // Call controller method directly
        Page<MemberDto> result = memberController.list(0, 2, "firstName,asc", null, null);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("A", result.getContent().get(0).getFirstName());
        assertEquals("B", result.getContent().get(1).getFirstName());

        verify(memberService, times(1)).search(anyString(), anyString(), any(Pageable.class));
        // mapper not used because service returned DTOs
        verifyNoInteractions(mapper);
    }

    // get_existing_returnsOkWithDto
    @Test
    void get_existing_returnsOkWithDto() {
        UUID id = UUID.randomUUID();

        MemberDto dto = new MemberDto();
        dto.setId(id);
        dto.setFirstName("John");
        dto.setEmail("john@x.com");

        // service returns Optional<MemberDto>
        when(memberService.getById(id)).thenReturn(Optional.of(dto));

        var resp = memberController.get(id);
        assertEquals(200, resp.getStatusCodeValue());
        assertNotNull(resp.getBody());
        assertEquals("John", resp.getBody().getFirstName());
        assertEquals("john@x.com", resp.getBody().getEmail());

        verify(memberService).getById(id);
        verifyNoInteractions(mapper);
    }

    // get_notFound_returnsNotFound
    @Test
    void get_notFound_returnsNotFound() {
        UUID id = UUID.randomUUID();
        when(memberService.getById(id)).thenReturn(Optional.empty());

        var resp = memberController.get(id);
        assertEquals(404, resp.getStatusCodeValue());
        assertNull(resp.getBody());

        verify(memberService).getById(id);
        verifyNoInteractions(mapper);
    }

    // create_callsServiceAndReturnsCreatedDto
    @Test
    void create_callsServiceAndReturnsCreatedDto() {
        MemberCreateDto req = new MemberCreateDto();
        req.setFirstName("New");
        req.setLastName("User");
        req.setEmail("new@x.com");
        req.setDateOfBirth(LocalDate.of(1995, 5, 5));

        MemberDto createdDto = new MemberDto();
        createdDto.setId(UUID.randomUUID());
        createdDto.setFirstName(req.getFirstName());
        createdDto.setLastName(req.getLastName());
        createdDto.setEmail(req.getEmail());

        // service returns MemberDto
        when(memberService.create(any(MemberCreateDto.class))).thenReturn(createdDto);

        var resp = memberController.create(req);
        assertEquals(201, resp.getStatusCodeValue());
        assertNotNull(resp.getBody());
        assertEquals("New", resp.getBody().getFirstName());
        assertEquals("new@x.com", resp.getBody().getEmail());

        verify(memberService).create(any(MemberCreateDto.class));
        verifyNoInteractions(mapper);
    }

    // update_existing_returnsOkDto
    @Test
    void update_existing_returnsOkDto() {
        UUID id = UUID.randomUUID();
        MemberCreateDto req = new MemberCreateDto();
        req.setFirstName("Updated");
        req.setLastName("User");
        req.setEmail("upd@x.com");
        req.setDateOfBirth(LocalDate.of(1990, 1, 1));

        MemberDto updatedDto = new MemberDto();
        updatedDto.setId(id);
        updatedDto.setFirstName(req.getFirstName());
        updatedDto.setLastName(req.getLastName());
        updatedDto.setEmail(req.getEmail());

        // service returns Optional<MemberDto>
        when(memberService.update(eq(id), any(MemberCreateDto.class))).thenReturn(Optional.of(updatedDto));

        var resp = memberController.update(id, req);
        assertEquals(200, resp.getStatusCodeValue());
        assertNotNull(resp.getBody());
        assertEquals("Updated", resp.getBody().getFirstName());
        assertEquals("upd@x.com", resp.getBody().getEmail());

        verify(memberService).update(eq(id), any(MemberCreateDto.class));
        verifyNoInteractions(mapper);
    }

    // update_notFound_returns404
    @Test
    void update_notFound_returns404() {
        UUID id = UUID.randomUUID();
        MemberCreateDto req = new MemberCreateDto();
        req.setFirstName("X");
        req.setLastName("Y");
        req.setEmail("x@y.com");
        req.setDateOfBirth(LocalDate.of(1980, 1, 1));

        when(memberService.update(eq(id), any(MemberCreateDto.class))).thenReturn(Optional.empty());

        var resp = memberController.update(id, req);
        assertEquals(404, resp.getStatusCodeValue());
        assertNull(resp.getBody());

        verify(memberService).update(eq(id), any(MemberCreateDto.class));
        verifyNoInteractions(mapper);
    }

    // delete_callsService_andReturnsNoContent
    @Test
    void delete_callsService_andReturnsNoContent() {
        UUID id = UUID.randomUUID();

        doNothing().when(memberService).delete(id);

        var resp = memberController.delete(id);
        assertEquals(204, resp.getStatusCodeValue());

        verify(memberService).delete(id);
        verifyNoInteractions(mapper);
    }
}
