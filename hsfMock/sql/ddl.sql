
DROP TABLE IF EXISTS `service_api`;
CREATE TABLE `service_api` (
  `id` int(10) NOT NULL AUTO_INCREMENT ,
  `name` varchar(50) DEFAULT '',
  `class_name` varchar(100)  NOT NULL ,
  `app` varchar(20) DEFAULT 'DEFAULT',
  `version` varchar(10) DEFAULT '1.0.0'
  PRIMARY KEY (`id`)
) ;

DROP TABLE IF EXISTS `method_info`;
CREATE TABLE `method_info` (
  `id` int(10) NOT NULL AUTO_INCREMENT ,
  `service_id` int(10) NOT NULL,
  `method_name` varchar(100) DEFAULT '',
  `method_full_name` varchar(100)  NOT NULL ,
  `return_class` varchar(100) DEFAULT '',
  `mock_data` blob DEFAULT ''
  PRIMARY KEY (`id`)
) ;

DROP TABLE IF EXISTS `mock_data`;
CREATE TABLE `mock_data` (
  `id` int(10) NOT NULL AUTO_INCREMENT ,
  `method_id` int(10) NOT NULL,
  `mock_data` blob DEFAULT ''
  PRIMARY KEY (`id`)
) ;



