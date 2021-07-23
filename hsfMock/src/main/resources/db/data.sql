

INSERT INTO menu (update_datetime, update_by, create_datetime, create_by, remark, status, sort, url, perms, icon, type, name, pid)
select datetime('now', 'localtime'), 'root', datetime('now', 'localtime'), 'root', NULL, 1, 1, '/views/service_list.html', '#', 'layui-icon-website', 1, 'HSF-MOCK', 0
where not exists (select 1 from menu where name = 'HSF-MOCK');

