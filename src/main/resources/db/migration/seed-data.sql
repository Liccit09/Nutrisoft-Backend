-- Nutrisoft - Datos de Ejemplo para Desarrollo y Pruebas
-- Este script popula la base de datos con pacientes, profesionales, servicios y citas de ejemplo

-- Limpiar datos existentes (si es necesario)
-- DELETE FROM appointments;
-- DELETE FROM services;
-- DELETE FROM professionals;
-- DELETE FROM patients;

-- =============================================================================
-- PACIENTES - Datos de pacientes de ejemplo
-- =============================================================================

INSERT INTO patients (id, first_name, last_name, date_of_birth, email, phone_number, address, created_at, updated_at)
VALUES
    ('550e8400-e29b-41d4-a716-446655440001', 'Juan', 'García', '1985-03-15', 'juan.garcia@email.com', '305-555-0101', 'Calle Principal 123, Miami', NOW(), NOW()),
    ('550e8400-e29b-41d4-a716-446655440002', 'María', 'López', '1990-07-22', 'maria.lopez@email.com', '305-555-0102', 'Avenida Central 456, Miami Beach', NOW(), NOW()),
    ('550e8400-e29b-41d4-a716-446655440003', 'Carlos', 'Rodríguez', '1988-11-08', 'carlos.rodriguez@email.com', '305-555-0103', 'Calle del Río 789, Coral Gables', NOW(), NOW()),
    ('550e8400-e29b-41d4-a716-446655440004', 'Ana', 'Martínez', '1992-05-30', 'ana.martinez@email.com', '305-555-0104', 'Paseo de la Playa 321, Miami', NOW(), NOW()),
    ('550e8400-e29b-41d4-a716-446655440005', 'Roberto', 'Pérez', '1987-09-12', 'roberto.perez@email.com', '305-555-0105', 'Boulevard del Mar 654, Miami', NOW(), NOW())
ON CONFLICT DO NOTHING;

-- =============================================================================
-- PROFESIONALES - Profesionales de nutrición de ejemplo
-- =============================================================================

INSERT INTO professionals (id, first_name, last_name, specialization, email, phone_number, created_at, updated_at)
VALUES
    ('650e8400-e29b-41d4-a716-446655440001', 'Dra. Patricia', 'Gómez', 'Nutrición Clínica', 'patricia.gomez@nutrisoft.com', '305-555-1001', NOW(), NOW()),
    ('650e8400-e29b-41d4-a716-446655440002', 'Lic. Fernando', 'Silva', 'Nutrición Deportiva', 'fernando.silva@nutrisoft.com', '305-555-1002', NOW(), NOW()),
    ('650e8400-e29b-41d4-a716-446655440003', 'Dra. Gabriela', 'Fuentes', 'Gestión del Peso', 'gabriela.fuentes@nutrisoft.com', '305-555-1003', NOW(), NOW()),
    ('650e8400-e29b-41d4-a716-446655440004', 'Lic. Miguel', 'Reyes', 'Nutrición Terapéutica', 'miguel.reyes@nutrisoft.com', '305-555-1004', NOW(), NOW())
ON CONFLICT DO NOTHING;

-- =============================================================================
-- SERVICIOS - Servicios de nutrición disponibles
-- =============================================================================

