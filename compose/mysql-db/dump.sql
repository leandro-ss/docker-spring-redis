use mydb;

create table stock_movement
(
    stock_movement_key    binary(16) not null,
    stock_sheet_key       int        not null,
    stock_movement_value  double     not null,
    stock_movement_date   datetime   not null default CURRENT_TIMESTAMP,
    stock_movement_signal bool       not null,
    primary key (stock_movement_key)
);

create table stock_sheet
(
    stock_sheet_key   int    not null,
    stock_sheet_value double not null,
    primary key (stock_sheet_key)
);

alter user 'root' IDENTIFIED WITH mysql_native_password BY 'mydb123';

## create table task
# (
#     task_id          int          not null auto_increment,
#     task_name        varchar(100) not null,
#     task_description text,
#     task_priority    varchar(20),
#     task_status      varchar(20),
#     task_start_time  datetime     not null default CURRENT_TIMESTAMP,
#     task_end_time    datetime     not null default CURRENT_TIMESTAMP,
#     task_archived    bool                  default false,
#     primary key (task_id)
# );

# insert into task
# values (1, 'Gathering Requirement', 'Requirement Gathering', 'MEDIUM', 'ACTIVE', curtime(), curtime() + INTERVAL 3 HOUR, 0);
# insert into task
# values (2, 'Application Designing', 'Application Designing', 'MEDIUM', 'ACTIVE', curtime(), curtime() + INTERVAL 2 HOUR, 0);
# insert into task
# values (3, 'Implementation', 'Implementation', 'MEDIUM', 'ACTIVE', curtime(), curtime() + INTERVAL 3 HOUR, 0);
# insert into task
# values (4, 'Unit Testing', 'Unit Testing', 'LOW', 'ACTIVE', curtime(), curtime() + INTERVAL 4 HOUR, 0);
# insert into task
# values (5, 'Maintanence', 'Maintanence', 'LOW', 'ACTIVE', curtime(), curtime() + INTERVAL 5 HOUR, 0);