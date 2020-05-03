DELETE FROM message;
DELETE FROM user_role;
DELETE FROM usr;

INSERT INTO usr(id, active, password, username, email) VALUES
(1, true, '$2a$08$8.cKpbrAkRbD5UNS8ai8PeNSLdZxE9M9cRHM/NZ2OzC/JVA1q5uQK', 'testUser1', 'test1@mail.ru'),
(2, true, '$2a$08$8.cKpbrAkRbD5UNS8ai8PeNSLdZxE9M9cRHM/NZ2OzC/JVA1q5uQK', 'testUser2', 'test2@mail.ru');

INSERT INTO user_role(user_id, roles) VALUES
(1, 'USER'),
(1, 'ADMIN'),
(2, 'USER');

INSERT INTO message(id, text, user_id) VALUES
(1, 'Hi! My name is testUser1!', 1),
(2, 'Hi! My name is testUser2!', 2),
(3, 'How are you?', 1),
(4, 'Pretty good. And you?', 2),
(5, 'Fine!', 1);
