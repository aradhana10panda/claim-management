# Testing Guide - Claim Management Microservice

## 🎯 Testing Overview

This guide covers comprehensive testing strategies for the Claim Management Microservice, including unit tests, integration tests, and API testing with Postman.

## 📋 Testing Strategy

### Testing Pyramid

```
       /\
      /  \     E2E Tests (5%)
     /____\    - Full workflow tests
    /      \   Integration Tests (20%)
   /        \  - API endpoint tests
  /          \ - Database integration tests
 /____________\ Unit Tests (75%)
                - Service layer tests
                - Repository tests
                - Utility tests
```

### Test Types

1. **Unit Tests**: Test individual components in isolation
2. **Integration Tests**: Test component interactions
3. **API Tests**: Test REST endpoints end-to-end
4. **Contract Tests**: Verify API contracts
5. **Performance Tests**: Load and stress testing

## 🧪 Unit Testing

### Service Layer Testing

#### ClaimServiceTest Example

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("Claim Service Tests")
class ClaimServiceTest {

    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private ClaimMapper claimMapper;

    @InjectMocks
    private ClaimServiceImpl claimService;

    @Test
    @DisplayName("Should create claim successfully")
    void shouldCreateClaimSuccessfully() {
        // Given (Arrange)
        ClaimRequestDto requestDto = createValidClaimRequest();
        Claim claim = createClaimEntity();
        ClaimResponseDto expectedResponse = createClaimResponse();
        
        when(claimMapper.toEntity(requestDto)).thenReturn(claim);
        when(claimRepository.save(any(Claim.class))).thenReturn(claim);
        when(claimMapper.toResponseDto(claim)).thenReturn(expectedResponse);

        // When (Act)
        ClaimResponseDto result = claimService.createClaim(requestDto);

        // Then (Assert)
        assertThat(result).isNotNull();
        assertThat(result.getClaimNumber()).isEqualTo(expectedResponse.getClaimNumber());
        
        // Verify interactions
        verify(claimMapper).toEntity(requestDto);
        verify(claimRepository).save(any(Claim.class));
        verify(claimMapper).toResponseDto(claim);
    }
}
```

#### Key Testing Principles

1. **AAA Pattern**: Arrange, Act, Assert
2. **Descriptive Names**: Test method names should describe the scenario
3. **Single Responsibility**: Each test should verify one behavior
4. **Mock External Dependencies**: Isolate the unit under test
5. **Verify Interactions**: Ensure mocks are called correctly

### Repository Layer Testing

#### ClaimRepositoryTest Example

```java
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Claim Repository Tests")
class ClaimRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ClaimRepository claimRepository;

    @Test
    @DisplayName("Should find claim by claim number")
    void shouldFindClaimByClaimNumber() {
        // Given
        Claim claim = createTestClaim("CLM-2024-001");
        entityManager.persistAndFlush(claim);

        // When
        Optional<Claim> found = claimRepository.findByClaimNumber("CLM-2024-001");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getClaimNumber()).isEqualTo("CLM-2024-001");
    }
}
```

#### Repository Testing Best Practices

1. **Use @DataJpaTest**: Configures only JPA-related components
2. **TestEntityManager**: For test data setup
3. **Test Custom Queries**: Verify JPQL and native queries
4. **Test Constraints**: Verify database constraints work
5. **Test Relationships**: Verify entity relationships

## 🔗 Integration Testing

### Controller Integration Tests

#### ClaimControllerIntegrationTest Example

```java
@SpringBootTest
@AutoConfigureTestMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Claim Controller Integration Tests")
class ClaimControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should create claim successfully")
    void shouldCreateClaimSuccessfully() throws Exception {
        // Given
        ClaimRequestDto request = createValidClaimRequest();

        // When & Then
        mockMvc.perform(post("/claims")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.claimNumber").exists())
                .andExpect(jsonPath("$.status").value("SUBMITTED"));
    }
}
```

#### Integration Testing Benefits

1. **End-to-End Verification**: Tests complete request/response cycle
2. **Serialization Testing**: Verifies JSON serialization/deserialization
3. **Validation Testing**: Tests Bean Validation annotations
4. **Error Handling**: Tests exception handling and error responses

## 🌐 API Testing with Postman

### Postman Collection Structure

```
Claim Management API/
├── Health Check/
│   ├── Application Health
│   └── Application Info
├── Claim CRUD Operations/
│   ├── Create Claim - Auto Insurance
│   ├── Create Claim - Home Insurance
│   ├── Get Claim by ID
│   ├── Update Claim
│   └── Delete Claim
├── Claim Search & Filter/
│   ├── Get All Claims (Paginated)
│   ├── Search Claims by Criteria
│   └── Get High-Value Claims
├── Error Scenarios/
│   ├── Get Non-Existent Claim
│   ├── Create Claim with Invalid Data
│   └── Invalid Status Transition
└── Status Workflow Tests/
    └── Complete Claim Workflow
