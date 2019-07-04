-------------------------------------------------
-- For user creation and login, the following 
-- steps must be executed:
-- 1-Execute this script as superuser logging in 
-- postgresql with the command: 
-- # sudo -u postgres psql
-- 2-After, enable external access with password
-- editing the file: 
-- # sudo vim /etc/postgresql/<version>/main/pg_hba.conf
-- and replacing the following line:
-- local   all             all                                     peer
-- with this line:
-- local   all             all                                     md5
-- 3-Restart postgresql to detect the changes
-- # sudo /etc/init.d/postgresql restart 
-------------------------------------------------

--
-- DROP objects
--
DROP DATABASE IF EXISTS bnzdb;
DROP OWNED BY bnzusr;
DROP ROLE IF EXISTS bnzusr;

--
-- CREATE objects
--

CREATE ROLE bnzusr LOGIN PASSWORD 'bnzpass';
CREATE DATABASE bnzdb;
\c bnzdb 

--
-- GRANT access
--
GRANT ALL PRIVILEGES ON DATABASE bnzdb TO bnzusr;
ALTER SCHEMA public OWNER TO bnzusr;
