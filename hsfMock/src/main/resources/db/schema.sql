

CREATE TABLE IF NOT EXISTS `service_api` (
  `id` INTEGER primary key AUTOINCREMENT,
  `class_name` varchar(100)  NOT NULL UNIQUE,
  `version` varchar(10) DEFAULT '1.0.0',
  `online_status` char(1) DEFAULT '0'
) ;

CREATE TABLE IF NOT EXISTS `method_info` (
  `id` INTEGER primary key AUTOINCREMENT,
  `service_id` int(10) NOT NULL,
  `method_name` varchar(100) DEFAULT '',
  `method_full_name` varchar(100)  NOT NULL UNIQUE,
  `return_class` varchar(100) DEFAULT '',
  `mock_data` blob DEFAULT ''
) ;