```

### Postman Test Scripts

#### Basic Response Validation

```javascript
pm.test("Status code is 201", function () {
    pm.response.to.have.status(201);
});

pm.test("Response has claim number", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.claimNumber).to.not.be.null;
    pm.globals.set("createdClaimId", jsonData.id);
});

pm.test("Content-Type is application/json", function () {
    pm.expect(pm.response.headers.get("Content-Type")).to.include("application/json");
});
```

#### Advanced Test Scenarios

```javascript
// Test claim workflow
pm.test("Complete workflow test", function () {
    var jsonData = pm.response.json();
    
    // Verify status progression
    pm.expect(jsonData.status).to.be.oneOf(["SUBMITTED", "UNDER_REVIEW", "APPROVED", "PAID"]);
    
    // Store values for next requests
    pm.globals.set("claimId", jsonData.id);
    pm.globals.set("currentStatus", jsonData.status);
});

// Validate business rules
pm.test("Claim amount is positive", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.claimAmount).to.be.above(0);
});
```

### Environment Variables

```json
{
    "baseUrl": "http://localhost:8080",
    "apiVersion": "v1",
    "createdClaimId": "",
    "createdClaimNumber": "",
    "workflowClaimId": ""
}
```

## 🚀 Running Tests

### Maven Commands

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ClaimServiceTest

# Run tests with coverage
mvn test jacoco:report

# Run integration tests only
mvn test -Dtest=*IntegrationTest

# Run tests with specific profile
mvn test -Dspring.profiles.active=test
```

### Test Execution Order

1. **Unit Tests**: Fast, isolated tests
2. **Integration Tests**: Slower, but comprehensive
3. **API Tests**: Manual or automated with Postman/Newman

## 📊 Test Coverage

### Coverage Goals

- **Unit Tests**: 80%+ line coverage
- **Integration Tests**: Cover all API endpoints
- **Business Logic**: 100% coverage of critical paths

### Coverage Report

```bash
# Generate coverage report
mvn jacoco:report

# View report
open target/site/jacoco/index.html
```

### Coverage Analysis

```
Package                          Line Coverage    Branch Coverage
com.claimmanagement.service      95%             90%
com.claimmanagement.controller   85%             80%
com.claimmanagement.repository   90%             85%
Overall                          90%             85%
```

## 🔍 Test Data Management

### Test Data Builders

```java
public class ClaimTestDataBuilder {
    
    public static ClaimRequestDto createValidClaimRequest() {
        ClaimRequestDto dto = new ClaimRequestDto();
        dto.setPolicyNumber("POL-TEST-001");
        dto.setClaimantName("Test User");
        dto.setClaimantEmail("test@example.com");
        dto.setClaimAmount(new BigDecimal("1000.00"));
        dto.setIncidentDate(LocalDateTime.now().minusDays(1));
        return dto;
    }
    
    public static Claim createClaimEntity() {
        Claim claim = new Claim();
        claim.setId(1L);
        claim.setClaimNumber("CLM-2024-001");
        claim.setPolicyNumber("POL-TEST-001");
        claim.setStatus(ClaimStatus.SUBMITTED);
        return claim;
    }
}
```

### Database Test Data

```sql
-- Test data for integration tests
INSERT INTO claims (claim_number, policy_number, claimant_name, claimant_email, 
                   description, claim_amount, status, incident_date, created_at, updated_at)
VALUES ('CLM-TEST-001', 'POL-TEST-001', 'Test User', 'test@example.com',
        'Test claim description', 1000.00, 'SUBMITTED', 
        CURRENT_TIMESTAMP - INTERVAL '1' DAY, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
```

## 🐛 Testing Error Scenarios

### Exception Testing

```java
@Test
@DisplayName("Should throw ClaimNotFoundException when claim not found")
void shouldThrowClaimNotFoundExceptionWhenClaimNotFound() {
    // Given
    Long claimId = 999L;
    when(claimRepository.findById(claimId)).thenReturn(Optional.empty());

    // When & Then
    ClaimNotFoundException exception = assertThrows(
        ClaimNotFoundException.class, 
        () -> claimService.getClaimById(claimId)
    );
    
    assertThat(exception.getMessage()).contains("Claim not found with ID: " + claimId);
}
```

