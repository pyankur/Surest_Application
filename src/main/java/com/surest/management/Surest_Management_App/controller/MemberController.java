package com.surest.management.Surest_Management_App.controller;

import com.surest.management.Surest_Management_App.dto.MemberCreateDto;
import com.surest.management.Surest_Management_App.dto.MemberDto;
import com.surest.management.Surest_Management_App.mapper.MemberMapper;
import com.surest.management.Surest_Management_App.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;
    private final MemberMapper mapper;
    public MemberController(MemberService memberService, MemberMapper
            mapper) {
        this.memberService = memberService;
        this.mapper = mapper;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public Page<MemberDto> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName,asc") String sort,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName
    ) {
        var parts = sort.split(",");
        var sr = Sort.by(Sort.Direction.fromString(parts[1]), parts[0]);
        var pg = PageRequest.of(page, size, sr);
        var p = memberService.search(firstName, lastName, pg);
        return p;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<MemberDto> get(@PathVariable UUID id) {
        return memberService.getById(id)
                .map(m -> ResponseEntity.ok(m))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<MemberDto> create(@Valid @RequestBody
                                            MemberCreateDto dto) {
        var created = memberService.create(dto);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<MemberDto> update(@PathVariable UUID id, @Valid
    @RequestBody MemberCreateDto dto) {
        return memberService.update(id, dto).map(m ->
                ResponseEntity.ok(m)).orElseGet(() ->
                ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        memberService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
