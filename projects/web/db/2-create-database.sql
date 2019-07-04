
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', 'public', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;

--
-- SEQUENCE
--

CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE hibernate_sequence OWNER TO bnzusr;

SET default_tablespace = '';
SET default_with_oids = false;

--
-- TABLES
--

CREATE TABLE tb_application (
    type character varying(31) NOT NULL,
    id bigint NOT NULL,
    commandline character varying(255),
    executionscript character varying(1000),
    executionscriptenabled boolean NOT NULL,
    firewalltcprules character varying(255),
    firewalludprules character varying(255),
    name character varying(255),
    startupscript character varying(1000)
);

CREATE TABLE tb_application_arguments (
    id bigint NOT NULL,
    arguments character varying(255)
);

CREATE TABLE tb_application_file (
    file_type character varying(31) NOT NULL,
    id bigint NOT NULL,
    fileorder integer,
    spacefile_id bigint NOT NULL,
    applicationarguments_id bigint NOT NULL
);

CREATE TABLE tb_application_image (
    id_application bigint NOT NULL,
    id_image bigint NOT NULL
);

CREATE TABLE tb_credential (
    id bigint NOT NULL,
    credentialdata bytea,
    credentialdatatype character varying(255),
    enabled boolean NOT NULL,
    name character varying(255),
    plugin_id bigint,
    user_id bigint NOT NULL
);

CREATE TABLE tb_group (
    id bigint NOT NULL,
    name character varying(255)
);

CREATE TABLE tb_group_credential (
    id_credential bigint NOT NULL,
    id_group bigint NOT NULL
);

CREATE TABLE tb_group_space (
    id_space bigint NOT NULL,
    id_group bigint NOT NULL
);

CREATE TABLE tb_image (
    id bigint NOT NULL,
    name character varying(255),
    url character varying(255),
    plugin_id bigint
);

CREATE TABLE tb_instance (
    id bigint NOT NULL,
    cloudinstanceip character varying(255),
    cloudinstancename character varying(255),
    cores smallint,
    creationdate timestamp without time zone,
    credentialusage character varying(255),
    executionaftercreation boolean NOT NULL,
    executionobservation character varying(255),
    instanceidentity character varying(255),
    memory double precision,
    phase character varying(255),
    price double precision,
    pricetabledate timestamp without time zone,
    regionname character varying(255),
    status character varying(255),
    typename character varying(255),
    zonename character varying(255),
    applicationarguments_id bigint,
    credential_id bigint,
    executor_id bigint,
    plugin_id bigint
);

CREATE TABLE tb_instance_type (
    id bigint NOT NULL,
    cores smallint,
    memory double precision,
    name character varying(255)
);

CREATE TABLE tb_instance_type_region (
    id_instance_type bigint NOT NULL,
    id_region bigint NOT NULL,
    price double precision,
    pricetable_id bigint NOT NULL
);

CREATE TABLE tb_menu (
    id bigint NOT NULL,
    iconclass character varying(255),
    menuorder smallint,
    name character varying(255),
    path character varying(255),
    parentmenu_id bigint
);

CREATE TABLE tb_plugin (
    id bigint NOT NULL,
    authtype character varying(255),
    cloudtype character varying(255),
    enabled boolean NOT NULL,
    instancereadscope character varying(255),
    instancewritescope character varying(255),
    name character varying(255),
    pluginversion character varying(255),
    storagereadscope character varying(255),
    storagewritescope character varying(255),
    url character varying(255)
);

CREATE TABLE tb_price_table (
    id bigint NOT NULL,
    lastsearchdate timestamp without time zone,
    lastsyncdate timestamp without time zone,
    pricetabledate timestamp without time zone,
    syncmessage character varying(255),
    syncstatus character varying(255),
    plugin_id bigint
);

CREATE TABLE tb_region (
    id bigint NOT NULL,
    name character varying(255)
);

CREATE TABLE tb_role (
    id character varying(255) NOT NULL
);

CREATE TABLE tb_role_menu (
    id_role character varying(255) NOT NULL,
    id_menu bigint NOT NULL
);

CREATE TABLE tb_setting (
    settingname character varying(255) NOT NULL,
    defaultvalue character varying(255) NOT NULL,
    maxsize character varying(255),
    minsize character varying(255),
    required boolean NOT NULL,
    settingtype character varying(255) NOT NULL,
    settingvalue character varying(255) NOT NULL
);

CREATE TABLE tb_space (
    id bigint NOT NULL,
    alocationaftercreation boolean NOT NULL,
    classaprice double precision,
    classbprice double precision,
    creationdate timestamp without time zone,
    name character varying(255),
    pricepergb double precision,
    pricetabledate timestamp without time zone,
    regionname character varying(255),
    credential_id bigint NOT NULL,
    plugin_id bigint,
    user_id bigint NOT NULL
);

