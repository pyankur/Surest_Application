package com.surest.management.Surest_Management_App.Integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.surest.management.Surest_Management_App.dto.MemberCreateDto;
import com.surest.management.Surest_Management_App.dto.MemberDto;
import com.surest.management.Surest_Management_App.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberService memberService;

    private MemberDto testMember;

    @BeforeEach
    void setUp() {
        // seed a member using service (returns MemberDto)
        MemberCreateDto create = new MemberCreateDto();
        create.setFirstName("John");
        create.setLastName("Doe");
        create.setDateOfBirth(LocalDate.parse("2000-12-10"));
        create.setEmail("user_" + UUID.randomUUID() + "@example.com");

        // memberService.create returns MemberDto according to your service changes
        testMember = memberService.create(create);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddMember() throws Exception {
        MemberCreateDto newMember = new MemberCreateDto();
        newMember.setFirstName("Jane");
        newMember.setLastName("Smith");
        newMember.setEmail("user_" + UUID.randomUUID() + "@example.com");
        newMember.setDateOfBirth(LocalDate.parse("1995-05-05"));

        mockMvc.perform(post("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMember)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    @WithMockUser(roles = {"ADMIN","USER"})
    void testGetMemberById() throws Exception {
        mockMvc.perform(get("/api/v1/members/{id}", testMember.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value(testMember.getEmail()));
    }

    @Test
    @WithMockUser(roles = {"ADMIN","USER"})
    void testGetAllMembers() throws Exception {
        mockMvc.perform(get("/api/v1/members")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "lastName,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].firstName").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateMember() throws Exception {
        MemberCreateDto update = new MemberCreateDto();
        update.setFirstName(testMember.getFirstName());
        update.setLastName("Updated");
        update.setDateOfBirth(testMember.getDateOfBirth());
        update.setEmail(testMember.getEmail());

        mockMvc.perform(put("/api/v1/members/{id}", testMember.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Updated"));
    }

    @Test
    void testUnauthorizedAccessIsRejected() throws Exception {
        // no @WithMockUser -> should be 401 or 403 depending on security config
        mockMvc.perform(get("/api/v1/members"))
                .andExpect(status().is4xxClientError());
    }
}
