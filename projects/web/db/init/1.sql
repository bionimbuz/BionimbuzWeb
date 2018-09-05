
-- # --- !Ups

-- TB_SETTING
INSERT INTO tb_setting 
     (settingName,            settingValue,             defaultValue,             minSize, maxSize, settingType, required) VALUES
     ('setting_external_url', 'http://127.0.0.1:9000',  'http://127.0.0.1:9000',  '15',    '100',   'STRING',    TRUE)
;

-- TB_ROLE
INSERT INTO tb_role (id) VALUES
     ('ADMIN')
    ,('NORMAL')
;

-- TB_USER
INSERT INTO tb_user (id, email, joined, name, role_id, pass) VALUES
    -- pass: master
    (1, 'master@bionimbuz.org.br', TRUE, 'Administrador do Sistema', 'ADMIN', '353ba90f8c0b3e0f355a3d6c960b7caed5f2c1412992277c0669a04a62e7dfd35fba9f4631a7dc6d00fb44d93d305cc0b749c7501d9ce86f26148d05101b8324')
;

-- TB_MENU
INSERT INTO tb_menu (id, parentmenu_id, menuorder, path, name, iconclass) VALUES
    (1,  NULL,  1, '#',                     'menu.plugins',                     'glyphicon glyphicon-cloud-upload')
   ,(2,     1,  1, '/adm/list/plugins',     'menu.plugins.administration',      NULL)
   ,(3,     1,  2, '/adm/list/prices',      'menu.plugins.prices',              NULL)
   ,(4,  NULL,  2, '/list/credentials',     'menu.credentials',                 'glyphicon glyphicon-lock')
   ,(5,  NULL,  3, '/list/user/groups',     'menu.groups',                      'glyphicon glyphicon-th')
   ,(6,  NULL,  4, '/adm/list/images',      'menu.images',                      'glyphicon glyphicon-play-circle')
   ,(7,  NULL,  5, '#',                     'menu.applications',                'glyphicon glyphicon-play')
   ,(8,     7,  1, '/adm/coordinator',      'menu.applications.coordinators',   NULL)
   ,(9,     7,  2, '/adm/list/executors',   'menu.applications.executors',      NULL)
   ,(10, NULL,  6, '#',                     'menu.storages',                    'glyphicon glyphicon-hdd')
   ,(11,   10,  1, '/list/spaces',          'menu.spaces',                      NULL)
   ,(12,   10,  2, '/list/space/files',     'menu.files',                       NULL)
   ,(13, NULL,  7, '#',                     'menu.execution',                   'glyphicon glyphicon-screenshot')
   ,(14,   13,  1, '/list/instances',       'menu.instances',                   NULL)
   ,(15,   13,  2, '/list/workflows',       'menu.workflows',                   NULL)
   ,(16, NULL,  8, '/adm/list/settings',    'menu.settings',                   'glyphicon glyphicon-wrench')
;

-- TB_ROLE_MENU
INSERT INTO tb_role_menu (id_menu, id_role) VALUES
    (1,  'ADMIN')
   ,(2,  'ADMIN')
   ,(3,  'ADMIN')
   ,(4,  'ADMIN')
   ,(4,  'NORMAL')
   ,(5,  'ADMIN')
   ,(5,  'NORMAL')
   ,(6,  'ADMIN')
   ,(7,  'ADMIN')
   ,(8,  'ADMIN')
   ,(9,  'ADMIN')
   ,(10, 'ADMIN')
   ,(10, 'NORMAL')
   ,(11, 'ADMIN')
   ,(11, 'NORMAL')
   ,(12, 'ADMIN')
   ,(12, 'NORMAL')
   ,(13, 'ADMIN')
   ,(13, 'NORMAL')
   ,(14, 'ADMIN')
   ,(14, 'NORMAL')
   ,(15, 'ADMIN')
   ,(15, 'NORMAL')
   ,(16, 'ADMIN')
;

-- # --- !Downs

