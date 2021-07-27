
-- 初始化hsfMock菜单
INSERT INTO menu (update_datetime, update_by, create_datetime, create_by, remark, status, sort, url, perms, icon, type, name, pid)
select datetime('now', 'localtime'), 'root', datetime('now', 'localtime'), 'root', NULL, 1, 1, '/views/service_list.html', '#', 'layui-icon-website', 1, 'HSF-MOCK', 0
where not exists (select 1 from menu where name = 'HSF-MOCK');

-- 初始化hsf系统配置项分组
INSERT INTO sys_config_group (update_datetime, update_by, create_datetime, create_by, remark, name)
select datetime('now', 'localtime'), 'root', datetime('now', 'localtime'), 'root', 'HSF-MOCK相关配置', 'HSF-MOCK配置'
where not exists (select 1 from sys_config_group where name = 'HSF-MOCK配置');
-- 初始化hsfMock相关配置：settings文件路径
INSERT INTO sys_config (update_datetime, update_by, create_datetime, create_by, group_id, remark, value, name, [key])
select datetime('now', 'localtime'), 'root', datetime('now', 'localtime'), 'root', (select id from sys_config_group where name = 'HSF-MOCK配置'), '默认{用户目录}/.m2/settings.xml', NULL, 'settings.xml文件地址', 'settings_file_path'
where not exists (select 1 from sys_config where key = 'settings_file_path');