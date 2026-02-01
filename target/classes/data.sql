-- Sample data for Claim Management System
-- This file is automatically executed by Spring Boot on startup
-- H2 database supports this SQL syntax

-- Insert sample claims for testing and demonstration
INSERT INTO claims (
    claim_number, 
    policy_number, 
    claimant_name, 
    claimant_email, 
    claimant_phone, 
    description, 
    claim_amount, 
    status, 
    incident_date, 
    created_at, 
    updated_at
) VALUES 
-- Claim 1: Auto accident claim
(
    'CLM-2024-000001',
    'POL-AUTO-2024-001',
    'John Smith',
    'john.smith@email.com',
    '+1-555-123-4567',
    'Rear-end collision at intersection of Main Street and Oak Avenue. Minor damage to rear bumper and trunk.',
    2500.00,
    'SUBMITTED',
    '2024-01-15 14:30:00',
    '2024-01-16 09:00:00',
    '2024-01-16 09:00:00'
),

-- Claim 2: Home insurance claim
(
    'CLM-2024-000002',
    'POL-HOME-2024-002',
    'Sarah Johnson',
    'sarah.johnson@email.com',
    '+1-555-234-5678',
    'Water damage in basement due to burst pipe. Affected flooring, drywall, and personal belongings.',
    8750.50,
    'UNDER_REVIEW',
    '2024-01-10 08:15:00',
    '2024-01-11 10:30:00',
    '2024-01-18 14:20:00'
),

-- Claim 3: Health insurance claim
(
    'CLM-2024-000003',
    'POL-HEALTH-2024-003',
    'Michael Brown',
    'michael.brown@email.com',
    '+1-555-345-6789',
    'Emergency room visit for broken arm after bicycle accident. Includes X-rays, treatment, and follow-up care.',
    1250.75,
    'APPROVED',
    '2024-01-08 16:45:00',
    '2024-01-09 11:15:00',
    '2024-01-20 13:45:00'
),

-- Claim 4: Auto insurance claim (high value)
(
    'CLM-2024-000004',
    'POL-AUTO-2024-004',
    'Emily Davis',
    'emily.davis@email.com',
    '+1-555-456-7890',
    'Total loss of vehicle due to collision with deer on Highway 101. Vehicle is beyond economical repair.',
    25000.00,
    'UNDER_REVIEW',
    '2024-01-12 19:20:00',
    '2024-01-13 08:45:00',
    '2024-01-19 16:30:00'
),

-- Claim 5: Property insurance claim
(
    'CLM-2024-000005',
    'POL-PROP-2024-005',
    'Robert Wilson',
    'robert.wilson@email.com',
    '+1-555-567-8901',
    'Theft of electronics and jewelry from home during vacation. Police report filed.',
    5500.25,
    'REJECTED',
    '2024-01-05 00:00:00',
    '2024-01-06 12:00:00',
    '2024-01-22 10:15:00'
),

-- Claim 6: Life insurance claim
(
    'CLM-2024-000006',
    'POL-LIFE-2024-006',
    'Jennifer Martinez',
    'jennifer.martinez@email.com',
    '+1-555-678-9012',
    'Accidental death benefit claim. All required documentation has been submitted.',
    100000.00,
    'PAID',
    '2023-12-28 10:30:00',
    '2024-01-02 14:20:00',
    '2024-01-25 11:00:00'
),

-- Claim 7: Auto insurance claim (minor)
(
    'CLM-2024-000007',
    'POL-AUTO-2024-007',
    'David Anderson',
    'david.anderson@email.com',
    '+1-555-789-0123',
    'Windshield crack from road debris. Needs replacement for safety reasons.',
    350.00,
    'APPROVED',
    '2024-01-20 07:45:00',
    '2024-01-21 09:30:00',
    '2024-01-23 15:10:00'
),

-- Claim 8: Home insurance claim (weather)
(
    'CLM-2024-000008',
    'POL-HOME-2024-008',
    'Lisa Thompson',
    'lisa.thompson@email.com',
    '+1-555-890-1234',
    'Roof damage from hailstorm. Multiple shingles damaged and gutters need repair.',
    4200.80,
    'SUBMITTED',
    '2024-01-22 15:30:00',
    '2024-01-23 08:15:00',
    '2024-01-23 08:15:00'
),

-- Claim 9: Health insurance claim
(
    'CLM-2024-000009',
    'POL-HEALTH-2024-009',
    'Christopher Lee',
    'christopher.lee@email.com',
    '+1-555-901-2345',
    'Routine surgery with complications requiring extended hospital stay.',
    15750.60,
    'UNDER_REVIEW',
    '2024-01-18 11:00:00',
    '2024-01-19 13:45:00',
    '2024-01-24 09:20:00'
),

-- Claim 10: Auto insurance claim (cancelled)
(
    'CLM-2024-000010',
    'POL-AUTO-2024-010',
    'Amanda Garcia',
    'amanda.garcia@email.com',
    '+1-555-012-3456',
    'Minor fender bender in parking lot. Claimant decided to handle privately.',
    800.00,
    'CANCELLED',
    '2024-01-25 12:15:00',
    '2024-01-26 10:00:00',
    '2024-01-27 14:30:00'
);

-- Note: The above data provides a good mix of:
-- 1. Different claim types (auto, home, health, life, property)
-- 2. Various claim amounts (from $350 to $100,000)
-- 3. All possible claim statuses
-- 4. Different dates for testing date-based queries
-- 5. Realistic scenarios for demonstration purposes