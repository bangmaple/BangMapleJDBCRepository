create database equipments_management;

create table equipment_categories
(
    category_id int identity
        primary key,
    category_name nvarchar(100) not null
        unique
)
    go

create table equipments
(
    equipment_id int identity
        primary key,
    equipment_name nvarchar(100) not null
        unique,
    equipment_color nvarchar(100) not null,
    equipment_created_date bigint not null,
    equipment_quantity int not null,
    equipment_category_id int
        references equipment_categories
)
    go

create table roles
(
    role_id int not null
        primary key,
    name varchar(100) not null
        unique
)
    go

create table users
(
    user_id varchar(100) not null
        primary key,
    password varchar(100) not null,
    fullname nvarchar(100) not null,
    email varchar(100) not null
        unique,
    phone varchar(100) not null,
    address varchar(200) not null,
    created_date bigint not null,
    is_activated bit not null,
    role_id int
        references roles,
    otp varchar(6) not null
)
    go

create table equipments_request
(
    equipments_request_id int identity
        primary key,
    requester varchar(100)
        references users,
    assignee varchar(100)
        references users,
    request_status varchar(100) not null,
    requested_date bigint not null,
    equipment_id int
        references equipments
)
    go

create table equipments_request_history
(
    equipments_request_history_id int identity
        primary key,
    equipments_request_id int
        references equipments_request
)
    go
