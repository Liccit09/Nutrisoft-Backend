-- Initial Schema for Nutrisoft Appointment Management System
-- Created for PostgreSQL 16


-- Table: patients
-- Stores patient information for the appointment context
CREATE TABLE IF NOT EXISTS patients (
    id UUID PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    date_of_birth DATE,
    email VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    address VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_patients_email ON patients(email);
CREATE INDEX IF NOT EXISTS idx_patients_phone ON patients(phone_number);

-- Table: professionals
-- Stores professional (nutritionist) information
CREATE TABLE IF NOT EXISTS professionals (
    id UUID PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    specialization VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_professionals_email ON professionals(email);
CREATE INDEX IF NOT EXISTS idx_professionals_specialization ON professionals(specialization);

-- Table: services
-- Stores nutrition services offered
CREATE TABLE IF NOT EXISTS services (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    duration_in_minutes INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_services_name ON services(name);

-- Table: appointments
-- Stores appointments (the aggregate root)
CREATE TABLE IF NOT EXISTS appointments (
    id UUID PRIMARY KEY,
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    professional_id UUID NOT NULL REFERENCES professionals(id) ON DELETE CASCADE,
    service_id UUID NOT NULL REFERENCES services(id) ON DELETE CASCADE,
    start_time TIMESTAMP NOT NULL,
    mode VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    virtual_meeting_link VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_appointments_patient_id ON appointments(patient_id);
CREATE INDEX IF NOT EXISTS idx_appointments_professional_id ON appointments(professional_id);
CREATE INDEX IF NOT EXISTS idx_appointments_status ON appointments(status);
CREATE INDEX IF NOT EXISTS idx_appointments_start_time ON appointments(start_time);

-- Comments for documentation
COMMENT ON TABLE patients IS 'Stores patient information within the Appointment context. In future, this may be replaced with a reference to a Patient bounded context.';
COMMENT ON TABLE professionals IS 'Stores professional information within the Appointment context. In future, this may be replaced with a reference to a Professional bounded context.';
COMMENT ON TABLE services IS 'Stores service information within the Appointment context. In future, this may be replaced with a reference to a Service bounded context.';
COMMENT ON TABLE appointments IS 'Stores appointment aggregate roots. This is the core entity of the Appointment bounded context.';


-- Migration: Create Credentials Table
-- Description: Create table for storing authentication credentials
-- This table stores credentials (email, password, role) without using a User entity
-- Credentials are linked to domain aggregates (Patient, Professional, etc.) via aggregateId

CREATE TABLE IF NOT EXISTS credentials (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    aggregate_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT true,
    CONSTRAINT fk_credential_aggregate UNIQUE (aggregate_id)
);

-- Create index on email for faster lookups during login
CREATE INDEX idx_credentials_email ON credentials(email);

-- Create index on aggregate_id for finding credentials by aggregate
CREATE INDEX idx_credentials_aggregate_id ON credentials(aggregate_id);

-- Create index on role for role-based queries
CREATE INDEX idx_credentials_role ON credentials(role);

-- Create schedules table for availability management
CREATE TABLE IF NOT EXISTS schedules (
    id UUID PRIMARY KEY,
    professional_id UUID NOT NULL UNIQUE,
    weekly_schedule_json TEXT NOT NULL,
    breaks_json TEXT,
    special_days_json TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_schedules_professional_id FOREIGN KEY (professional_id)
        REFERENCES professionals(id) ON DELETE CASCADE
);

-- Create index for faster lookups
CREATE INDEX IF NOT EXISTS idx_schedules_professional_id ON schedules(professional_id);

-- Create refresh_tokens table for managing refresh token lifecycle
-- This table enables token revocation, session management, and audit trail
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    credential_id UUID NOT NULL,
    token TEXT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT false,
    revoked_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    used_at TIMESTAMP,
    user_agent VARCHAR(500),
    ip_address VARCHAR(45),
    CONSTRAINT fk_refresh_token_credential FOREIGN KEY (credential_id)
        REFERENCES credentials(id) ON DELETE CASCADE
);

-- Create indexes for efficient queries
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_credential_id ON refresh_tokens(credential_id);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_revoked ON refresh_tokens(revoked);

-- Create index to help find valid (non-revoked, non-expired) tokens
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_valid
    ON refresh_tokens(credential_id)
    WHERE revoked = false;
