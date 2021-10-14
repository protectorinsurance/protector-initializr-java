-- INITIALIZR:INITIALIZR-DEMO
-- INITIALIZR:DATABASE
create table Employee
(
    Id         int          not null IDENTITY(1, 1) primary key,
    First_Name varchar(128) not null,
    Last_Name  varchar(128) not null
)
-- INITIALIZR:DATABASE
-- INITIALIZR:INITIALIZR-DEMO