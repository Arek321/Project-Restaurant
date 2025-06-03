-- V3__add_initial_users.sql

-- Admin user
INSERT INTO users (
    address,
    email,
    first_name,
    last_name,
    password,
    phone_number,
    role,
    username
) VALUES (
             'Admin Street 1',
             'admin@example.com',
             'Admin',
             'User',
             '$2a$10$TlEckpn0USUsPl9FdYAgu.a50w3Fp5glWSxFBY2t6uXJJm/ya6g0C',
             '123456789',
             'ROLE_ADMINISTRATOR',
             'admin'
         );

INSERT INTO users (
    address,
    email,
    first_name,
    last_name,
    password,
    phone_number,
    role,
    username
) VALUES (
             'User Lane 5',
             'user@example.com',
             'Regular',
             'User',
             '$2a$10$TlEckpn0USUsPl9FdYAgu.a50w3Fp5glWSxFBY2t6uXJJm/ya6g0C',
             '987654321',
             'ROLE_USER',
             'user'
         );
