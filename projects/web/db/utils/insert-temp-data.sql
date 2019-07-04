
INSERT INTO tb_plugin(
            id, name, authtype, cloudtype, pluginversion, url, enabled, 
            instancereadscope, instancewritescope, storagereadscope, storagewritescope) VALUES 
(1, 'Google Cloud Platform', 
    'AUTH_BEARER_TOKEN', 
    'google-compute-engine', '0.1', 
    'http://localhost:8080', true, 
    'https://www.googleapis.com/auth/compute.readonly', 
    'https://www.googleapis.com/auth/compute', 
    'https://www.googleapis.com/auth/devstorage.read_only', 
    'https://www.googleapis.com/auth/devstorage.read_write'),
(2, 'Amazon Web Services', 
    'AUTH_AWS', 
    'aws-ec2', '0.1', 
    'http://localhost:8484', true, 
    '', 
    '', 
    '', 
    ''),
(3, 'Local Machine', 
    'AUTH_SUPER_USER', 
    'local-machine', '0.1', 
    'http://localhost:8282', true, 
    '', 
    '', 
    '', 
    '');

INSERT INTO tb_image(id, plugin_id, name, url) VALUES 
(1, 1, 'ubuntu-1604-xenial-v20180627', 'https://www.googleapis.com/compute/v1/projects/ubuntu-os-cloud/global/images/ubuntu-1604-xenial-v20180627'),
(2, 1, 'ubuntu-1204-precise-v20141028', 'https://www.googleapis.com/compute/v1/projects/ubuntu-os-cloud/global/images/ubuntu-1204-precise-v20141028'),
(3, 1, 'ubuntu-1610-yakkety-v20170307', 'https://www.googleapis.com/compute/v1/projects/ubuntu-os-cloud/global/images/ubuntu-1610-yakkety-v20170307'),
(4, 2, 'ubuntu-xenial-16.04-amd64-server-20180627', 'ubuntu/images/hvm-ssd/ubuntu-xenial-16.04-amd64-server-20180627'),
(5, 3, 'linux-4.13.0-45-generic-amd64', 'local-image-url');

INSERT INTO tb_user(
            id, role_id, email, joined, name, pass) VALUES 
(2, 'NORMAL', 'guest@bionimbuz.org.br', true, 'Usuário do Sistema', '');

INSERT INTO tb_group(
            id, name) VALUES 
(1, 'UnB Group'),
(2, 'UFRJ Group'),
(3, 'CiC Group'),
(4, 'Teachers Group');

INSERT INTO tb_user_group(
            id_user, id_group, joined, userowner) VALUES 
(1, 1, true, true),
(2, 1, true, true),
(1, 2, true, true),
(2, 2, true, true),
(1, 3, true, true),
(2, 4, true, true);

-- These data content must be replaced with the encrypted (using web)
INSERT INTO tb_credential(
            id, user_id, plugin_id, name, credentialdatatype, enabled, credentialdata) VALUES 
(1, 1, 1, 'Credential Google', 'application/json', true, '
{
  "type": "",
  "project_id": "",
  "private_key_id": "",
  "private_key": "",
  "client_email": "",
  "client_id": "",
  "auth_uri": "",
  "token_uri": "",
  "auth_provider_x509_cert_url": "",
  "client_x509_cert_url": ""
}
'),
(2, 1, 2, 'Credential AWS', 'text/csv', true, '
AWSAccessKeyId=
AWSSecretKey=
'),
(3, 1, 3, 'Local Machine', 'text/csv', true, '');

INSERT INTO tb_application(
            id, name, type, firewalltcprules, firewalludprules, 
            executionscriptenabled, commandline,  
            executionscript, startupscript) VALUES 

(1, 'Apache', 'E', '80,8080', NULL, false, NULL, NULL, '
#!/bin/bash

apt-get update && apt-get install -y apache2 && hostname > /var/www/index.html
'),

(2, 'Unix Application', 'E', NULL, NULL, true, '{i} {i} {o} {o}', 
'
#!/bin/bash

cat $1 > $3
cat $2 >> $3

echo "Execution time: `date`" >> $3

echo "Extra file execution time: `date`" >> $4"
', '
#!/bin/bash

EXECUTOR=task-executor.jar
curl -o ${EXECUTOR} http://localhost:9000/files/${EXECUTOR}
apt-get update && apt-get install -y openjdk-8-jdk && java -jar ${EXECUTOR} &
 
# Below is used to kill process when parent dies
PID=$!
trap "kill -9 ${PID} && exit " SIGHUP SIGINT SIGTERM
while :
do
    sleep 1 
done'),

(3, 'Windows Application', 'E', NULL, NULL, true, NULL, 
'
type %1 > %3
type %2 >> %3

set h=%TIME:~0,2%
set m=%TIME:~3,2%
set s=%TIME:~6,2%
set time=%h%%m%%s%

echo "Execution time: %time%" >> %3

echo "Extra file execution time: %time%" >> %4
', '
SET EXECUTOR=ttask-executor.jar
curl -o %EXECUTOR% http://localhost:9000/files/%EXECUTOR%
java -jar %EXECUTOR%
');


INSERT INTO public.tb_application_image(
            id_application, id_image) VALUES 
(1, 1),
(1, 2),
(1, 3),
(1, 4),
(1, 5),
(2, 1),
(2, 2),
(2, 3),
(2, 4),
(2, 5);