CREATE TABLE tb_space_file (
    id bigint NOT NULL,
    name character varying(255),
    publicurl character varying(255),
    virtualname character varying(255),
    space_id bigint
);

CREATE TABLE tb_storage_region (
    id bigint NOT NULL,
    classaprice double precision,
    classbprice double precision,
    price double precision,
    pricetable_id bigint NOT NULL,
    id_region bigint NOT NULL
);

CREATE TABLE tb_user (
    id bigint NOT NULL,
    email character varying(255),
    joined boolean NOT NULL,
    name character varying(255),
    pass character varying(255),
    role_id character varying(255)
);

CREATE TABLE tb_user_group (
    id_group bigint NOT NULL,
    id_user bigint NOT NULL,
    joined boolean NOT NULL,
    userowner boolean NOT NULL
);

CREATE TABLE tb_workflow (
    id bigint NOT NULL,
    creationdate timestamp without time zone,
    executionmessage character varying(255),
    jsongraph character varying(3000),
    jsonmodel character varying(3000),
    name character varying(255),
    status character varying(255),
    user_id bigint NOT NULL
);

CREATE TABLE tb_workflow_node (
    id bigint NOT NULL,
    instance_id bigint,
    workflow_id bigint NOT NULL
);

--
-- CONSTRAINTS PK
--

ALTER TABLE ONLY tb_application_arguments
    ADD CONSTRAINT tb_application_arguments_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_application_file
    ADD CONSTRAINT tb_application_file_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_application
    ADD CONSTRAINT tb_application_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_credential
    ADD CONSTRAINT tb_credential_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_group
    ADD CONSTRAINT tb_group_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_image
    ADD CONSTRAINT tb_image_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_instance
    ADD CONSTRAINT tb_instance_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_instance_type
    ADD CONSTRAINT tb_instance_type_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_instance_type_region
    ADD CONSTRAINT tb_instance_type_region_pkey PRIMARY KEY (id_instance_type, id_region);

ALTER TABLE ONLY tb_menu
    ADD CONSTRAINT tb_menu_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_plugin
    ADD CONSTRAINT tb_plugin_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_price_table
    ADD CONSTRAINT tb_price_table_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_region
    ADD CONSTRAINT tb_region_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_role
    ADD CONSTRAINT tb_role_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_setting
    ADD CONSTRAINT tb_setting_pkey PRIMARY KEY (settingname);

ALTER TABLE ONLY tb_space_file
    ADD CONSTRAINT tb_space_file_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_space
    ADD CONSTRAINT tb_space_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_storage_region
    ADD CONSTRAINT tb_storage_region_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_user_group
    ADD CONSTRAINT tb_user_group_pkey PRIMARY KEY (id_group, id_user);

ALTER TABLE ONLY tb_user
    ADD CONSTRAINT tb_user_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_workflow_node
    ADD CONSTRAINT tb_workflow_node_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_workflow
    ADD CONSTRAINT tb_workflow_pkey PRIMARY KEY (id);

--
-- CONSTRAINTS FK
--

ALTER TABLE ONLY tb_user_group
    ADD CONSTRAINT fk_tb_user_group_tb_group FOREIGN KEY (id_group) REFERENCES tb_group(id);

ALTER TABLE ONLY tb_user_group
    ADD CONSTRAINT fk_tb_user_group_tb_user FOREIGN KEY (id_user) REFERENCES tb_user(id);

ALTER TABLE ONLY tb_space
    ADD CONSTRAINT fk_tb_space_tb_credential FOREIGN KEY (credential_id) REFERENCES tb_credential(id);

ALTER TABLE ONLY tb_space
    ADD CONSTRAINT fk_tb_space_tb_plugin FOREIGN KEY (plugin_id) REFERENCES tb_plugin(id);

ALTER TABLE ONLY tb_space
    ADD CONSTRAINT fk_tb_space_tb_user FOREIGN KEY (user_id) REFERENCES tb_user(id);

ALTER TABLE ONLY tb_credential
    ADD CONSTRAINT fk_tb_credential_tb_plugin FOREIGN KEY (plugin_id) REFERENCES tb_plugin(id);

ALTER TABLE ONLY tb_credential
    ADD CONSTRAINT fk_tb_credential_tb_user FOREIGN KEY (user_id) REFERENCES tb_user(id);

ALTER TABLE ONLY tb_user
    ADD CONSTRAINT fk_tb_user_tb_role FOREIGN KEY (role_id) REFERENCES tb_role(id);

ALTER TABLE ONLY tb_role_menu
    ADD CONSTRAINT fk_tb_role_menu_tb_menu FOREIGN KEY (id_menu) REFERENCES tb_menu(id);

