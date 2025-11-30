-- Добавляем "Ограничение-проверку" - Возраст студента не может быть меньше 16лет
-- ADD ОГРАНИЧЕНИЕ имяОграничения CHECK (условиеОграничения)
ALTER TABLE hogwarts.public.student
ADD CONSTRAINT age_minimum CHECK (age >= 16);

-- Добавляем "Ограничение-проверку" - Имена студентов должны быть уникальными и не равны нулю.
ALTER TABLE hogwarts.public.student
ADD CONSTRAINT unique_name UNIQUE (name),
ADD CONSTRAINT name_not_null CHECK (name IS NOT NULL);

-- При создании студента без возраста, то возраст по умолчанию 20 лет
ALTER TABLE hogwarts.public.student
ALTER COLUMN age SET DEFAULT 20;

-- Пара “значение названия” - “цвет факультета” должна быть уникальной.
ALTER TABLE hogwarts.public.faculty
ADD CONSTRAINT unique_name_color UNIQUE (name, color);