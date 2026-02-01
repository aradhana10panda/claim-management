package com.claimmanagement.controller;

import com.claimmanagement.exception.GlobalExceptionHandler;
import com.claimmanagement.model.dto.ClaimRequestDto;
import com.claimmanagement.model.dto.ClaimResponseDto;
import com.claimmanagement.model.entity.ClaimStatus;
import com.claimmanagement.service.ClaimService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for ClaimController without Spring context
 * 
 * This test class provides controller layer testing without loading the full Spring context,
 * making tests faster and more focused on the controller logic.
 * 
 * @author Claim Management Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Claim Controller Unit Tests")
class ClaimControllerUnitTest {

    @Mock
    private ClaimService claimService;

    @InjectMocks
    private ClaimController claimController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ClaimRequestDto requestDto;
    private ClaimResponseDto responseDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(claimController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // For LocalDateTime serialization

        // Setup test data
        requestDto = new ClaimRequestDto();
        requestDto.setPolicyNumber("POL-TEST-001");
        requestDto.setClaimantName("Test User");
        requestDto.setClaimantEmail("test@example.com");
        requestDto.setClaimantPhone("+1-555-000-0000");
        requestDto.setDescription("Test claim description");
        requestDto.setClaimAmount(new BigDecimal("1000.00"));
        requestDto.setStatus(ClaimStatus.SUBMITTED);
        requestDto.setIncidentDate(LocalDateTime.now().minusDays(1));

        responseDto = new ClaimResponseDto();
        responseDto.setId(1L);
        responseDto.setClaimNumber("CLM-2026-000001");
        responseDto.setPolicyNumber("POL-TEST-001");
        responseDto.setClaimantName("Test User");
        responseDto.setClaimantEmail("test@example.com");
        responseDto.setClaimantPhone("+1-555-000-0000");
        responseDto.setDescription("Test claim description");
        responseDto.setClaimAmount(new BigDecimal("1000.00"));
        responseDto.setStatus(ClaimStatus.SUBMITTED);
        responseDto.setIncidentDate(LocalDateTime.now().minusDays(1));
        responseDto.setCreatedAt(LocalDateTime.now());
        responseDto.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("POST /claims - Should create claim successfully")
    void shouldCreateClaimSuccessfully() throws Exception {
        // Arrange
        when(claimService.createClaim(any(ClaimRequestDto.class))).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post("/claims")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.claimNumber").value("CLM-2026-000001"))
                .andExpect(jsonPath("$.policyNumber").value("POL-TEST-001"));

        verify(claimService).createClaim(any(ClaimRequestDto.class));
    }

    @Test
    @DisplayName("GET /claims/{id} - Should retrieve claim by ID")
    void shouldRetrieveClaimById() throws Exception {
        // Arrange
        Long claimId = 1L;
        when(claimService.getClaimById(claimId)).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(get("/claims/{id}", claimId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.claimNumber").value("CLM-2026-000001"));

        verify(claimService).getClaimById(claimId);
    }

    @Test
    @DisplayName("GET /claims - Should retrieve all claims with pagination")
    void shouldRetrieveAllClaimsWithPagination() throws Exception {
        // Arrange
        List<ClaimResponseDto> claims = Arrays.asList(responseDto);
        Pageable pageable = PageRequest.of(0, 10);
        Page<ClaimResponseDto> claimsPage = new PageImpl<>(claims, pageable, 1);
        when(claimService.getAllClaims(any(Pageable.class))).thenReturn(claimsPage);

        // Act & Assert
        mockMvc.perform(get("/claims")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1L));

        verify(claimService).getAllClaims(any(Pageable.class));
    }

    @Test
    @DisplayName("PUT /claims/{id} - Should update claim successfully")
    void shouldUpdateClaimSuccessfully() throws Exception {
        // Arrange
        Long claimId = 1L;
        when(claimService.updateClaim(eq(claimId), any(ClaimRequestDto.class))).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(put("/claims/{id}", claimId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L));

        verify(claimService).updateClaim(eq(claimId), any(ClaimRequestDto.class));
    }

    @Test
    @DisplayName("DELETE /claims/{id} - Should delete claim successfully")
    void shouldDeleteClaimSuccessfully() throws Exception {
        // Arrange
        Long claimId = 1L;
        doNothing().when(claimService).deleteClaim(claimId);

        // Act & Assert
        mockMvc.perform(delete("/claims/{id}", claimId))
                .andExpect(status().isNoContent());

        verify(claimService).deleteClaim(claimId);
    }
}