ALTER TABLE ONLY tb_role_menu
    ADD CONSTRAINT fk_tb_role_menu_tb_role FOREIGN KEY (id_role) REFERENCES tb_role(id);

ALTER TABLE ONLY tb_group_space
    ADD CONSTRAINT fk_tb_group_space_tb_space FOREIGN KEY (id_space) REFERENCES tb_space(id);

ALTER TABLE ONLY tb_group_space
    ADD CONSTRAINT fk_tb_group_space_tb_group FOREIGN KEY (id_group) REFERENCES tb_group(id);

ALTER TABLE ONLY tb_instance
    ADD CONSTRAINT fk_tb_instance_tb_credential FOREIGN KEY (credential_id) REFERENCES tb_credential(id);

ALTER TABLE ONLY tb_instance
    ADD CONSTRAINT fk_tb_instance_tb_application FOREIGN KEY (executor_id) REFERENCES tb_application(id);

ALTER TABLE ONLY tb_instance
    ADD CONSTRAINT fk_tb_instance_tb_plugin FOREIGN KEY (plugin_id) REFERENCES tb_plugin(id);

ALTER TABLE ONLY tb_instance
    ADD CONSTRAINT fk_tb_instance_tb_application_arguments FOREIGN KEY (applicationarguments_id) REFERENCES tb_application_arguments(id);

ALTER TABLE ONLY tb_instance_type_region
    ADD CONSTRAINT fk_tb_instance_type_region_tb_region FOREIGN KEY (id_region) REFERENCES tb_region(id);

ALTER TABLE ONLY tb_instance_type_region
    ADD CONSTRAINT fk_tb_instance_type_region_tb_price_table FOREIGN KEY (pricetable_id) REFERENCES tb_price_table(id);

ALTER TABLE ONLY tb_instance_type_region
    ADD CONSTRAINT fk_tb_instance_type_region_tb_instance_type FOREIGN KEY (id_instance_type) REFERENCES tb_instance_type(id);

ALTER TABLE ONLY tb_application_image
    ADD CONSTRAINT fk_tb_application_image_tb_application FOREIGN KEY (id_application) REFERENCES tb_application(id);

ALTER TABLE ONLY tb_application_image
    ADD CONSTRAINT fk_tb_application_image_tb_image FOREIGN KEY (id_image) REFERENCES tb_image(id);

ALTER TABLE ONLY tb_workflow_node
    ADD CONSTRAINT fk_tb_workflow_node_tb_workflow FOREIGN KEY (workflow_id) REFERENCES tb_workflow(id);

ALTER TABLE ONLY tb_workflow_node
    ADD CONSTRAINT fk_tb_workflow_node_tb_instance FOREIGN KEY (instance_id) REFERENCES tb_instance(id);

ALTER TABLE ONLY tb_image
    ADD CONSTRAINT fk_tb_image_tb_plugin FOREIGN KEY (plugin_id) REFERENCES tb_plugin(id);

ALTER TABLE ONLY tb_menu
    ADD CONSTRAINT fk_tb_menu_tb_menu FOREIGN KEY (parentmenu_id) REFERENCES tb_menu(id);

ALTER TABLE ONLY tb_application_file
    ADD CONSTRAINT fk_tb_application_file_tb_application_arguments FOREIGN KEY (applicationarguments_id) REFERENCES tb_application_arguments(id);

ALTER TABLE ONLY tb_application_file
    ADD CONSTRAINT fk_tb_application_file_tb_space_file FOREIGN KEY (spacefile_id) REFERENCES tb_space_file(id);

ALTER TABLE ONLY tb_price_table
    ADD CONSTRAINT fk_tb_price_table_tb_plugin FOREIGN KEY (plugin_id) REFERENCES tb_plugin(id);

ALTER TABLE ONLY tb_storage_region
    ADD CONSTRAINT fk_tb_storage_region_tb_region FOREIGN KEY (id_region) REFERENCES tb_region(id);

ALTER TABLE ONLY tb_storage_region
    ADD CONSTRAINT fk_tb_storage_region_tb_price_table FOREIGN KEY (pricetable_id) REFERENCES tb_price_table(id);

ALTER TABLE ONLY tb_workflow
    ADD CONSTRAINT fk_tb_workflow_tb_user FOREIGN KEY (user_id) REFERENCES tb_user(id);

ALTER TABLE ONLY tb_group_credential
    ADD CONSTRAINT fk_tb_group_credential_tb_credential FOREIGN KEY (id_credential) REFERENCES tb_credential(id);

ALTER TABLE ONLY tb_group_credential
    ADD CONSTRAINT fk_tb_group_credential_tb_group FOREIGN KEY (id_group) REFERENCES tb_group(id);

ALTER TABLE ONLY tb_space_file
    ADD CONSTRAINT fk_tb_space_file_tb_space FOREIGN KEY (space_id) REFERENCES tb_space(id);



