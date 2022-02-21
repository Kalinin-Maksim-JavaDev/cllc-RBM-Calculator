create table indicator_formula
(
    name       varchar(255) not null primary key,
    expression varchar(255),
    p_begin    date,
    p_end      date
);

INSERT INTO indicator_formula (name, expression, p_begin, p_end)
VALUES ('AHT', 't_ring,t_inb,t_hold,t_acw,n_inb->(t_ring + t_inb + t_hold + t_acw) / n_inb', '2000-01-01', null);