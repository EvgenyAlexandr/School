-- Получить всех студентов, возраст которых находится между 10 и 20
select  * from  hogwarts.public.student where age between 10 and 20;

-- Получить всех студентов, но отобразить только список их имен.
select name from hogwarts.public.student;

-- Получить всех студентов, у которых в имени присутствует буква А
select  * from  hogwarts.public.student where name like '%а%' or name like '%А%';

-- Получить всех студентов, у которых возраст меньше идентификатора.
select * from  hogwarts.public.student where age < id;

-- Получить всех студентов упорядоченных по возрасту.
select  * from  hogwarts.public.student order by age;

