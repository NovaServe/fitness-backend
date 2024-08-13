INSERT INTO users(username, full_name, email, phone, password, role, gender, age_group) VALUES
    ('superadmin-test',
    'Superadmin Full Name',
    'superadmin-test@email.com',
    '+301230000001',
    '$2a$10$tyZhvHRirYBsTclmBZD7WOiD3hmXR0yUsCejVJWkOJ1COU8v42zL2',
    'ROLE_SUPERADMIN',
    'Male',
    'Adult'),

    ('admin-test',
    'Admin Full Name',
    'admin-test@email.com',
    '+301230000002',
    '$2a$10$tyZhvHRirYBsTclmBZD7WOiD3hmXR0yUsCejVJWkOJ1COU8v42zL2',
    'ROLE_ADMIN',
    'Female',
    'Adult'),

    ('customer-test',
    'Customer Full Name',
    'customer-test@email.com',
    '+301230000003',
    '$2a$10$tyZhvHRirYBsTclmBZD7WOiD3hmXR0yUsCejVJWkOJ1COU8v42zL2',
    'ROLE_CUSTOMER',
    'Female',
    'Teenager'),

    ('instructor-test',
    'Instructor Full Name',
    'instructor-test@email.com',
    '+301230000004',
    '$2a$10$tyZhvHRirYBsTclmBZD7WOiD3hmXR0yUsCejVJWkOJ1COU8v42zL2',
    'ROLE_INSTRUCTOR',
    'Male',
    'Adult');

--Test password: Password1!
