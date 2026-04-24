-- Nutrisoft - Sample Data for Development and Testing
-- This script populates the database with sample patients, professionals, services, and appointments

-- Clear existing data (if needed)
-- DELETE FROM appointments;
-- DELETE FROM services;
-- DELETE FROM professionals;
-- DELETE FROM patients;

-- =============================================================================
-- PATIENTS - Sample patient data
-- =============================================================================

INSERT INTO patients (id, first_name, last_name, email, phone_number, medical_history, created_at, updated_at)
VALUES
    ('550e8400-e29b-41d4-a716-446655440001', 'Juan', 'García', 'juan.garcia@email.com', '305-555-0101', 'Diabetes type 2, Regular exercise', NOW(), NOW()),
    ('550e8400-e29b-41d4-a716-446655440002', 'María', 'López', 'maria.lopez@email.com', '305-555-0102', 'Hypertension, Low sodium diet', NOW(), NOW()),
    ('550e8400-e29b-41d4-a716-446655440003', 'Carlos', 'Rodríguez', 'carlos.rodriguez@email.com', '305-555-0103', 'Obesity management, Fitness goals', NOW(), NOW()),
    ('550e8400-e29b-41d4-a716-446655440004', 'Ana', 'Martínez', 'ana.martinez@email.com', '305-555-0104', 'Gluten sensitivity, Plant-based diet', NOW(), NOW()),
    ('550e8400-e29b-41d4-a716-446655440005', 'Roberto', 'Pérez', 'roberto.perez@email.com', '305-555-0105', 'Athletic performance enhancement', NOW(), NOW())
ON CONFLICT DO NOTHING;

-- =============================================================================
-- PROFESSIONALS - Sample nutrition professionals
-- =============================================================================

INSERT INTO professionals (id, first_name, last_name, specialization, email, phone_number, created_at, updated_at)
VALUES
    ('650e8400-e29b-41d4-a716-446655440001', 'Dr. Patricia', 'Gómez', 'Clinical Nutrition', 'patricia.gomez@nutrisoft.com', '305-555-1001', NOW(), NOW()),
    ('650e8400-e29b-41d4-a716-446655440002', 'Lic. Fernando', 'Silva', 'Sports Nutrition', 'fernando.silva@nutrisoft.com', '305-555-1002', NOW(), NOW()),
    ('650e8400-e29b-41d4-a716-446655440003', 'Dra. Gabriela', 'Fuentes', 'Weight Management', 'gabriela.fuentes@nutrisoft.com', '305-555-1003', NOW(), NOW()),
    ('650e8400-e29b-41d4-a716-446655440004', 'Lic. Miguel', 'Reyes', 'Therapeutic Nutrition', 'miguel.reyes@nutrisoft.com', '305-555-1004', NOW(), NOW())
ON CONFLICT DO NOTHING;

-- =============================================================================
-- SERVICES - Available nutrition services
-- =============================================================================

