create table indicator_formula
(
    id         uuid not null
        primary key,
    expression varchar(255),
    name       varchar(255),
    p_begin    date,
    p_end      date
);

INSERT INTO indicator_formula (id, expression, name, p_begin, p_end)
VALUES ('2d58b7e2-e5df-494c-acff-645df63643eb', 'a,b->a+b', 'sum', '2022-01-01', null);