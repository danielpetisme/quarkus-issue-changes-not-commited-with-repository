DROP TABLE IF EXISTS fruit;
DROP TABLE IF EXISTS Person;
DROP SEQUENCE IF EXISTS hibernate_sequence;

CREATE SEQUENCE hibernate_sequence start 1 increment 1;

CREATE TABLE fruit (
    id bigint  NOT NULL,
    name VARCHAR(50) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE Person (
    id bigint  NOT NULL,
    name VARCHAR(50) NOT NULL,
    PRIMARY KEY (id)
);