### Validation Testing

```java
@Test
@DisplayName("Should return validation errors for invalid data")
void shouldReturnValidationErrorsForInvalidData() throws Exception {
    ClaimRequestDto invalidRequest = new ClaimRequestDto();
    // Leave required fields empty
    
    mockMvc.perform(post("/claims")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Validation Failed"))
            .andExpect(jsonPath("$.details.validationErrors").exists());
}
```

## 🔄 Continuous Testing

### Test Automation Pipeline

```yaml
# GitHub Actions example
name: Test Pipeline
on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 17
        uses: actions/setup-java@v2
        with:
          java-version: '17'
      - name: Run tests
        run: mvn test
      - name: Generate coverage report
        run: mvn jacoco:report
      - name: Upload coverage
        uses: codecov/codecov-action@v1
```

### Pre-commit Hooks

```bash
#!/bin/sh
# Run tests before commit
mvn test
if [ $? -ne 0 ]; then
    echo "Tests failed. Commit aborted."
    exit 1
fi
```

## 📈 Performance Testing

### Load Testing with JMeter

```xml
<!-- JMeter Test Plan -->
<TestPlan>
  <ThreadGroup>
    <numThreads>100</numThreads>
    <rampTime>60</rampTime>
    <duration>300</duration>
  </ThreadGroup>
  <HTTPSampler>
    <domain>localhost</domain>
    <port>8080</port>
    <path>/api/v1/claims</path>
    <method>GET</method>
  </HTTPSampler>
</TestPlan>
```

### Performance Assertions

```java
@Test
@DisplayName("Should handle concurrent requests")
void shouldHandleConcurrentRequests() throws InterruptedException {
    int threadCount = 10;
    CountDownLatch latch = new CountDownLatch(threadCount);
    
    for (int i = 0; i < threadCount; i++) {
        new Thread(() -> {
            try {
                ClaimResponseDto result = claimService.getClaimById(1L);
                assertThat(result).isNotNull();
            } finally {
                latch.countDown();
            }
        }).start();
    }
    
    latch.await(10, TimeUnit.SECONDS);
}
```

## 🎯 Testing Best Practices

### Do's

1. **Write Tests First**: TDD approach
2. **Test Behavior, Not Implementation**: Focus on what, not how
3. **Use Descriptive Names**: Test names should tell a story
4. **Keep Tests Simple**: One assertion per test when possible
5. **Mock External Dependencies**: Isolate units under test
6. **Test Edge Cases**: Boundary conditions and error scenarios
7. **Maintain Test Data**: Keep test data clean and relevant

### Don'ts

1. **Don't Test Framework Code**: Don't test Spring Boot auto-configuration
2. **Don't Write Brittle Tests**: Avoid testing implementation details
3. **Don't Ignore Failing Tests**: Fix or remove broken tests
4. **Don't Skip Error Testing**: Test exception scenarios
5. **Don't Use Production Data**: Use dedicated test data
6. **Don't Make Tests Dependent**: Each test should be independent

## 🔧 Test Configuration

### Test Profiles

```yaml
# application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
  jpa:
    hibernate:
      ddl-auto: create-drop
  logging:
    level:
      com.claimmanagement: DEBUG
```

### Test Slices

```java
// Test only web layer
@WebMvcTest(ClaimController.class)

// Test only JPA layer
@DataJpaTest

// Test only JSON serialization
@JsonTest

// Full integration test
@SpringBootTest
```

## 📋 Test Checklist

### Before Committing

- [ ] All unit tests pass
- [ ] Integration tests pass
- [ ] Code coverage meets threshold
- [ ] No test warnings or errors
- [ ] Test data is clean

### Before Releasing

- [ ] All test suites pass
- [ ] Performance tests pass
- [ ] API tests with Postman pass
- [ ] Error scenarios tested
- [ ] Documentation updated

## 🎉 Conclusion

Comprehensive testing ensures:

1. **Code Quality**: Catches bugs early
2. **Refactoring Safety**: Enables safe code changes
3. **Documentation**: Tests serve as living documentation
4. **Confidence**: Reduces fear of making changes
5. **Maintainability**: Makes code easier to maintain

Remember: **Good tests are an investment in your codebase's future!**

---

**Happy Testing! 🧪**