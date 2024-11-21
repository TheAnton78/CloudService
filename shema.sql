CREATE SCHEMA CloudService
CREATE TABLE users.usersData (
                       id SERIAL,
                       user_name VARCHAR(20) NOT NULL PRIMARY KEY ,
                       password varchar(255),
                        role varchar(10)

);
CREATE TABLE "Users".public.usersdata();
INSERT INTO public.usersdata( username, password, auth_token)
VALUES ('iPhone X', 'Apple', '123');
DROP TABLE "Users".public.files;
DELETE TABLE "Users".public.userdata();
CREATE TABLE "Users".public.files();
INSERT INTO public.files(file_data, file_name) VALUES ('test', 'test.txt')

CREATE TABLE files (
                       id bigserial not null ,
                       user_id BIGINT NOT NULL,
                       fileName VARCHAR(255),
                       fileData VARCHAR(255),
                       hash VARCHAR(255),
                       PRIMARY KEY (id),
                       FOREIGN KEY (user_id) REFERENCES usersdata(id)
);

SELECT f.*
FROM files f
         JOIN usersdata u ON f.user_id = u.id
WHERE f.fileName = :fileName
  AND u.id = :userId;