INSERT INTO services (id, name, description, price, duration_in_minutes, created_at, updated_at)
VALUES
    ('750e8400-e29b-41d4-a716-446655440001', 'Initial Consultation', 'Comprehensive nutritional assessment and plan development', 75.00, 60, NOW(), NOW()),
    ('750e8400-e29b-41d4-a716-446655440002', 'Follow-up Session', 'Progress review and plan adjustment', 50.00, 30, NOW(), NOW()),
    ('750e8400-e29b-41d4-a716-446655440003', 'Weight Management Program', '12-week structured weight loss program', 200.00, 45, NOW(), NOW()),
    ('750e8400-e29b-41d4-a716-446655440004', 'Sports Nutrition Plan', 'Customized nutrition for athletic performance', 85.00, 60, NOW(), NOW()),
    ('750e8400-e29b-41d4-a716-446655440005', 'Diabetes Management', 'Specialized diabetes nutrition counseling', 90.00, 45, NOW(), NOW()),
    ('750e8400-e29b-41d4-a716-446655440006', 'Meal Planning Session', 'Personalized meal planning and preparation tips', 60.00, 30, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- =============================================================================
-- APPOINTMENTS - Sample appointments
-- =============================================================================

-- Scheduled appointments (next 7 days)
INSERT INTO appointments (id, patient_id, professional_id, service_id, start_time, end_time, status, notes, created_at, updated_at)
VALUES
    ('850e8400-e29b-41d4-a716-446655440001',
     '550e8400-e29b-41d4-a716-446655440001',
     '650e8400-e29b-41d4-a716-446655440001',
     '750e8400-e29b-41d4-a716-446655440001',
     NOW() + INTERVAL '2 days' + INTERVAL '09:00',
     NOW() + INTERVAL '2 days' + INTERVAL '10:00',
     'SCHEDULED',
     'Patient is new, comprehensive assessment needed',
     NOW(), NOW()),

    ('850e8400-e29b-41d4-a716-446655440002',
     '550e8400-e29b-41d4-a716-446655440002',
     '650e8400-e29b-41d4-a716-446655440003',
     '750e8400-e29b-41d4-a716-446655440003',
     NOW() + INTERVAL '3 days' + INTERVAL '10:30',
     NOW() + INTERVAL '3 days' + INTERVAL '11:15',
     'CONFIRMED',
     'Patient is committed to weight management program',
     NOW(), NOW()),

    ('850e8400-e29b-41d4-a716-446655440003',
     '550e8400-e29b-41d4-a716-446655440003',
     '650e8400-e29b-41d4-a716-446655440002',
     '750e8400-e29b-41d4-a716-446655440004',
     NOW() + INTERVAL '4 days' + INTERVAL '14:00',
     NOW() + INTERVAL '4 days' + INTERVAL '15:00',
     'SCHEDULED',
     'Athlete preparing for competition, need sports nutrition plan',
     NOW(), NOW()),

    ('850e8400-e29b-41d4-a716-446655440004',
     '550e8400-e29b-41d4-a716-446655440004',
     '650e8400-e29b-41d4-a716-446655440001',
     '750e8400-e29b-41d4-a716-446655440006',
     NOW() + INTERVAL '5 days' + INTERVAL '11:00',
     NOW() + INTERVAL '5 days' + INTERVAL '11:30',
     'SCHEDULED',
     'Gluten-free meal planning assistance',
     NOW(), NOW()),

    ('850e8400-e29b-41d4-a716-446655440005',
     '550e8400-e29b-41d4-a716-446655440005',
     '650e8400-e29b-41d4-a716-446655440002',
     '750e8400-e29b-41d4-a716-446655440004',
     NOW() + INTERVAL '6 days' + INTERVAL '16:00',
     NOW() + INTERVAL '6 days' + INTERVAL '17:00',
     'CONFIRMED',
     'Follow-up on training nutrition',
     NOW(), NOW())

ON CONFLICT DO NOTHING;

-- Completed appointments (past)
INSERT INTO appointments (id, patient_id, professional_id, service_id, start_time, end_time, status, notes, created_at, updated_at)
VALUES
    ('850e8400-e29b-41d4-a716-446655440010',
     '550e8400-e29b-41d4-a716-446655440001',
     '650e8400-e29b-41d4-a716-446655440001',
     '750e8400-e29b-41d4-a716-446655440002',
     NOW() - INTERVAL '5 days' + INTERVAL '09:00',
     NOW() - INTERVAL '5 days' + INTERVAL '09:30',
     'COMPLETED',
     'Follow-up session, good progress',
     NOW(), NOW()),

    ('850e8400-e29b-41d4-a716-446655440011',
     '550e8400-e29b-41d4-a716-446655440002',
     '650e8400-e29b-41d4-a716-446655440003',
     '750e8400-e29b-41d4-a716-446655440005',
     NOW() - INTERVAL '10 days' + INTERVAL '14:00',
     NOW() - INTERVAL '10 days' + INTERVAL '14:45',
     'COMPLETED',
     'Initial diabetes consultation',
     NOW(), NOW())

ON CONFLICT DO NOTHING;

-- Cancelled appointment
INSERT INTO appointments (id, patient_id, professional_id, service_id, start_time, end_time, status, notes, created_at, updated_at)
VALUES
    ('850e8400-e29b-41d4-a716-446655440020',
     '550e8400-e29b-41d4-a716-446655440003',
     '650e8400-e29b-41d4-a716-446655440002',
     '750e8400-e29b-41d4-a716-446655440002',
     NOW() - INTERVAL '7 days' + INTERVAL '10:00',
     NOW() - INTERVAL '7 days' + INTERVAL '10:30',
     'CANCELLED',
     'Patient requested cancellation',
     NOW(), NOW())

ON CONFLICT DO NOTHING;

-- =============================================================================
-- Verify data insertion
-- =============================================================================

-- Show patient count
-- SELECT COUNT(*) as patient_count FROM patients;
-- SELECT COUNT(*) as professional_count FROM professionals;
-- SELECT COUNT(*) as service_count FROM services;
-- SELECT COUNT(*) as appointment_count FROM appointments;

COMMIT;

