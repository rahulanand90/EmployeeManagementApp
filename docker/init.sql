-- PostgreSQL initialization script for Employee Management System
-- This script runs when the PostgreSQL container starts for the first time

-- Create database (already created by POSTGRES_DB env var)
-- CREATE DATABASE employee_management;

-- Create additional schemas if needed
-- CREATE SCHEMA IF NOT EXISTS public;

-- Set up any additional database configuration
-- This file can be used for creating additional users, schemas, or initial data
-- The actual tables will be created by JPA/Hibernate when the Spring Boot app starts

-- Example: Create read-only user
-- CREATE USER readonly WITH PASSWORD 'readonly123';
-- GRANT CONNECT ON DATABASE employee_management TO readonly;
-- GRANT USAGE ON SCHEMA public TO readonly;
-- GRANT SELECT ON ALL TABLES IN SCHEMA public TO readonly;
-- ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO readonly;

-- Log successful initialization
SELECT 'PostgreSQL database initialized successfully for Employee Management System' as status;