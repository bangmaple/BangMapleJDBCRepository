drop database if exists users_management;
create database users_management;
use users_management
go

create table users
(
    username varchar(100) not null
        constraint users_pk
            primary key nonclustered,
    password varchar(100) not null,
    fullname varchar(100) not null,
    role varchar(100) not null
)
go

