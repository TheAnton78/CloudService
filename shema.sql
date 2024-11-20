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
CREATE TABLE "Users".public.files();
INSERT INTO public.files(file_data, file_name) VALUES ('test', 'test.txt')




