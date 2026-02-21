-- liquibase formatted sql

-- changeset evgeniy:create_student_name_index
CREATE INDEX IF NOT EXISTS idx_student_name ON student(name);

-- changeset evgeniy:create_faculty_name_color_index
CREATE INDEX IF NOT EXISTS idx_faculty_name_color ON faculty(name, color);