CREATE TABLE Car (
    id SERIAL PRIMARY KEY,          -- Уникальный идентификатор автомобиля (автоматически генерируется)
    brand VARCHAR(100) NOT NULL,    -- Марка автомобиля (обязательное поле, до 100 символов)
    model VARCHAR(100) NOT NULL,    -- Модель автомобиля (обязательное поле, до 100 символов)
    price DECIMAL(10, 2) NOT NULL   -- Цена автомобиля с точностью до 2 знаков после запятой (обязательное поле)
);

CREATE TABLE Person (
    id SERIAL PRIMARY KEY,                       -- Уникальный идентификатор человека (автоматически генерируется)
    name VARCHAR(100) NOT NULL,                  -- Имя человека (обязательное поле, до 100 символов)
    age INTEGER NOT NULL,                        -- Возраст человека (обязательное целое число)
    has_license BOOLEAN NOT NULL DEFAULT false,  -- Наличие водительских прав (по умолчанию — false)
    car_id INTEGER,                              -- Внешний ключ, ссылающийся на автомобиль (может быть NULL)
    FOREIGN KEY (car_id) REFERENCES Car(id) ON DELETE SET NULL  -- Связь с таблицей Car: при удалении автомобиля поле car_id станет NULL
);
