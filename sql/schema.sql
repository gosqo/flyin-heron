DROP TABLE IF EXISTS `member` CASCADE;

CREATE TABLE `member` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(50) NOT NULL UNIQUE,
  `nickname` varchar(20) NOT NULL UNIQUE,
  `password` varchar(200) NOT NULL,
  `register_date` timestamp DEFAULT CURRENT_TIMESTAMP,
  `role` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `board`;

CREATE TABLE `board` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` varchar(4000) DEFAULT NULL,
  `register_date` timestamp DEFAULT CURRENT_TIMESTAMP,
  `update_date` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `member_id` bigint DEFAULT NULL,
  `title` varchar(50) NOT NULL,
  `view_count` bigint DEFAULT '0',
  PRIMARY KEY (`id`),
  CONSTRAINT `board_member_id` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
);

DROP TABLE IF EXISTS `token`;

CREATE TABLE `token` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `token` varchar(255) DEFAULT NULL,
  `member_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `token_member_id` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
);