INSERT INTO services (id, name, description, price, duration_in_minutes, created_at, updated_at)
VALUES
    ('750e8400-e29b-41d4-a716-446655440001', 'Consulta Inicial', 'Evaluación nutricional integral y desarrollo del plan', 75.00, 60, NOW(), NOW()),
    ('750e8400-e29b-41d4-a716-446655440002', 'Sesión de Seguimiento', 'Revisión del progreso y ajuste del plan', 50.00, 30, NOW(), NOW()),
    ('750e8400-e29b-41d4-a716-446655440003', 'Programa de Gestión del Peso', 'Programa estructurado de 12 semanas para pérdida de peso', 200.00, 45, NOW(), NOW()),
    ('750e8400-e29b-41d4-a716-446655440004', 'Plan de Nutrición Deportiva', 'Nutrición personalizada para el rendimiento atlético', 85.00, 60, NOW(), NOW()),
    ('750e8400-e29b-41d4-a716-446655440005', 'Gestión de la Diabetes', 'Asesoramiento nutricional especializado en diabetes', 90.00, 45, NOW(), NOW()),
    ('750e8400-e29b-41d4-a716-446655440006', 'Sesión de Planificación de Comidas', 'Planificación personalizada de comidas y consejos de preparación', 60.00, 30, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- =============================================================================
-- CITAS - Citas de ejemplo
-- =============================================================================

-- Citas programadas (próximos 7 días)
INSERT INTO appointments (id, patient_id, professional_id, service_id, start_time, mode, status, virtual_meeting_link, created_at, updated_at)
VALUES
    ('850e8400-e29b-41d4-a716-446655440001',
     '550e8400-e29b-41d4-a716-446655440001',
     '650e8400-e29b-41d4-a716-446655440001',
     '750e8400-e29b-41d4-a716-446655440001',
     NOW() + INTERVAL '2 days' + INTERVAL '09:00',
     'IN_PERSON',
     'SCHEDULED',
     NULL,
     NOW(), NOW()),

    ('850e8400-e29b-41d4-a716-446655440002',
     '550e8400-e29b-41d4-a716-446655440002',
     '650e8400-e29b-41d4-a716-446655440003',
     '750e8400-e29b-41d4-a716-446655440003',
     NOW() + INTERVAL '3 days' + INTERVAL '10:30',
     'VIRTUAL',
     'CONFIRMED',
     'https://meet.example.com/session-001',
     NOW(), NOW()),

    ('850e8400-e29b-41d4-a716-446655440003',
     '550e8400-e29b-41d4-a716-446655440003',
     '650e8400-e29b-41d4-a716-446655440002',
     '750e8400-e29b-41d4-a716-446655440004',
     NOW() + INTERVAL '4 days' + INTERVAL '14:00',
     'IN_PERSON',
     'SCHEDULED',
     NULL,
     NOW(), NOW()),

    ('850e8400-e29b-41d4-a716-446655440004',
     '550e8400-e29b-41d4-a716-446655440004',
     '650e8400-e29b-41d4-a716-446655440001',
     '750e8400-e29b-41d4-a716-446655440006',
     NOW() + INTERVAL '5 days' + INTERVAL '11:00',
     'IN_PERSON',
     'SCHEDULED',
     NULL,
     NOW(), NOW()),

    ('850e8400-e29b-41d4-a716-446655440005',
     '550e8400-e29b-41d4-a716-446655440005',
     '650e8400-e29b-41d4-a716-446655440002',
     '750e8400-e29b-41d4-a716-446655440004',
     NOW() + INTERVAL '6 days' + INTERVAL '16:00',
     'VIRTUAL',
     'CONFIRMED',
     'https://meet.example.com/session-002',
     NOW(), NOW())

ON CONFLICT DO NOTHING;

-- Citas completadas (pasadas)
INSERT INTO appointments (id, patient_id, professional_id, service_id, start_time, mode, status, virtual_meeting_link, created_at, updated_at)
VALUES
    ('850e8400-e29b-41d4-a716-446655440010',
     '550e8400-e29b-41d4-a716-446655440001',
     '650e8400-e29b-41d4-a716-446655440001',
     '750e8400-e29b-41d4-a716-446655440002',
     NOW() - INTERVAL '5 days' + INTERVAL '09:00',
     'IN_PERSON',
     'COMPLETED',
     NULL,
     NOW(), NOW()),

    ('850e8400-e29b-41d4-a716-446655440011',
     '550e8400-e29b-41d4-a716-446655440002',
     '650e8400-e29b-41d4-a716-446655440003',
     '750e8400-e29b-41d4-a716-446655440005',
     NOW() - INTERVAL '10 days' + INTERVAL '14:00',
     'VIRTUAL',
     'COMPLETED',
     'https://meet.example.com/session-003',
     NOW(), NOW())

ON CONFLICT DO NOTHING;

-- Cita cancelada
INSERT INTO appointments (id, patient_id, professional_id, service_id, start_time, mode, status, virtual_meeting_link, created_at, updated_at)
VALUES
    ('850e8400-e29b-41d4-a716-446655440020',
     '550e8400-e29b-41d4-a716-446655440003',
     '650e8400-e29b-41d4-a716-446655440002',
     '750e8400-e29b-41d4-a716-446655440002',
     NOW() - INTERVAL '7 days' + INTERVAL '10:00',
     'IN_PERSON',
     'CANCELLED',
     NULL,
     NOW(), NOW())

ON CONFLICT DO NOTHING;

-- =============================================================================
-- Verificar la inserción de datos
-- =============================================================================

-- Mostrar recuento de pacientes
-- SELECT COUNT(*) as patient_count FROM patients;
-- SELECT COUNT(*) as professional_count FROM professionals;
-- SELECT COUNT(*) as service_count FROM services;
-- SELECT COUNT(*) as appointment_count FROM appointments;

COMMIT;

