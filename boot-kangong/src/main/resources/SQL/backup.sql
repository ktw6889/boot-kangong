--설치

--bin 폴더로 이동하여 아래 명령어 실행
mysqld --install
--Service succesfully installed 출력

mysqld --initialize
-- mysql 폴더안에 data 폴더가 생성

net start mysql
--mysql 서비스가 잘 실행되었습니다. 출력

mysql -u root -p
-- 실패
--컴퓨터이름.err 파일안에 root'@'localhost 임시비밀 번호가 적혀있음

mysql> alter user 'root'@'localhost' identified by 'zkfltmak';
--비밀번호 변경  root@localhost  zkfltmak

mysql>exit

\bin>mysql -u root -p
--ENTER PASSWORD:

mysql>show databases



--db 생성
CREATE DATABASE SECKIMDB;

--user 생성
CREATE USER 'SECKIM'@'localhost' IDENTIFIED BY '1234';
ALTER USER 'SECKIM'@'localhost' IDENTIFIED WITH mysql_native_password BY '1234';
ALTER  USER 'SECKIM'@'localhost' IDENTIFIED BY '1234';

--모든 권한 생성
GRANT ALL PRIVILEGES ON SECKIMDB.* TO 'SECKIM'@'localhost';


--dbms에 적용 
FLUSH PRIVILEGES;

quit
exit

CREATE TABLE `authorities` (
  `username` varchar(50) NOT NULL,
  `authority` varchar(50) NOT NULL,
  UNIQUE KEY `ix_auth_username` (`username`,`authority`),
  CONSTRAINT `fk_authorities_users` FOREIGN KEY (`username`) REFERENCES `users` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into `authorities`(`username`,`authority`) values ('admin00','ROLE_ADMIN');
insert into `authorities`(`username`,`authority`) values ('admin00','ROLE_MEMBER');
insert into `authorities`(`username`,`authority`) values ('member00','ROLE_MEMBER');
insert into `authorities`(`username`,`authority`) values ('user00','ROLE_USER');

CREATE TABLE `board` (
  `bno` bigint(20) NOT NULL AUTO_INCREMENT,
  `moddate` datetime(6) DEFAULT NULL,
  `regdate` datetime(6) DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `writer_email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`bno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `boot_tbl_board` (
  `bno` bigint(20) NOT NULL AUTO_INCREMENT,
  `moddate` datetime(6) DEFAULT NULL,
  `regdate` datetime(6) DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `writer_email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`bno`),
  KEY `FKq2s7c2fagb6j25jy9nheoaf0p` (`writer_email`),
  CONSTRAINT `FKq2s7c2fagb6j25jy9nheoaf0p` FOREIGN KEY (`writer_email`) REFERENCES `boot_tbl_member` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8;


insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (1,'2021-05-03 11:10:51','2021-05-03 11:10:51','Content....1','Title...1','user1@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (2,'2021-05-03 11:10:51','2021-05-03 11:10:51','Content....2','Title...2','user2@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (3,'2021-05-03 11:10:51','2021-05-03 11:10:51','Content....3','Title...3','user3@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (4,'2021-05-03 11:10:51','2021-05-03 11:10:51','Content....4','Title...4','user4@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (5,'2021-05-03 11:10:51','2021-05-03 11:10:51','Content....5','Title...5','user5@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (6,'2021-05-03 11:10:51','2021-05-03 11:10:51','Content....6','Title...6','user6@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (7,'2021-05-03 11:10:51','2021-05-03 11:10:51','Content....7','Title...7','user7@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (8,'2021-05-03 11:10:51','2021-05-03 11:10:51','Content....8','Title...8','user8@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (9,'2021-05-03 11:10:51','2021-05-03 11:10:51','Content....9','Title...9','user9@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (10,'2021-05-03 11:10:51','2021-05-03 11:10:51','Content....10','Title...10','user10@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (11,'2021-05-03 11:10:51','2021-05-03 11:10:51','Content....11','Title...11','user11@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (12,'2021-05-03 11:10:51','2021-05-03 11:10:51','Content....12','Title...12','user12@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (13,'2021-05-03 11:10:51','2021-05-03 11:10:51','Content....13','Title...13','user13@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (14,'2021-05-03 11:10:51','2021-05-03 11:10:51','Content....14','Title...14','user14@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (15,'2021-05-03 11:10:51','2021-05-03 11:10:51','Content....15','Title...15','user15@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (16,'2021-05-03 11:10:51','2021-05-03 11:10:51','Content....16','Title...16','user16@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (17,'2021-05-03 11:10:51','2021-05-03 11:10:51','Content....17','Title...17','user17@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (18,'2021-05-03 11:10:51','2021-05-03 11:10:51','Content....18','Title...18','user18@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (19,'2021-05-03 11:10:51','2021-05-03 11:10:51','Content....19','Title...19','user19@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (20,'2021-05-03 11:10:51','2021-05-03 11:10:51','Content....20','Title...20','user20@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (21,'2021-05-03 11:10:51','2021-05-03 11:10:51','Content....21','Title...21','user21@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (22,'2021-05-03 11:10:51','2021-05-03 11:10:51','Content....22','Title...22','user22@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (23,'2021-05-03 11:10:51','2021-05-03 11:10:51','Content....23','Title...23','user23@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (24,'2021-05-03 11:10:51','2021-05-03 11:10:51','Content....24','Title...24','user24@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (25,'2021-05-03 11:10:51','2021-05-03 11:10:51','Content....25','Title...25','user25@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (26,'2021-05-03 11:10:51','2021-05-03 11:10:51','Content....26','Title...26','user26@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (27,'2021-05-03 11:10:51','2021-05-03 11:10:51','Content....27','Title...27','user27@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (28,'2021-05-03 11:10:51','2021-05-03 11:10:51','Content....28','Title...28','user28@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (29,'2021-05-03 11:10:51','2021-05-03 11:10:51','Content....29','Title...29','user29@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (30,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....30','Title...30','user30@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (31,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....31','Title...31','user31@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (32,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....32','Title...32','user32@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (33,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....33','Title...33','user33@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (34,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....34','Title...34','user34@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (35,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....35','Title...35','user35@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (36,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....36','Title...36','user36@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (37,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....37','Title...37','user37@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (38,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....38','Title...38','user38@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (39,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....39','Title...39','user39@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (40,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....40','Title...40','user40@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (41,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....41','Title...41','user41@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (42,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....42','Title...42','user42@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (43,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....43','Title...43','user43@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (44,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....44','Title...44','user44@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (45,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....45','Title...45','user45@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (46,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....46','Title...46','user46@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (47,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....47','Title...47','user47@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (48,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....48','Title...48','user48@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (49,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....49','Title...49','user49@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (50,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....50','Title...50','user50@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (51,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....51','Title...51','user51@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (52,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....52','Title...52','user52@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (53,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....53','Title...53','user53@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (54,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....54','Title...54','user54@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (55,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....55','Title...55','user55@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (56,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....56','Title...56','user56@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (57,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....57','Title...57','user57@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (58,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....58','Title...58','user58@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (59,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....59','Title...59','user59@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (60,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....60','Title...60','user60@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (61,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....61','Title...61','user61@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (62,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....62','Title...62','user62@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (63,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....63','Title...63','user63@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (64,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....64','Title...64','user64@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (65,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....65','Title...65','user65@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (66,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....66','Title...66','user66@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (67,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....67','Title...67','user67@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (68,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....68','Title...68','user68@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (69,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....69','Title...69','user69@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (70,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....70','Title...70','user70@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (71,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....71','Title...71','user71@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (72,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....72','Title...72','user72@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (73,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....73','Title...73','user73@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (74,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....74','Title...74','user74@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (75,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....75','Title...75','user75@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (76,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....76','Title...76','user76@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (77,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....77','Title...77','user77@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (78,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....78','Title...78','user78@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (79,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....79','Title...79','user79@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (80,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....80','Title...80','user80@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (81,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....81','Title...81','user81@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (82,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....82','Title...82','user82@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (83,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....83','Title...83','user83@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (84,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....84','Title...84','user84@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (85,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....85','Title...85','user85@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (86,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....86','Title...86','user86@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (87,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....87','Title...87','user87@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (88,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....88','Title...88','user88@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (89,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....89','Title...89','user89@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (90,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....90','Title...90','user90@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (91,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....91','Title...91','user91@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (92,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....92','Title...92','user92@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (93,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....93','Title...93','user93@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (94,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....94','Title...94','user94@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (95,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....95','Title...95','user95@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (96,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....96','Title...96','user96@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (97,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....97','Title...97','user97@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (98,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....98','Title...98','user98@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (99,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....99','Title...99','user99@aaa.com');
insert into `boot_tbl_board`(`bno`,`moddate`,`regdate`,`content`,`title`,`writer_email`) values (100,'2021-05-03 11:10:52','2021-05-03 11:10:52','Content....100','Title...100','user100@aaa.com');

CREATE TABLE `boot_tbl_member` (
  `email` varchar(255) NOT NULL,
  `moddate` datetime(6) DEFAULT NULL,
  `regdate` datetime(6) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user100@aaa.com','2021-05-03 11:00:50','2021-05-03 11:00:50','USER100','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user10@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER10','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user11@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER11','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user12@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER12','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user13@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER13','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user14@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER14','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user15@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER15','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user16@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER16','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user17@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER17','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user18@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER18','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user19@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER19','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user1@aaa.com','2021-05-03 11:00:48','2021-05-03 11:00:48','USER1','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user20@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER20','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user21@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER21','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user22@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER22','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user23@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER23','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user24@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER24','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user25@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER25','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user26@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER26','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user27@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER27','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user28@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER28','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user29@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER29','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user2@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER2','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user30@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER30','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user31@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER31','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user32@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER32','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user33@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER33','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user34@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER34','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user35@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER35','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user36@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER36','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user37@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER37','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user38@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER38','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user39@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER39','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user3@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER3','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user40@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER40','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user41@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER41','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user42@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER42','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user43@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER43','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user44@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER44','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user45@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER45','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user46@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER46','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user47@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER47','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user48@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER48','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user49@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER49','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user4@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER4','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user50@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER50','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user51@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER51','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user52@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER52','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user53@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER53','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user54@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER54','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user55@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER55','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user56@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER56','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user57@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER57','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user58@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER58','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user59@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER59','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user5@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER5','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user60@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER60','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user61@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER61','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user62@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER62','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user63@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER63','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user64@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER64','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user65@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER65','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user66@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER66','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user67@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER67','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user68@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER68','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user69@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER69','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user6@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER6','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user70@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER70','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user71@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER71','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user72@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER72','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user73@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER73','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user74@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER74','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user75@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER75','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user76@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER76','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user77@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER77','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user78@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER78','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user79@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER79','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user7@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER7','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user80@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER80','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user81@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER81','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user82@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER82','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user83@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER83','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user84@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER84','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user85@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER85','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user86@aaa.com','2021-05-03 11:00:50','2021-05-03 11:00:50','USER86','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user87@aaa.com','2021-05-03 11:00:50','2021-05-03 11:00:50','USER87','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user88@aaa.com','2021-05-03 11:00:50','2021-05-03 11:00:50','USER88','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user89@aaa.com','2021-05-03 11:00:50','2021-05-03 11:00:50','USER89','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user8@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER8','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user90@aaa.com','2021-05-03 11:00:50','2021-05-03 11:00:50','USER90','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user91@aaa.com','2021-05-03 11:00:50','2021-05-03 11:00:50','USER91','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user92@aaa.com','2021-05-03 11:00:50','2021-05-03 11:00:50','USER92','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user93@aaa.com','2021-05-03 11:00:50','2021-05-03 11:00:50','USER93','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user94@aaa.com','2021-05-03 11:00:50','2021-05-03 11:00:50','USER94','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user95@aaa.com','2021-05-03 11:00:50','2021-05-03 11:00:50','USER95','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user96@aaa.com','2021-05-03 11:00:50','2021-05-03 11:00:50','USER96','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user97@aaa.com','2021-05-03 11:00:50','2021-05-03 11:00:50','USER97','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user98@aaa.com','2021-05-03 11:00:50','2021-05-03 11:00:50','USER98','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user99@aaa.com','2021-05-03 11:00:50','2021-05-03 11:00:50','USER99','1111');
insert into `boot_tbl_member`(`email`,`moddate`,`regdate`,`name`,`password`) values ('user9@aaa.com','2021-05-03 11:00:49','2021-05-03 11:00:49','USER9','1111');

CREATE TABLE `boot_tbl_reply` (
  `rno` bigint(20) NOT NULL AUTO_INCREMENT,
  `moddate` datetime(6) DEFAULT NULL,
  `regdate` datetime(6) DEFAULT NULL,
  `replyer` varchar(255) DEFAULT NULL,
  `text` varchar(255) DEFAULT NULL,
  `board_bno` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rno`),
  KEY `FKn8kgn56j8tgtx1h8efckfaphw` (`board_bno`),
  CONSTRAINT `FKn8kgn56j8tgtx1h8efckfaphw` FOREIGN KEY (`board_bno`) REFERENCES `boot_tbl_board` (`bno`)
) ENGINE=InnoDB AUTO_INCREMENT=301 DEFAULT CHARSET=utf8;


insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (1,'2021-05-03 11:12:57','2021-05-03 11:12:57','guest','Reply.......1',52);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (2,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......2',25);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (3,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......3',40);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (4,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......4',5);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (5,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......5',70);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (6,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......6',42);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (7,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......7',84);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (8,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......8',25);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (9,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......9',24);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (10,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......10',73);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (11,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......11',4);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (12,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......12',47);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (13,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......13',19);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (14,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......14',4);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (15,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......15',48);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (16,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......16',47);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (17,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......17',34);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (18,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......18',44);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (19,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......19',82);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (20,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......20',50);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (21,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......21',84);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (22,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......22',55);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (23,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......23',95);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (24,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......24',43);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (25,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......25',94);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (26,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......26',84);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (27,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......27',8);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (28,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......28',38);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (29,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......29',40);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (30,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......30',77);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (31,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......31',90);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (32,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......32',64);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (33,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......33',20);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (34,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......34',95);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (35,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......35',23);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (36,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......36',14);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (37,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......37',8);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (38,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......38',7);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (39,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......39',26);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (40,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......40',39);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (41,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......41',17);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (42,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......42',92);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (43,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......43',41);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (44,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......44',52);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (45,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......45',25);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (46,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......46',13);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (47,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......47',97);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (48,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......48',56);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (49,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......49',62);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (50,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......50',9);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (51,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......51',25);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (52,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......52',6);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (53,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......53',27);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (54,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......54',3);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (55,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......55',70);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (56,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......56',62);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (57,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......57',11);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (58,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......58',72);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (59,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......59',48);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (60,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......60',86);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (61,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......61',50);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (62,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......62',16);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (63,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......63',35);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (64,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......64',19);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (65,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......65',43);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (66,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......66',14);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (67,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......67',31);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (68,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......68',34);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (69,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......69',24);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (70,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......70',41);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (71,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......71',33);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (72,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......72',81);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (73,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......73',29);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (74,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......74',78);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (75,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......75',71);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (76,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......76',100);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (77,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......77',99);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (78,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......78',60);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (79,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......79',98);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (80,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......80',92);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (81,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......81',82);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (82,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......82',31);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (83,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......83',7);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (84,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......84',19);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (85,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......85',33);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (86,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......86',37);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (87,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......87',52);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (88,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......88',20);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (89,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......89',23);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (90,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......90',1);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (91,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......91',69);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (92,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......92',14);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (93,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......93',34);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (94,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......94',70);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (95,'2021-05-03 11:12:58','2021-05-03 11:12:58','guest','Reply.......95',51);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (96,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......96',77);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (97,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......97',77);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (98,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......98',93);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (99,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......99',38);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (100,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......100',93);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (101,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......101',31);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (102,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......102',87);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (103,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......103',66);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (104,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......104',81);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (105,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......105',74);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (106,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......106',63);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (107,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......107',89);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (108,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......108',13);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (109,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......109',25);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (110,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......110',69);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (111,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......111',41);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (112,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......112',76);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (113,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......113',29);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (114,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......114',37);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (115,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......115',1);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (116,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......116',21);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (117,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......117',27);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (118,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......118',4);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (119,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......119',80);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (120,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......120',3);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (121,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......121',21);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (122,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......122',18);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (123,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......123',60);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (124,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......124',3);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (125,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......125',8);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (126,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......126',29);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (127,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......127',33);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (128,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......128',92);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (129,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......129',94);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (130,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......130',18);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (131,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......131',82);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (132,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......132',1);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (133,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......133',96);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (134,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......134',98);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (135,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......135',93);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (136,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......136',20);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (137,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......137',51);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (138,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......138',95);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (139,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......139',44);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (140,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......140',70);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (141,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......141',7);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (142,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......142',89);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (143,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......143',28);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (144,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......144',50);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (145,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......145',19);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (146,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......146',97);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (147,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......147',78);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (148,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......148',96);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (149,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......149',100);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (150,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......150',40);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (151,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......151',9);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (152,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......152',54);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (153,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......153',94);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (154,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......154',37);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (155,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......155',98);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (156,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......156',93);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (157,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......157',76);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (158,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......158',74);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (159,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......159',98);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (160,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......160',78);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (161,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......161',10);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (162,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......162',48);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (163,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......163',36);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (164,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......164',43);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (165,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......165',69);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (166,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......166',54);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (167,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......167',14);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (168,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......168',1);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (169,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......169',76);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (170,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......170',94);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (171,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......171',11);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (172,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......172',60);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (173,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......173',10);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (174,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......174',79);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (175,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......175',1);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (176,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......176',8);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (177,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......177',83);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (178,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......178',40);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (179,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......179',83);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (180,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......180',86);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (181,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......181',53);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (182,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......182',6);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (183,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......183',44);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (184,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......184',60);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (185,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......185',77);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (186,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......186',3);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (187,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......187',35);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (188,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......188',42);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (189,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......189',79);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (190,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......190',66);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (191,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......191',24);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (192,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......192',46);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (193,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......193',42);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (194,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......194',38);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (195,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......195',93);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (196,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......196',100);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (197,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......197',43);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (198,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......198',13);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (199,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......199',5);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (200,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......200',49);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (201,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......201',46);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (202,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......202',27);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (203,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......203',95);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (204,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......204',60);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (205,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......205',22);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (206,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......206',10);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (207,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......207',92);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (208,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......208',50);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (209,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......209',41);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (210,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......210',82);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (211,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......211',41);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (212,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......212',26);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (213,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......213',66);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (214,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......214',51);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (215,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......215',98);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (216,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......216',66);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (217,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......217',23);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (218,'2021-05-03 11:12:59','2021-05-03 11:12:59','guest','Reply.......218',80);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (219,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......219',7);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (220,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......220',21);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (221,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......221',72);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (222,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......222',68);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (223,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......223',16);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (224,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......224',81);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (225,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......225',82);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (226,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......226',81);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (227,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......227',94);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (228,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......228',100);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (229,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......229',36);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (230,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......230',84);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (231,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......231',88);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (232,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......232',36);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (233,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......233',19);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (234,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......234',8);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (235,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......235',52);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (236,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......236',39);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (237,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......237',40);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (238,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......238',95);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (239,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......239',3);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (240,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......240',66);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (241,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......241',88);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (242,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......242',85);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (243,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......243',92);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (244,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......244',3);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (245,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......245',94);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (246,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......246',93);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (247,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......247',78);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (248,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......248',9);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (249,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......249',100);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (250,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......250',63);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (251,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......251',76);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (252,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......252',29);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (253,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......253',25);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (254,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......254',82);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (255,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......255',99);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (256,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......256',31);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (257,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......257',8);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (258,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......258',78);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (259,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......259',64);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (260,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......260',87);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (261,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......261',95);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (262,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......262',26);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (263,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......263',30);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (264,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......264',34);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (265,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......265',31);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (266,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......266',25);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (267,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......267',39);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (268,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......268',86);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (269,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......269',87);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (270,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......270',43);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (271,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......271',7);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (272,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......272',42);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (273,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......273',12);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (274,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......274',40);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (275,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......275',39);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (276,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......276',40);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (277,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......277',86);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (278,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......278',38);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (279,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......279',90);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (280,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......280',86);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (281,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......281',19);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (282,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......282',21);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (283,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......283',88);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (284,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......284',32);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (285,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......285',48);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (286,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......286',20);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (287,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......287',28);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (288,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......288',26);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (289,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......289',7);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (290,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......290',37);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (291,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......291',83);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (292,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......292',18);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (293,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......293',32);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (294,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......294',70);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (295,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......295',61);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (296,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......296',84);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (297,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......297',25);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (298,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......298',94);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (299,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......299',87);
insert into `boot_tbl_reply`(`rno`,`moddate`,`regdate`,`replyer`,`text`,`board_bno`) values (300,'2021-05-03 11:13:00','2021-05-03 11:13:00','guest','Reply.......300',61);

CREATE TABLE `guestbook` (
  `gno` bigint(20) NOT NULL AUTO_INCREMENT,
  `moddate` datetime(6) DEFAULT NULL,
  `regdate` datetime(6) DEFAULT NULL,
  `content` varchar(1500) NOT NULL,
  `title` varchar(100) NOT NULL,
  `writer` varchar(50) NOT NULL,
  PRIMARY KEY (`gno`)
) ENGINE=InnoDB AUTO_INCREMENT=303 DEFAULT CHARSET=utf8;


insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (1,'2021-04-13 21:27:24','2021-04-13 21:27:24','Content...1','Title....1','user1');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (2,'2021-04-13 21:27:24','2021-04-13 21:27:24','Content...2','Title....2','user2');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (3,'2021-04-13 21:27:24','2021-04-13 21:27:24','Content...3','Title....3','user3');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (4,'2021-04-13 21:27:24','2021-04-13 21:27:24','Content...4','Title....4','user4');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (5,'2021-04-13 21:27:24','2021-04-13 21:27:24','Content...5','Title....5','user5');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (6,'2021-04-13 21:27:24','2021-04-13 21:27:24','Content...6','Title....6','user6');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (7,'2021-04-13 21:27:24','2021-04-13 21:27:24','Content...7','Title....7','user7');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (8,'2021-04-13 21:27:24','2021-04-13 21:27:24','Content...8','Title....8','user8');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (9,'2021-04-13 21:27:24','2021-04-13 21:27:24','Content...9','Title....9','user9');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (10,'2021-04-13 21:27:24','2021-04-13 21:27:24','Content...10','Title....10','user0');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (11,'2021-04-13 21:27:24','2021-04-13 21:27:24','Content...11','Title....11','user1');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (12,'2021-04-13 21:27:24','2021-04-13 21:27:24','Content...12','Title....12','user2');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (13,'2021-04-13 21:27:24','2021-04-13 21:27:24','Content...13','Title....13','user3');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (14,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...14','Title....14','user4');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (15,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...15','Title....15','user5');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (16,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...16','Title....16','user6');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (17,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...17','Title....17','user7');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (18,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...18','Title....18','user8');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (19,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...19','Title....19','user9');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (20,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...20','Title....20','user0');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (21,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...21','Title....21','user1');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (22,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...22','Title....22','user2');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (23,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...23','Title....23','user3');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (24,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...24','Title....24','user4');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (25,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...25','Title....25','user5');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (26,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...26','Title....26','user6');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (27,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...27','Title....27','user7');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (28,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...28','Title....28','user8');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (29,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...29','Title....29','user9');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (30,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...30','Title....30','user0');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (31,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...31','Title....31','user1');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (32,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...32','Title....32','user2');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (33,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...33','Title....33','user3');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (34,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...34','Title....34','user4');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (35,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...35','Title....35','user5');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (36,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...36','Title....36','user6');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (37,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...37','Title....37','user7');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (38,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...38','Title....38','user8');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (39,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...39','Title....39','user9');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (40,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...40','Title....40','user0');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (41,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...41','Title....41','user1');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (42,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...42','Title....42','user2');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (43,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...43','Title....43','user3');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (44,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...44','Title....44','user4');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (45,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...45','Title....45','user5');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (46,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...46','Title....46','user6');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (47,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...47','Title....47','user7');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (48,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...48','Title....48','user8');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (49,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...49','Title....49','user9');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (50,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...50','Title....50','user0');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (51,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...51','Title....51','user1');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (52,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...52','Title....52','user2');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (53,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...53','Title....53','user3');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (54,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...54','Title....54','user4');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (55,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...55','Title....55','user5');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (56,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...56','Title....56','user6');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (57,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...57','Title....57','user7');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (58,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...58','Title....58','user8');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (59,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...59','Title....59','user9');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (60,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...60','Title....60','user0');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (61,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...61','Title....61','user1');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (62,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...62','Title....62','user2');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (63,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...63','Title....63','user3');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (64,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...64','Title....64','user4');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (65,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...65','Title....65','user5');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (66,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...66','Title....66','user6');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (67,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...67','Title....67','user7');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (68,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...68','Title....68','user8');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (69,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...69','Title....69','user9');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (70,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...70','Title....70','user0');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (71,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...71','Title....71','user1');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (72,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...72','Title....72','user2');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (73,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...73','Title....73','user3');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (74,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...74','Title....74','user4');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (75,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...75','Title....75','user5');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (76,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...76','Title....76','user6');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (77,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...77','Title....77','user7');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (78,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...78','Title....78','user8');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (79,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...79','Title....79','user9');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (80,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...80','Title....80','user0');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (81,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...81','Title....81','user1');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (82,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...82','Title....82','user2');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (83,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...83','Title....83','user3');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (84,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...84','Title....84','user4');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (85,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...85','Title....85','user5');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (86,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...86','Title....86','user6');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (87,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...87','Title....87','user7');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (88,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...88','Title....88','user8');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (89,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...89','Title....89','user9');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (90,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...90','Title....90','user0');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (91,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...91','Title....91','user1');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (92,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...92','Title....92','user2');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (93,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...93','Title....93','user3');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (94,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...94','Title....94','user4');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (95,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...95','Title....95','user5');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (96,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...96','Title....96','user6');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (97,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...97','Title....97','user7');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (98,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...98','Title....98','user8');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (99,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...99','Title....99','user9');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (100,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...100','Title....100','user0');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (101,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...101','Title....101','user1');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (102,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...102','Title....102','user2');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (103,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...103','Title....103','user3');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (104,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...104','Title....104','user4');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (105,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...105','Title....105','user5');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (106,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...106','Title....106','user6');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (107,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...107','Title....107','user7');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (108,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...108','Title....108','user8');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (109,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...109','Title....109','user9');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (110,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...110','Title....110','user0');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (111,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...111','Title....111','user1');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (112,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...112','Title....112','user2');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (113,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...113','Title....113','user3');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (114,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...114','Title....114','user4');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (115,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...115','Title....115','user5');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (116,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...116','Title....116','user6');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (117,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...117','Title....117','user7');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (118,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...118','Title....118','user8');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (119,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...119','Title....119','user9');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (120,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...120','Title....120','user0');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (121,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...121','Title....121','user1');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (122,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...122','Title....122','user2');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (123,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...123','Title....123','user3');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (124,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...124','Title....124','user4');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (125,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...125','Title....125','user5');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (126,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...126','Title....126','user6');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (127,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...127','Title....127','user7');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (128,'2021-04-13 21:27:25','2021-04-13 21:27:25','Content...128','Title....128','user8');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (129,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...129','Title....129','user9');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (130,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...130','Title....130','user0');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (131,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...131','Title....131','user1');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (132,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...132','Title....132','user2');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (133,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...133','Title....133','user3');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (134,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...134','Title....134','user4');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (135,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...135','Title....135','user5');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (136,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...136','Title....136','user6');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (137,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...137','Title....137','user7');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (138,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...138','Title....138','user8');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (139,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...139','Title....139','user9');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (140,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...140','Title....140','user0');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (141,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...141','Title....141','user1');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (142,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...142','Title....142','user2');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (143,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...143','Title....143','user3');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (144,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...144','Title....144','user4');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (145,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...145','Title....145','user5');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (146,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...146','Title....146','user6');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (147,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...147','Title....147','user7');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (148,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...148','Title....148','user8');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (149,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...149','Title....149','user9');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (150,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...150','Title....150','user0');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (151,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...151','Title....151','user1');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (152,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...152','Title....152','user2');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (153,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...153','Title....153','user3');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (154,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...154','Title....154','user4');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (155,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...155','Title....155','user5');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (156,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...156','Title....156','user6');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (157,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...157','Title....157','user7');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (158,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...158','Title....158','user8');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (159,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...159','Title....159','user9');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (160,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...160','Title....160','user0');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (161,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...161','Title....161','user1');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (162,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...162','Title....162','user2');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (163,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...163','Title....163','user3');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (164,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...164','Title....164','user4');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (165,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...165','Title....165','user5');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (166,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...166','Title....166','user6');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (167,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...167','Title....167','user7');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (168,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...168','Title....168','user8');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (169,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...169','Title....169','user9');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (170,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...170','Title....170','user0');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (171,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...171','Title....171','user1');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (172,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...172','Title....172','user2');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (173,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...173','Title....173','user3');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (174,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...174','Title....174','user4');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (175,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...175','Title....175','user5');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (176,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...176','Title....176','user6');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (177,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...177','Title....177','user7');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (178,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...178','Title....178','user8');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (179,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...179','Title....179','user9');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (180,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...180','Title....180','user0');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (181,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...181','Title....181','user1');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (182,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...182','Title....182','user2');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (183,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...183','Title....183','user3');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (184,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...184','Title....184','user4');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (185,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...185','Title....185','user5');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (186,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...186','Title....186','user6');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (187,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...187','Title....187','user7');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (188,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...188','Title....188','user8');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (189,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...189','Title....189','user9');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (190,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...190','Title....190','user0');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (191,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...191','Title....191','user1');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (192,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...192','Title....192','user2');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (193,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...193','Title....193','user3');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (194,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...194','Title....194','user4');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (195,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...195','Title....195','user5');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (196,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...196','Title....196','user6');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (197,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...197','Title....197','user7');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (198,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...198','Title....198','user8');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (199,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...199','Title....199','user9');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (200,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...200','Title....200','user0');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (201,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...201','Title....201','user1');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (202,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...202','Title....202','user2');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (203,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...203','Title....203','user3');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (204,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...204','Title....204','user4');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (205,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...205','Title....205','user5');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (206,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...206','Title....206','user6');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (207,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...207','Title....207','user7');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (208,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...208','Title....208','user8');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (209,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...209','Title....209','user9');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (210,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...210','Title....210','user0');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (211,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...211','Title....211','user1');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (212,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...212','Title....212','user2');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (213,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...213','Title....213','user3');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (214,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...214','Title....214','user4');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (215,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...215','Title....215','user5');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (216,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...216','Title....216','user6');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (217,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...217','Title....217','user7');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (218,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...218','Title....218','user8');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (219,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...219','Title....219','user9');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (220,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...220','Title....220','user0');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (221,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...221','Title....221','user1');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (222,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...222','Title....222','user2');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (223,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...223','Title....223','user3');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (224,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...224','Title....224','user4');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (225,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...225','Title....225','user5');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (226,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...226','Title....226','user6');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (227,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...227','Title....227','user7');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (228,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...228','Title....228','user8');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (229,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...229','Title....229','user9');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (230,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...230','Title....230','user0');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (231,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...231','Title....231','user1');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (232,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...232','Title....232','user2');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (233,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...233','Title....233','user3');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (234,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...234','Title....234','user4');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (235,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...235','Title....235','user5');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (236,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...236','Title....236','user6');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (237,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...237','Title....237','user7');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (238,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...238','Title....238','user8');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (239,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...239','Title....239','user9');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (240,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...240','Title....240','user0');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (241,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...241','Title....241','user1');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (242,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...242','Title....242','user2');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (243,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...243','Title....243','user3');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (244,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...244','Title....244','user4');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (245,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...245','Title....245','user5');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (246,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...246','Title....246','user6');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (247,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...247','Title....247','user7');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (248,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...248','Title....248','user8');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (249,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...249','Title....249','user9');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (250,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...250','Title....250','user0');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (251,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...251','Title....251','user1');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (252,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...252','Title....252','user2');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (253,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...253','Title....253','user3');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (254,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...254','Title....254','user4');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (255,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...255','Title....255','user5');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (256,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...256','Title....256','user6');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (257,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...257','Title....257','user7');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (258,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...258','Title....258','user8');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (259,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...259','Title....259','user9');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (260,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...260','Title....260','user0');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (261,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...261','Title....261','user1');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (262,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...262','Title....262','user2');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (263,'2021-04-13 21:27:26','2021-04-13 21:27:26','Content...263','Title....263','user3');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (264,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...264','Title....264','user4');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (265,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...265','Title....265','user5');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (266,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...266','Title....266','user6');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (267,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...267','Title....267','user7');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (268,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...268','Title....268','user8');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (269,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...269','Title....269','user9');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (270,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...270','Title....270','user0');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (271,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...271','Title....271','user1');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (272,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...272','Title....272','user2');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (273,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...273','Title....273','user3');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (274,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...274','Title....274','user4');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (275,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...275','Title....275','user5');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (276,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...276','Title....276','user6');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (277,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...277','Title....277','user7');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (278,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...278','Title....278','user8');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (279,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...279','Title....279','user9');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (280,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...280','Title....280','user0');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (281,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...281','Title....281','user1');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (282,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...282','Title....282','user2');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (283,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...283','Title....283','user3');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (284,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...284','Title....284','user4');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (285,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...285','Title....285','user5');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (286,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...286','Title....286','user6');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (287,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...287','Title....287','user7');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (288,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...288','Title....288','user8');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (289,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...289','Title....289','user9');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (290,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...290','Title....290','user0');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (291,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...291','Title....291','user1');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (292,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...292','Title....292','user2');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (293,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...293','Title....293','user3');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (294,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...294','Title....294','user4');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (295,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...295','Title....295','user5');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (296,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...296','Title....296','user6');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (297,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...297','Title....297','user7');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (298,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...298','Title....298','user8');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (299,'2021-04-13 21:27:27','2021-04-13 21:27:27','Content...299','Title....299','user9');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (300,'2021-04-13 22:34:20','2021-04-13 21:27:27','Changed Content...','Changed Title....','user0');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (301,'2021-04-20 20:41:08','2021-04-17 10:47:03','Sample Content...2','Sample Title...','user0');
insert into `guestbook`(`gno`,`moddate`,`regdate`,`content`,`title`,`writer`) values (302,'2021-04-20 20:29:43','2021-04-20 20:29:43','content','guestbook1','源?쒖슦');

CREATE TABLE `ot_com_auth` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `USER_ID` varchar(30) DEFAULT NULL,
  `authority` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;


insert into `ot_com_auth`(`id`,`USER_ID`,`authority`) values (1,'orktw@naver.com','ROLE_USER');
insert into `ot_com_auth`(`id`,`USER_ID`,`authority`) values (2,'admin','ROLE_ADMIN');

CREATE TABLE `ot_com_user` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `LAST_NAME` varchar(50) DEFAULT NULL,
  `FIRST_NAME` varchar(50) DEFAULT NULL,
  `USER_ID` varchar(30) DEFAULT NULL,
  `SEX` varchar(10) DEFAULT NULL,
  `CITY` varchar(30) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

insert into `ot_com_user`(`id`,`LAST_NAME`,`FIRST_NAME`,`USER_ID`,`SEX`,`CITY`,`password`) values (4,'admin','admin','admin','Male','경기도','1234');
insert into `ot_com_user`(`id`,`LAST_NAME`,`FIRST_NAME`,`USER_ID`,`SEX`,`CITY`,`password`) values (5,'김','태우','orktw@naver.com','Male','경기도','1234');

CREATE TABLE `persistent_logins` (
  `username` varchar(64) NOT NULL,
  `series` varchar(64) NOT NULL,
  `token` varchar(64) NOT NULL,
  `last_used` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`series`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `reply` (
  `rno` bigint(20) NOT NULL AUTO_INCREMENT,
  `moddate` datetime(6) DEFAULT NULL,
  `regdate` datetime(6) DEFAULT NULL,
  `replyer` varchar(255) DEFAULT NULL,
  `text` varchar(255) DEFAULT NULL,
  `board_bno` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rno`),
  KEY `FKr1bmblqir7dalmh47ngwo7mcs` (`board_bno`),
  CONSTRAINT `FKr1bmblqir7dalmh47ngwo7mcs` FOREIGN KEY (`board_bno`) REFERENCES `board` (`bno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `seq_mysql` (
  `id` int(11) NOT NULL,
  `seq_name` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


insert into `seq_mysql`(`id`,`seq_name`) values (2,'boardSeq');
insert into `seq_mysql`(`id`,`seq_name`) values (7,'ST_USER_INFO');
insert into `seq_mysql`(`id`,`seq_name`) values (118,'ST_USER_INFO_ITEM');
insert into `seq_mysql`(`id`,`seq_name`) values (14,'ST_BOARD');
insert into `seq_mysql`(`id`,`seq_name`) values (13,'ST_BOARD_REPLY');
insert into `seq_mysql`(`id`,`seq_name`) values (2,'ST_COM_DD');
insert into `seq_mysql`(`id`,`seq_name`) values (7,'ST_COM_DD_VALUE');
insert into `seq_mysql`(`id`,`seq_name`) values (5,'ST_COM_DD_VALUE');
insert into `seq_mysql`(`id`,`seq_name`) values (1,'ST_COMMON_TABLE_MASTER');
insert into `seq_mysql`(`id`,`seq_name`) values (13,'ST_COMMON_TABLE_COLUMN');
insert into `seq_mysql`(`id`,`seq_name`) values (16,'TBL_BOARD');
insert into `seq_mysql`(`id`,`seq_name`) values (16,'TBL_REPLY');
insert into `seq_mysql`(`id`,`seq_name`) values (1,'ST_SCHEDULE');
insert into `seq_mysql`(`id`,`seq_name`) values (1974,'ST_STOCK_MASTER');

CREATE TABLE `st_board` (
  `ID` bigint(20) unsigned NOT NULL COMMENT 'ID',
  `CREATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
  `CREATE_USER` varchar(50) DEFAULT NULL COMMENT '생성자',
  `UPDATE_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '수정일',
  `UPDATE_USER` varchar(50) DEFAULT NULL COMMENT '수정자',
  `DELETE_YN` varchar(1) DEFAULT 'N' COMMENT '삭제여부',
  `CATEGORY_ID` varchar(20) DEFAULT NULL COMMENT '게시글 카테고리',
  `TITLE` varchar(200) NOT NULL COMMENT '제목',
  `content` text NOT NULL COMMENT '게시글',
  `TAG` varchar(1000) DEFAULT NULL COMMENT '태그',
  `view_cnt` int(11) NOT NULL DEFAULT '0' COMMENT '카운트',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='게시판 테이블';


insert into `st_board`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`CATEGORY_ID`,`TITLE`,`content`,`TAG`,`view_cnt`) values (3,'2020-08-24 19:53:30','4','2020-08-29 23:49:10','3','N',null,'게시판1','김태우','태그',21);
insert into `st_board`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`CATEGORY_ID`,`TITLE`,`content`,`TAG`,`view_cnt`) values (4,'2020-08-24 20:14:32','3','2020-08-25 15:02:15','3','N',null,'강공','내용','태그',45);
insert into `st_board`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`CATEGORY_ID`,`TITLE`,`content`,`TAG`,`view_cnt`) values (5,'2020-08-29 23:34:08','4','2021-05-30 16:45:21','5','N',null,'제목','내용','',11);
insert into `st_board`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`CATEGORY_ID`,`TITLE`,`content`,`TAG`,`view_cnt`) values (6,'2020-08-29 23:35:37','6','2020-08-29 23:52:42','6','N',null,'게시판','내용2','',4);
insert into `st_board`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`CATEGORY_ID`,`TITLE`,`content`,`TAG`,`view_cnt`) values (7,'2020-08-29 23:37:49','7','2020-08-29 23:50:53','7','N',null,'사이코지만 괜찮아','고문영 작가 문상태 작가','',1);
insert into `st_board`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`CATEGORY_ID`,`TITLE`,`content`,`TAG`,`view_cnt`) values (8,'2020-08-29 23:38:49','4','2021-03-01 09:42:52','6','N',null,'최종회','<p>마지막회2</p><p>마지막회 라고요</p>','',11);
insert into `st_board`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`CATEGORY_ID`,`TITLE`,`content`,`TAG`,`view_cnt`) values (9,'2021-05-29 10:50:14','3','2021-05-29 10:50:14','4','N',null,'게시판 등록','<p>등록합니다</p>','태그 태그',0);
insert into `st_board`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`CATEGORY_ID`,`TITLE`,`content`,`TAG`,`view_cnt`) values (10,'2021-05-29 10:52:20','4','2021-05-30 16:43:01','10','N',null,'게시판 등록2','<p>등록합니데이</p>','',3);
insert into `st_board`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`CATEGORY_ID`,`TITLE`,`content`,`TAG`,`view_cnt`) values (11,'2021-05-29 10:53:24','6','2021-05-30 16:56:07','11','Y',null,'게시판 생성','<p>게시판 생성</p>','',2);
insert into `st_board`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`CATEGORY_ID`,`TITLE`,`content`,`TAG`,`view_cnt`) values (12,'2021-05-29 10:56:32','4','2021-05-30 16:54:54','12','Y',null,'게시판 등록2','<p>게시판 등록2</p>','',1);
insert into `st_board`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`CATEGORY_ID`,`TITLE`,`content`,`TAG`,`view_cnt`) values (13,'2021-05-29 10:59:25','4','2021-05-30 16:20:14','4','N',null,'게시판 등록3','<p>게시판 등록3</p>','태그',4);
insert into `st_board`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`CATEGORY_ID`,`TITLE`,`content`,`TAG`,`view_cnt`) values (14,'2021-05-29 11:01:35','4','2021-05-30 16:20:46','4','N',null,'게시판 등록4-1','<p>게시판 등록4</p>','태그',10);

CREATE TABLE `st_board_reply` (
  `ID` bigint(20) unsigned NOT NULL COMMENT 'ID',
  `CREATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
  `CREATE_USER` varchar(50) DEFAULT NULL COMMENT '생성자',
  `UPDATE_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '수정일',
  `UPDATE_USER` varchar(50) DEFAULT NULL COMMENT '수정자',
  `DELETE_YN` varchar(1) DEFAULT 'N' COMMENT '삭제여부',
  `BOARD_ID` bigint(20) unsigned NOT NULL COMMENT '게시물 일련번호',
  `content` text COMMENT '댓글내용',
  PRIMARY KEY (`ID`),
  KEY `st_board_reply_st_board_id_fk` (`BOARD_ID`),
  CONSTRAINT `st_board_reply_st_board_id_fk` FOREIGN KEY (`BOARD_ID`) REFERENCES `st_board` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='댓글 테이블';


insert into `st_board_reply`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`BOARD_ID`,`content`) values (4,'2020-08-29 13:35:10','김태우','2020-08-29 13:35:10','김태우','N',4,'댓글1');
insert into `st_board_reply`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`BOARD_ID`,`content`) values (5,'2020-08-29 13:35:19','김예린','2020-08-29 13:35:25',null,'N',4,'댓글3');
insert into `st_board_reply`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`BOARD_ID`,`content`) values (6,'2020-08-29 23:53:48','김태우','2020-08-29 23:53:48','김태우','N',6,'댓글');
insert into `st_board_reply`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`BOARD_ID`,`content`) values (7,'2020-08-29 23:54:02','김예나','2020-08-29 23:54:11',null,'Y',6,'언제 자냐?2');
insert into `st_board_reply`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`BOARD_ID`,`content`) values (8,'2020-08-29 23:54:21','김태우','2020-08-29 23:54:21','김태우','N',6,'뭐라카노');
insert into `st_board_reply`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`BOARD_ID`,`content`) values (9,'2021-03-01 09:42:40','작성자','2021-03-01 09:42:40','작성자','N',8,'댓글');
insert into `st_board_reply`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`BOARD_ID`,`content`) values (10,'2021-05-30 16:40:59','김태우','2021-05-30 16:41:07','4','N',14,'댓글 입력2');
insert into `st_board_reply`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`BOARD_ID`,`content`) values (11,'2021-05-30 16:42:28','김태우','2021-05-30 16:42:28','4','N',14,'댓글을 더 입력하자');
insert into `st_board_reply`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`BOARD_ID`,`content`) values (12,'2021-05-30 16:42:35','김태우','2021-05-30 16:42:40','4','Y',14,'댓들 한 번더');
insert into `st_board_reply`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`BOARD_ID`,`content`) values (13,'2021-05-30 16:56:48','김태우','2021-05-30 16:56:48','4','N',13,'댓글 ');

CREATE TABLE `st_com_board` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `PARENT_ID` bigint(20) DEFAULT NULL,
  `TITLE` varchar(100) NOT NULL,
  `CONTENTS` varchar(4000) NOT NULL,
  `HIT_CNT` bigint(20) NOT NULL,
  `DELETE_YN` varchar(1) NOT NULL DEFAULT 'N',
  `CREATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `CREATE_USER` varchar(30) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;


insert into `st_com_board`(`id`,`PARENT_ID`,`TITLE`,`CONTENTS`,`HIT_CNT`,`DELETE_YN`,`CREATE_DATE`,`CREATE_USER`) values (3,null,'제목','내용',0,'N','2020-08-04 21:34:15','Admin');


CREATE TABLE `st_com_dd` (
  `ID` bigint(20) unsigned NOT NULL COMMENT 'ID',
  `CREATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
  `CREATE_USER` varchar(50) DEFAULT NULL COMMENT '생성자',
  `UPDATE_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '수정일',
  `UPDATE_USER` varchar(50) DEFAULT NULL COMMENT '수정자',
  `DELETE_YN` varchar(1) DEFAULT 'N' COMMENT '삭제여부',
  `DD_CODE` varchar(50) NOT NULL COMMENT 'DD CODE',
  `DD_NAME` varchar(50) NOT NULL COMMENT 'DD명',
  `DD_DESC` varchar(100) DEFAULT NULL COMMENT 'DD설명',
  `DD_MODULE` varchar(30) DEFAULT NULL COMMENT 'DD 모듈',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='DD MASTER';


insert into `st_com_dd`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`DD_CODE`,`DD_NAME`,`DD_DESC`,`DD_MODULE`) values (1,'2020-09-05 10:16:18',null,'2020-09-05 10:16:18',null,'N','TEST_DD','DD테스트','','TEST');
insert into `st_com_dd`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`DD_CODE`,`DD_NAME`,`DD_DESC`,`DD_MODULE`) values (2,'2020-09-05 10:30:49',null,'2020-09-05 11:38:52',null,'N','TEST_DD','DD테스트','테스트 설명','TEST');

CREATE TABLE `st_com_dd_value` (
  `ID` bigint(20) unsigned NOT NULL COMMENT 'ID',
  `CREATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
  `CREATE_USER` varchar(50) DEFAULT NULL COMMENT '생성자',
  `UPDATE_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '수정일',
  `UPDATE_USER` varchar(50) DEFAULT NULL COMMENT '수정자',
  `DELETE_YN` varchar(1) DEFAULT 'N' COMMENT '삭제여부',
  `DD_ID` bigint(20) unsigned NOT NULL COMMENT 'DD ID',
  `DD_VAL` varchar(30) NOT NULL COMMENT 'DD VALUE',
  `DD_KO` varchar(50) DEFAULT NULL COMMENT 'DD LABEL KO',
  `DD_EN` varchar(50) DEFAULT NULL COMMENT 'DD LABEL EN',
  `DD_DEFAULT` varchar(1) DEFAULT 'N' COMMENT 'Default 여부',
  `USE_YN` varchar(1) DEFAULT 'Y' COMMENT '사용여부',
  `DD_ORDER` bigint(10) DEFAULT NULL COMMENT 'DD 순서',
  `DD_FILTER` varchar(50) DEFAULT NULL COMMENT 'DD FILTER',
  PRIMARY KEY (`ID`),
  KEY `st_com_dd_value_st_com_dd_id_fk` (`DD_ID`),
  CONSTRAINT `st_com_dd_value_st_com_dd_id_fk` FOREIGN KEY (`DD_ID`) REFERENCES `st_com_dd` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='DD VALUE';


insert into `st_com_dd_value`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`DD_ID`,`DD_VAL`,`DD_KO`,`DD_EN`,`DD_DEFAULT`,`USE_YN`,`DD_ORDER`,`DD_FILTER`) values (4,'2020-09-05 10:50:41',null,'2020-09-05 11:38:52',null,'N',2,'1','테스트','TEST','Y','N',3,'FILTER');
insert into `st_com_dd_value`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`DD_ID`,`DD_VAL`,`DD_KO`,`DD_EN`,`DD_DEFAULT`,`USE_YN`,`DD_ORDER`,`DD_FILTER`) values (5,'2020-09-05 11:30:12',null,'2020-09-05 11:38:52',null,'N',2,'2','테스트2','TEST2','N','Y',1,'FILTER2');


CREATE TABLE `st_com_message` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `CREATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
  `CREATE_USER` varchar(50) DEFAULT NULL COMMENT '생성자',
  `UPDATE_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '수정일',
  `UPDATE_USER` varchar(50) DEFAULT NULL COMMENT '수정자',
  `DELETE_YN` varchar(1) DEFAULT 'N' COMMENT '삭제여부',
  `MESSAGE_CODE` varchar(100) DEFAULT NULL COMMENT '메시지코드',
  `LABLE_KO` varchar(100) DEFAULT NULL COMMENT '메시지_한글',
  `LABLE_EN` varchar(100) DEFAULT NULL COMMENT '메시지_영문',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COMMENT='리소브번들 메시지 테이블';


insert into `st_com_message`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`MESSAGE_CODE`,`LABLE_KO`,`LABLE_EN`) values (6,'2020-08-23 16:13:42','ADMIN','2020-08-23 16:13:42','ADMIN','N','LABEL.APPNAME','강공','KANGONG');

CREATE TABLE `st_common_table_column` (
  `ID` bigint(20) unsigned NOT NULL COMMENT 'ID',
  `CREATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
  `CREATE_USER` varchar(50) DEFAULT NULL COMMENT '생성자',
  `UPDATE_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '수정일',
  `UPDATE_USER` varchar(50) DEFAULT NULL COMMENT '수정자',
  `DELETE_YN` varchar(1) DEFAULT 'N' COMMENT '삭제여부',
  `MASTER_ID` bigint(20) unsigned NOT NULL COMMENT 'COMMON MASTER ID',
  `COLUMN_CODE` varchar(50) NOT NULL COMMENT 'COLUMN CODE',
  `COLUMN_COMMENT` varchar(50) NOT NULL COMMENT 'COLUMN COMMENT',
  `COLUMN_TYPE` varchar(50) DEFAULT NULL COMMENT 'COLUMN TYPE',
  `INPUT_TYPE` varchar(50) DEFAULT NULL COMMENT 'INPUT TYPE',
  `COLUMN_SIZE` varchar(30) DEFAULT NULL COMMENT 'COLUMN SIZE',
  `COLUMN_MAX` varchar(30) DEFAULT NULL COMMENT 'COLUMN SIZE MAX',
  `COLUMN_PATTERN` varchar(50) DEFAULT NULL COMMENT 'COLUMN 패턴',
  `COLUMN_ORDER` varchar(30) DEFAULT NULL COMMENT 'COLUMN 순서',
  PRIMARY KEY (`ID`),
  KEY `st_common_table_column_st_common_table_master_id_fk` (`MASTER_ID`),
  CONSTRAINT `st_common_table_column_st_common_table_master_id_fk` FOREIGN KEY (`MASTER_ID`) REFERENCES `st_common_table_master` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='TABLE 공용테이블 COLUMN';

insert into `st_common_table_column`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`MASTER_ID`,`COLUMN_CODE`,`COLUMN_COMMENT`,`COLUMN_TYPE`,`INPUT_TYPE`,`COLUMN_SIZE`,`COLUMN_MAX`,`COLUMN_PATTERN`,`COLUMN_ORDER`) values (4,'2020-09-06 20:20:33',null,'2020-09-06 20:20:33',null,'N',1,'USER_ID','사용자ID','varchar(50)','text','','50','','106');
insert into `st_common_table_column`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`MASTER_ID`,`COLUMN_CODE`,`COLUMN_COMMENT`,`COLUMN_TYPE`,`INPUT_TYPE`,`COLUMN_SIZE`,`COLUMN_MAX`,`COLUMN_PATTERN`,`COLUMN_ORDER`) values (5,'2020-09-06 20:20:33',null,'2020-09-06 20:20:33',null,'N',1,'USER_NAME','사용자명','varchar(50)','text','','50','','107');
insert into `st_common_table_column`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`MASTER_ID`,`COLUMN_CODE`,`COLUMN_COMMENT`,`COLUMN_TYPE`,`INPUT_TYPE`,`COLUMN_SIZE`,`COLUMN_MAX`,`COLUMN_PATTERN`,`COLUMN_ORDER`) values (6,'2020-09-06 20:20:33',null,'2020-09-06 20:20:33',null,'N',1,'PASSWORD','패스워드','varchar(50)','text','','50','','108');
insert into `st_common_table_column`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`MASTER_ID`,`COLUMN_CODE`,`COLUMN_COMMENT`,`COLUMN_TYPE`,`INPUT_TYPE`,`COLUMN_SIZE`,`COLUMN_MAX`,`COLUMN_PATTERN`,`COLUMN_ORDER`) values (7,'2020-09-06 20:20:33',null,'2020-09-06 20:20:33',null,'N',1,'AGE','나이','int(3)','text','','','','109');
insert into `st_common_table_column`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`MASTER_ID`,`COLUMN_CODE`,`COLUMN_COMMENT`,`COLUMN_TYPE`,`INPUT_TYPE`,`COLUMN_SIZE`,`COLUMN_MAX`,`COLUMN_PATTERN`,`COLUMN_ORDER`) values (8,'2020-09-06 20:20:33',null,'2020-09-06 20:20:33',null,'N',1,'GENDER','성별','varchar(10)','text','','10','','110');
insert into `st_common_table_column`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`MASTER_ID`,`COLUMN_CODE`,`COLUMN_COMMENT`,`COLUMN_TYPE`,`INPUT_TYPE`,`COLUMN_SIZE`,`COLUMN_MAX`,`COLUMN_PATTERN`,`COLUMN_ORDER`) values (9,'2020-09-06 20:20:34',null,'2020-09-06 20:20:34',null,'N',1,'BIRTH_DATE','생일','date','date','','','','111');
insert into `st_common_table_column`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`MASTER_ID`,`COLUMN_CODE`,`COLUMN_COMMENT`,`COLUMN_TYPE`,`INPUT_TYPE`,`COLUMN_SIZE`,`COLUMN_MAX`,`COLUMN_PATTERN`,`COLUMN_ORDER`) values (10,'2020-09-06 20:20:34',null,'2020-09-06 20:20:34',null,'N',1,'CITY','도시','varchar(30)','text','','30','','112');
insert into `st_common_table_column`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`MASTER_ID`,`COLUMN_CODE`,`COLUMN_COMMENT`,`COLUMN_TYPE`,`INPUT_TYPE`,`COLUMN_SIZE`,`COLUMN_MAX`,`COLUMN_PATTERN`,`COLUMN_ORDER`) values (11,'2020-09-06 20:20:34',null,'2020-09-06 20:20:34',null,'N',1,'HOBBY','취미','varchar(60)','text','','60','','113');
insert into `st_common_table_column`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`MASTER_ID`,`COLUMN_CODE`,`COLUMN_COMMENT`,`COLUMN_TYPE`,`INPUT_TYPE`,`COLUMN_SIZE`,`COLUMN_MAX`,`COLUMN_PATTERN`,`COLUMN_ORDER`) values (12,'2020-09-06 20:20:34',null,'2020-09-06 20:20:34',null,'N',1,'COMMENT','설명','varchar(300)','text','','300','','114');
insert into `st_common_table_column`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`MASTER_ID`,`COLUMN_CODE`,`COLUMN_COMMENT`,`COLUMN_TYPE`,`INPUT_TYPE`,`COLUMN_SIZE`,`COLUMN_MAX`,`COLUMN_PATTERN`,`COLUMN_ORDER`) values (13,'2020-09-06 20:20:34',null,'2020-09-06 20:20:34',null,'N',1,'DELETE_YN','삭제여부','varchar(1)','text','','1','','115');

CREATE TABLE `st_common_table_master` (
  `ID` bigint(20) unsigned NOT NULL COMMENT 'ID',
  `CREATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
  `CREATE_USER` varchar(50) DEFAULT NULL COMMENT '생성자',
  `UPDATE_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '수정일',
  `UPDATE_USER` varchar(50) DEFAULT NULL COMMENT '수정자',
  `DELETE_YN` varchar(1) DEFAULT 'N' COMMENT '삭제여부',
  `TABLE_CODE` varchar(50) NOT NULL COMMENT 'TABLE CODE',
  `TABLE_NAME` varchar(50) NOT NULL COMMENT 'TABLE명',
  `TABLE_DESC` varchar(100) DEFAULT NULL COMMENT 'TABLE설명',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='TABLE 공용테이블 MASTER';

insert into `st_common_table_master`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`TABLE_CODE`,`TABLE_NAME`,`TABLE_DESC`) values (1,'2020-09-06 20:20:33',null,'2021-03-01 09:53:00',null,'N','ST_USER_INFO','사용자정보','사용자정보');

CREATE TABLE `st_schedule` (
  `ID` bigint(20) unsigned NOT NULL COMMENT 'ID',
  `CREATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
  `CREATE_USER` varchar(50) DEFAULT NULL COMMENT '생성자',
  `UPDATE_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '수정일',
  `UPDATE_USER` varchar(50) DEFAULT NULL COMMENT '수정자',
  `DELETE_YN` varchar(1) DEFAULT 'N' COMMENT '삭제여부',
  `CATEGORY` varchar(50) NOT NULL COMMENT 'CATEGORY',
  `TITLE` varchar(50) NOT NULL COMMENT '제목',
  `START_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '시작일',
  `END_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '종료일',
  `COMMENTS` varchar(100) DEFAULT NULL COMMENT '일정_설명',
  `CALENDAR_ID` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='일정 테이블';


insert into `st_schedule`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`CATEGORY`,`TITLE`,`START_DATE`,`END_DATE`,`COMMENTS`,`CALENDAR_ID`) values (0,'2021-06-16 22:47:21','4','2021-06-16 22:47:21','4','N','time','강공','2021-06-16 22:47:00','2021-06-18 22:47:00','','1');
insert into `st_schedule`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`CATEGORY`,`TITLE`,`START_DATE`,`END_DATE`,`COMMENTS`,`CALENDAR_ID`) values (1,'2021-06-16 22:50:24','4','2021-06-26 21:37:34','4','N','time','강공2','2021-06-16 04:47:00','2021-06-16 04:47:00','','3');

CREATE TABLE `st_stock_master` (
  `ID` bigint(20) unsigned NOT NULL COMMENT 'ID',
  `CREATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
  `UPDATE_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '수정일',
  `DELETE_YN` varchar(1) DEFAULT 'N' COMMENT '삭제여부',
  `STOCK_ID` varchar(20) NOT NULL COMMENT '종목_ID',
  `NAME` varchar(50) DEFAULT NULL COMMENT '종목명',
  `PRICE` bigint(20) unsigned DEFAULT NULL COMMENT '현재가',
  `PRICE_BEFOREDAY` bigint(20) unsigned DEFAULT NULL COMMENT '전일가',
  `FACE_VALUE` bigint(10) unsigned DEFAULT NULL COMMENT '액면가',
  `MARKET_CAPITALIZATION` bigint(20) unsigned DEFAULT NULL COMMENT '시가총액',
  `STOCK_QTY` bigint(20) unsigned DEFAULT NULL COMMENT '상장주식수',
  `FOREIGNER_RATIO` bigint(10) unsigned DEFAULT NULL COMMENT '외국인비율',
  `VOLUMN` bigint(20) unsigned DEFAULT NULL COMMENT '거래량',
  `PER` bigint(10) DEFAULT NULL COMMENT 'PER',
  `ESTIMATION_PER` bigint(10) DEFAULT NULL COMMENT '추정PER',
  `ROE` bigint(10) DEFAULT NULL COMMENT 'ROE',
  `PBR` bigint(10) DEFAULT NULL COMMENT 'PBR',
  `BPS` bigint(10) DEFAULT NULL COMMENT 'BPS',
  `INDUSTRY_PER` bigint(10) DEFAULT NULL COMMENT '동일업종 PER',
  `INDUSTRY_BAISSE` bigint(10) DEFAULT NULL COMMENT '동일업종 등락률',
  `INVESTMENT_OPINION` varchar(10) DEFAULT NULL COMMENT '투자의견',
  `TARGET_PRICE` bigint(20) unsigned DEFAULT NULL COMMENT '목표주가',
  `MAX_52` bigint(20) unsigned DEFAULT NULL COMMENT '52주 최고',
  `MIN_52` bigint(20) unsigned DEFAULT NULL COMMENT '52주 최저',
  `DIVIDEND_RATE` bigint(20) DEFAULT NULL COMMENT '배당수익률',
  `NATIONAL` varchar(20) DEFAULT NULL COMMENT '종목 국가',
  PRIMARY KEY (`STOCK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='주식 마스터 테이블';

CREATE TABLE `st_user_authorities` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `CREATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
  `CREATE_USER` varchar(50) DEFAULT NULL COMMENT '생성자',
  `UPDATE_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '수정일',
  `UPDATE_USER` varchar(50) DEFAULT NULL COMMENT '수정자',
  `DELETE_YN` varchar(1) DEFAULT 'N' COMMENT '삭제여부',
  `USER_INFO_ID` bigint(20) unsigned NOT NULL COMMENT '사용자정보 ID',
  `authority` varchar(50) DEFAULT NULL COMMENT '권한',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COMMENT='사용자 권한 테이블';


insert into `st_user_authorities`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`USER_INFO_ID`,`authority`) values (4,'2021-05-19 21:25:44',null,null,null,'N',4,'ROLE_ADMIN');
insert into `st_user_authorities`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`USER_INFO_ID`,`authority`) values (7,'2021-05-19 21:42:52',null,null,null,'N',4,'ROLE_MEMBER');

CREATE TABLE `st_user_info` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `CREATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
  `CREATE_USER` varchar(50) DEFAULT NULL COMMENT '생성자',
  `UPDATE_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '수정일',
  `UPDATE_USER` varchar(50) DEFAULT NULL COMMENT '수정자',
  `USER_ID` varchar(50) DEFAULT NULL COMMENT '사용자ID',
  `USER_NAME` varchar(50) DEFAULT NULL COMMENT '사용자명',
  `PASSWORD` varchar(50) DEFAULT NULL COMMENT '패스워드',
  `AGE` int(3) DEFAULT NULL COMMENT '나이',
  `GENDER` varchar(10) DEFAULT NULL COMMENT '성별',
  `BIRTH_DATE` date DEFAULT NULL COMMENT '생일',
  `CITY` varchar(30) DEFAULT NULL COMMENT '도시',
  `HOBBY` varchar(60) DEFAULT NULL COMMENT '취미',
  `COMMENT` varchar(300) DEFAULT NULL COMMENT '설명',
  `DELETE_YN` varchar(1) DEFAULT 'N' COMMENT '삭제여부',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `st_user_info_IDX1` (`USER_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COMMENT='사용자 테이블';

insert into `st_user_info`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`USER_ID`,`USER_NAME`,`PASSWORD`,`AGE`,`GENDER`,`BIRTH_DATE`,`CITY`,`HOBBY`,`COMMENT`,`DELETE_YN`) values (3,'2020-08-22 21:39:16','admin','2020-08-22 21:39:16','admin','abcd1@naver.com','김예린','123456789a',15,'female','2020-08-01 00:00:00','seoul','book,music,game','안녕하세요','N');
insert into `st_user_info`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`USER_ID`,`USER_NAME`,`PASSWORD`,`AGE`,`GENDER`,`BIRTH_DATE`,`CITY`,`HOBBY`,`COMMENT`,`DELETE_YN`) values (4,'2020-08-16 11:11:06','admin','2020-08-16 11:11:06','admin','orktw@naver.com','김태우','1234',45,'','1973-12-01 00:00:00','suwon','music,game','안녕하세요','N');
insert into `st_user_info`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`USER_ID`,`USER_NAME`,`PASSWORD`,`AGE`,`GENDER`,`BIRTH_DATE`,`CITY`,`HOBBY`,`COMMENT`,`DELETE_YN`) values (6,'2021-05-25 22:05:52','admin','2021-05-25 22:05:52','admin','ktw0001@abc.com','김태웅','1234',null,null,null,null,null,null,'N');
insert into `st_user_info`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`USER_ID`,`USER_NAME`,`PASSWORD`,`AGE`,`GENDER`,`BIRTH_DATE`,`CITY`,`HOBBY`,`COMMENT`,`DELETE_YN`) values (7,'2021-05-25 22:07:25','admin','2021-05-25 22:07:25','admin','ktw0002@abc.com','김태호','1234',null,null,null,null,null,null,'N');

CREATE TABLE `st_user_info_item` (
  `ID` bigint(20) NOT NULL COMMENT 'ID',
  `CREATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
  `CREATE_USER` varchar(50) DEFAULT NULL COMMENT '생성자',
  `UPDATE_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '수정일',
  `UPDATE_USER` varchar(50) DEFAULT NULL COMMENT '수정자',
  `DELETE_YN` varchar(1) DEFAULT 'N' COMMENT '삭제여부',
  `USER_INFO_ID` bigint(20) NOT NULL COMMENT 'USER_INFO ID',
  `DISPLAY_ORDER` int(3) DEFAULT NULL COMMENT '순서',
  `PROJECT_COMPANY` varchar(30) DEFAULT NULL COMMENT '회사',
  `PROJECT_NAME` varchar(30) DEFAULT NULL COMMENT '프로젝트명',
  `PROJECT_YEAR` varchar(10) DEFAULT NULL COMMENT '년도',
  `APPLY_MODULE` varchar(30) DEFAULT NULL COMMENT '적용모듈',
  `DESCRIPTION` varchar(100) DEFAULT NULL COMMENT '설명',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='사용자 ITEM 테이블';

insert into `st_user_info_item`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`USER_INFO_ID`,`DISPLAY_ORDER`,`PROJECT_COMPANY`,`PROJECT_NAME`,`PROJECT_YEAR`,`APPLY_MODULE`,`DESCRIPTION`) values (4,'2020-08-17 13:11:31','ADMIN','2020-08-17 13:11:31','ADMIN','N',9,1,'삼성','프로젝트1','2010','on','설명');
insert into `st_user_info_item`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`USER_INFO_ID`,`DISPLAY_ORDER`,`PROJECT_COMPANY`,`PROJECT_NAME`,`PROJECT_YEAR`,`APPLY_MODULE`,`DESCRIPTION`) values (5,'2020-08-17 13:11:31','ADMIN','2020-08-17 13:11:31','ADMIN','N',9,2,'엘지','프로젝트2','2015',null,'설명2');
insert into `st_user_info_item`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`USER_INFO_ID`,`DISPLAY_ORDER`,`PROJECT_COMPANY`,`PROJECT_NAME`,`PROJECT_YEAR`,`APPLY_MODULE`,`DESCRIPTION`) values (86,'2020-08-22 21:39:16','ADMIN','2020-08-22 21:39:16','ADMIN','N',3,1,'삼성','수학공부','2020','on','설명');
insert into `st_user_info_item`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`USER_INFO_ID`,`DISPLAY_ORDER`,`PROJECT_COMPANY`,`PROJECT_NAME`,`PROJECT_YEAR`,`APPLY_MODULE`,`DESCRIPTION`) values (87,'2020-08-22 21:43:30','ADMIN','2021-02-17 15:59:18','ADMIN','N',4,1,'삼성','수학공부2','2020','project','설명');
insert into `st_user_info_item`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`DELETE_YN`,`USER_INFO_ID`,`DISPLAY_ORDER`,`PROJECT_COMPANY`,`PROJECT_NAME`,`PROJECT_YEAR`,`APPLY_MODULE`,`DESCRIPTION`) values (117,'2020-09-02 21:34:57','ADMIN','2020-09-02 21:34:57','ADMIN','N',7,1,'삼성','수학공부','2015','project,bom','설명');

CREATE TABLE `st_user_info_temp` (
  `ID` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'ID',
  `CREATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
  `CREATE_USER` varchar(50) DEFAULT NULL COMMENT '생성자',
  `UPDATE_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '수정일',
  `UPDATE_USER` varchar(50) DEFAULT NULL COMMENT '수정자',
  `USER_ID` varchar(50) DEFAULT NULL COMMENT '사용자ID',
  `USER_NAME` varchar(50) DEFAULT NULL COMMENT '사용자명',
  `PASSWORD` varchar(50) DEFAULT NULL COMMENT '패스워드',
  `AGE` int(3) DEFAULT NULL COMMENT '나이',
  `GENDER` varchar(10) DEFAULT NULL COMMENT '성별',
  `BIRTH_DATE` date DEFAULT NULL COMMENT '생일',
  `CITY` varchar(30) DEFAULT NULL COMMENT '도시',
  `HOBBY` varchar(60) DEFAULT NULL COMMENT '취미',
  `COMMENT` varchar(300) DEFAULT NULL COMMENT '설명',
  `DELETE_YN` varchar(1) DEFAULT 'N' COMMENT '삭제여부'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into `st_user_info_temp`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`USER_ID`,`USER_NAME`,`PASSWORD`,`AGE`,`GENDER`,`BIRTH_DATE`,`CITY`,`HOBBY`,`COMMENT`,`DELETE_YN`) values (3,'2020-08-22 21:39:16','admin','2020-08-22 21:39:16','admin','abcd1@naver.com','김예린','123456789a',15,'female','2020-08-01 00:00:00','seoul','book,music,game','안녕하세요','N');
insert into `st_user_info_temp`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`USER_ID`,`USER_NAME`,`PASSWORD`,`AGE`,`GENDER`,`BIRTH_DATE`,`CITY`,`HOBBY`,`COMMENT`,`DELETE_YN`) values (4,'2020-08-22 21:42:42','admin','2020-09-02 22:12:05','admin','abcd1@naver.com','김예린','1234',15,'female','2020-08-01 00:00:00','seoul','book,music,game','안녕하세요','N');
insert into `st_user_info_temp`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`USER_ID`,`USER_NAME`,`PASSWORD`,`AGE`,`GENDER`,`BIRTH_DATE`,`CITY`,`HOBBY`,`COMMENT`,`DELETE_YN`) values (6,'2020-08-16 11:11:06','admin','2020-08-16 11:11:06','admin','orktw@naver.com','김태우','1234',45,'','1973-12-01 00:00:00','suwon','music,game','안녕하세요','N');
insert into `st_user_info_temp`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`USER_ID`,`USER_NAME`,`PASSWORD`,`AGE`,`GENDER`,`BIRTH_DATE`,`CITY`,`HOBBY`,`COMMENT`,`DELETE_YN`) values (7,'2020-08-16 11:36:04','admin','2020-09-02 22:03:58','admin','orktw@naver.com','김태우','1234',45,'female','1973-12-01 00:00:00','suwon','music,game','안녕하세요','N');
insert into `st_user_info_temp`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`USER_ID`,`USER_NAME`,`PASSWORD`,`AGE`,`GENDER`,`BIRTH_DATE`,`CITY`,`HOBBY`,`COMMENT`,`DELETE_YN`) values (8,'2020-08-16 11:47:26','admin','2020-08-16 11:47:26','admin','orktw@naver.com','김태우','1234',45,'male','1973-12-01 00:00:00','suwon','music,game','안녕하세요','N');
insert into `st_user_info_temp`(`ID`,`CREATE_DATE`,`CREATE_USER`,`UPDATE_DATE`,`UPDATE_USER`,`USER_ID`,`USER_NAME`,`PASSWORD`,`AGE`,`GENDER`,`BIRTH_DATE`,`CITY`,`HOBBY`,`COMMENT`,`DELETE_YN`) values (9,'2020-08-16 11:52:41','admin','2020-08-17 13:36:37','admin','orktw@naver.com','김태똥','1234',45,'male','1973-12-01 00:00:00','busan','book,music','안녕하세요','N');

CREATE TABLE `tbl_attach` (
  `uuid` varchar(100) NOT NULL,
  `uploadPath` varchar(200) NOT NULL,
  `fileName` varchar(100) NOT NULL,
  `filetype` char(1) DEFAULT 'I',
  `bno` bigint(10) unsigned NOT NULL,
  PRIMARY KEY (`uuid`),
  KEY `fk_board_attach` (`bno`),
  CONSTRAINT `fk_board_attach` FOREIGN KEY (`bno`) REFERENCES `tbl_board` (`bno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into `tbl_attach`(`uuid`,`uploadPath`,`fileName`,`filetype`,`bno`) values ('4f2d8487-e13f-4aac-8312-19e4d4ec259a','2021\01\17','스프링부트 개념과 활용.pdf','0',6);
insert into `tbl_attach`(`uuid`,`uploadPath`,`fileName`,`filetype`,`bno`) values ('590eada8-7ad8-40a8-8d02-70c97df2d72c','2021\01\23','미운 우리 새끼.E224.210110.720p-NEXT.torrent','0',6);
insert into `tbl_attach`(`uuid`,`uploadPath`,`fileName`,`filetype`,`bno`) values ('72bcd8ed-a29e-4bd1-802d-836a6db82c0a','2021\01\17','스프링부트 개념과 활용.pdf','0',9);
insert into `tbl_attach`(`uuid`,`uploadPath`,`fileName`,`filetype`,`bno`) values ('c6d65322-e325-479d-bdea-58f131ca4cc4','2021\01\17','백기선의 스프링 프레임워크 핵심 기술.pdf','0',6);
insert into `tbl_attach`(`uuid`,`uploadPath`,`fileName`,`filetype`,`bno`) values ('f2edb410-985a-4c60-b972-2632b47c6fe7','2021\01\23','스위스.jpg','1',7);

CREATE TABLE `tbl_board` (
  `bno` bigint(10) unsigned NOT NULL,
  `title` varchar(200) NOT NULL,
  `content` varchar(2000) NOT NULL,
  `writer` varchar(50) NOT NULL,
  `regdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `replycnt` bigint(20) DEFAULT '0',
  PRIMARY KEY (`bno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


insert into `tbl_board`(`bno`,`title`,`content`,`writer`,`regdate`,`updatedate`,`replycnt`) values (3,'TS NO 테스트3','테스트 내용','user00','2021-01-02 17:47:58','2021-01-02 17:47:58',13);
insert into `tbl_board`(`bno`,`title`,`content`,`writer`,`regdate`,`updatedate`,`replycnt`) values (4,'TS NO 테스트4','테스트 내용2','user00','2021-01-03 10:58:49','2021-01-03 10:58:49',1);
insert into `tbl_board`(`bno`,`title`,`content`,`writer`,`regdate`,`updatedate`,`replycnt`) values (5,'게시판 등록','게시판 등록','김태우','2021-01-03 15:09:27','2021-01-03 15:09:27',1);
insert into `tbl_board`(`bno`,`title`,`content`,`writer`,`regdate`,`updatedate`,`replycnt`) values (6,'게시판 작성 112','게시판 작성 내용 1
내용수정
내용수정
내용수정','작성자 1','2021-01-03 20:29:25','2021-01-23 11:22:10',0);
insert into `tbl_board`(`bno`,`title`,`content`,`writer`,`regdate`,`updatedate`,`replycnt`) values (7,'게시판 작성 2','게시판 작성 내용 2','작성자 2','2021-01-03 20:29:25','2021-01-23 11:47:14',0);
insert into `tbl_board`(`bno`,`title`,`content`,`writer`,`regdate`,`updatedate`,`replycnt`) values (8,'게시판 작성 3','게시판 작성 내용 3','작성자 3','2021-01-03 20:29:25','2021-01-17 21:32:24',0);
insert into `tbl_board`(`bno`,`title`,`content`,`writer`,`regdate`,`updatedate`,`replycnt`) values (9,'게시판 작성 4','게시판 작성 내용 4','작성자 4','2021-01-03 20:29:25','2021-01-17 21:32:43',0);
insert into `tbl_board`(`bno`,`title`,`content`,`writer`,`regdate`,`updatedate`,`replycnt`) values (10,'게시판 작성 5','게시판 작성 내용 5','작성자 5','2021-01-03 20:29:25','2021-01-03 20:29:25',0);
insert into `tbl_board`(`bno`,`title`,`content`,`writer`,`regdate`,`updatedate`,`replycnt`) values (11,'게시판 작성 6','게시판 작성 내용 6','작성자 6','2021-01-03 20:29:25','2021-01-03 20:29:25',0);
insert into `tbl_board`(`bno`,`title`,`content`,`writer`,`regdate`,`updatedate`,`replycnt`) values (12,'게시판 작성 7','게시판 작성 내용 7','작성자 7','2021-01-03 20:29:25','2021-01-03 20:29:25',0);
insert into `tbl_board`(`bno`,`title`,`content`,`writer`,`regdate`,`updatedate`,`replycnt`) values (13,'게시판 작성 8','게시판 작성 내용 8','작성자 8','2021-01-03 20:29:25','2021-01-03 20:29:25',0);
insert into `tbl_board`(`bno`,`title`,`content`,`writer`,`regdate`,`updatedate`,`replycnt`) values (14,'게시판 작성 9','게시판 작성 내용 9','작성자 9','2021-01-03 20:29:25','2021-01-03 20:29:25',0);
insert into `tbl_board`(`bno`,`title`,`content`,`writer`,`regdate`,`updatedate`,`replycnt`) values (15,'게시판 작성 10','게시판 작성 내용 10','작성자 10','2021-01-03 20:29:25','2021-01-03 20:29:25',0);
insert into `tbl_board`(`bno`,`title`,`content`,`writer`,`regdate`,`updatedate`,`replycnt`) values (16,'첨부파일 확인','첨부파일 확인','김태우','2021-01-17 17:22:08','2021-01-17 17:22:08',0);

CREATE TABLE `tbl_member` (
  `userid` varchar(50) NOT NULL,
  `userpw` varchar(100) NOT NULL,
  `username` varchar(100) NOT NULL,
  `regdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `enabled` char(1) DEFAULT '1',
  `email` varchar(255) NOT NULL,
  `moddate` datetime(6) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tbl_member_auth` (
  `userid` varchar(50) NOT NULL,
  `auth` varchar(50) NOT NULL,
  KEY `fk_member_auth` (`userid`),
  CONSTRAINT `fk_member_auth` FOREIGN KEY (`userid`) REFERENCES `tbl_member` (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tbl_memo` (
  `mno` bigint(20) NOT NULL AUTO_INCREMENT,
  `memo_text` varchar(200) NOT NULL,
  PRIMARY KEY (`mno`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8;


insert into `tbl_memo`(`mno`,`memo_text`) values (11,'Sample...1');
insert into `tbl_memo`(`mno`,`memo_text`) values (12,'Sample...2');
insert into `tbl_memo`(`mno`,`memo_text`) values (13,'Sample...3');
insert into `tbl_memo`(`mno`,`memo_text`) values (14,'Sample...4');
insert into `tbl_memo`(`mno`,`memo_text`) values (15,'Sample...5');
insert into `tbl_memo`(`mno`,`memo_text`) values (16,'Sample...6');
insert into `tbl_memo`(`mno`,`memo_text`) values (17,'Sample...7');
insert into `tbl_memo`(`mno`,`memo_text`) values (18,'Sample...8');
insert into `tbl_memo`(`mno`,`memo_text`) values (19,'Sample...9');
insert into `tbl_memo`(`mno`,`memo_text`) values (20,'Sample...10');
insert into `tbl_memo`(`mno`,`memo_text`) values (21,'Sample...11');
insert into `tbl_memo`(`mno`,`memo_text`) values (22,'Sample...12');
insert into `tbl_memo`(`mno`,`memo_text`) values (23,'Sample...13');
insert into `tbl_memo`(`mno`,`memo_text`) values (24,'Sample...14');
insert into `tbl_memo`(`mno`,`memo_text`) values (25,'Sample...15');
insert into `tbl_memo`(`mno`,`memo_text`) values (26,'Sample...16');
insert into `tbl_memo`(`mno`,`memo_text`) values (27,'Sample...17');
insert into `tbl_memo`(`mno`,`memo_text`) values (28,'Sample...18');
insert into `tbl_memo`(`mno`,`memo_text`) values (29,'Sample...19');
insert into `tbl_memo`(`mno`,`memo_text`) values (30,'Sample...20');
insert into `tbl_memo`(`mno`,`memo_text`) values (31,'Sample...21');
insert into `tbl_memo`(`mno`,`memo_text`) values (32,'Sample...22');
insert into `tbl_memo`(`mno`,`memo_text`) values (33,'Sample...23');
insert into `tbl_memo`(`mno`,`memo_text`) values (34,'Sample...24');
insert into `tbl_memo`(`mno`,`memo_text`) values (35,'Sample...25');
insert into `tbl_memo`(`mno`,`memo_text`) values (36,'Sample...26');
insert into `tbl_memo`(`mno`,`memo_text`) values (37,'Sample...27');
insert into `tbl_memo`(`mno`,`memo_text`) values (38,'Sample...28');
insert into `tbl_memo`(`mno`,`memo_text`) values (39,'Sample...29');
insert into `tbl_memo`(`mno`,`memo_text`) values (40,'Sample...30');
insert into `tbl_memo`(`mno`,`memo_text`) values (41,'Sample...31');
insert into `tbl_memo`(`mno`,`memo_text`) values (42,'Sample...32');
insert into `tbl_memo`(`mno`,`memo_text`) values (43,'Sample...33');
insert into `tbl_memo`(`mno`,`memo_text`) values (44,'Sample...34');
insert into `tbl_memo`(`mno`,`memo_text`) values (45,'Sample...35');
insert into `tbl_memo`(`mno`,`memo_text`) values (46,'Sample...36');
insert into `tbl_memo`(`mno`,`memo_text`) values (47,'Sample...37');
insert into `tbl_memo`(`mno`,`memo_text`) values (48,'Sample...38');
insert into `tbl_memo`(`mno`,`memo_text`) values (49,'Sample...39');
insert into `tbl_memo`(`mno`,`memo_text`) values (50,'Sample...40');
insert into `tbl_memo`(`mno`,`memo_text`) values (51,'Sample...41');
insert into `tbl_memo`(`mno`,`memo_text`) values (52,'Sample...42');
insert into `tbl_memo`(`mno`,`memo_text`) values (53,'Sample...43');
insert into `tbl_memo`(`mno`,`memo_text`) values (54,'Sample...44');
insert into `tbl_memo`(`mno`,`memo_text`) values (55,'Sample...45');
insert into `tbl_memo`(`mno`,`memo_text`) values (56,'Sample...46');
insert into `tbl_memo`(`mno`,`memo_text`) values (57,'Sample...47');
insert into `tbl_memo`(`mno`,`memo_text`) values (58,'Sample...48');
insert into `tbl_memo`(`mno`,`memo_text`) values (59,'Sample...49');
insert into `tbl_memo`(`mno`,`memo_text`) values (60,'Sample...50');
insert into `tbl_memo`(`mno`,`memo_text`) values (61,'Sample...51');
insert into `tbl_memo`(`mno`,`memo_text`) values (62,'Sample...52');
insert into `tbl_memo`(`mno`,`memo_text`) values (63,'Sample...53');
insert into `tbl_memo`(`mno`,`memo_text`) values (64,'Sample...54');
insert into `tbl_memo`(`mno`,`memo_text`) values (65,'Sample...55');
insert into `tbl_memo`(`mno`,`memo_text`) values (66,'Sample...56');
insert into `tbl_memo`(`mno`,`memo_text`) values (67,'Sample...57');
insert into `tbl_memo`(`mno`,`memo_text`) values (68,'Sample...58');
insert into `tbl_memo`(`mno`,`memo_text`) values (69,'Sample...59');
insert into `tbl_memo`(`mno`,`memo_text`) values (70,'Sample...60');
insert into `tbl_memo`(`mno`,`memo_text`) values (71,'Sample...61');
insert into `tbl_memo`(`mno`,`memo_text`) values (72,'Sample...62');
insert into `tbl_memo`(`mno`,`memo_text`) values (73,'Sample...63');
insert into `tbl_memo`(`mno`,`memo_text`) values (74,'Sample...64');
insert into `tbl_memo`(`mno`,`memo_text`) values (75,'Sample...65');
insert into `tbl_memo`(`mno`,`memo_text`) values (76,'Sample...66');
insert into `tbl_memo`(`mno`,`memo_text`) values (77,'Sample...67');
insert into `tbl_memo`(`mno`,`memo_text`) values (78,'Sample...68');
insert into `tbl_memo`(`mno`,`memo_text`) values (79,'Sample...69');
insert into `tbl_memo`(`mno`,`memo_text`) values (80,'Sample...70');
insert into `tbl_memo`(`mno`,`memo_text`) values (81,'Sample...71');
insert into `tbl_memo`(`mno`,`memo_text`) values (82,'Sample...72');
insert into `tbl_memo`(`mno`,`memo_text`) values (83,'Sample...73');
insert into `tbl_memo`(`mno`,`memo_text`) values (84,'Sample...74');
insert into `tbl_memo`(`mno`,`memo_text`) values (85,'Sample...75');
insert into `tbl_memo`(`mno`,`memo_text`) values (86,'Sample...76');
insert into `tbl_memo`(`mno`,`memo_text`) values (87,'Sample...77');
insert into `tbl_memo`(`mno`,`memo_text`) values (88,'Sample...78');
insert into `tbl_memo`(`mno`,`memo_text`) values (89,'Sample...79');
insert into `tbl_memo`(`mno`,`memo_text`) values (90,'Sample...80');
insert into `tbl_memo`(`mno`,`memo_text`) values (91,'Sample...81');
insert into `tbl_memo`(`mno`,`memo_text`) values (92,'Sample...82');
insert into `tbl_memo`(`mno`,`memo_text`) values (93,'Sample...83');
insert into `tbl_memo`(`mno`,`memo_text`) values (94,'Sample...84');
insert into `tbl_memo`(`mno`,`memo_text`) values (95,'Sample...85');
insert into `tbl_memo`(`mno`,`memo_text`) values (96,'Sample...86');
insert into `tbl_memo`(`mno`,`memo_text`) values (97,'Sample...87');
insert into `tbl_memo`(`mno`,`memo_text`) values (98,'Sample...88');
insert into `tbl_memo`(`mno`,`memo_text`) values (99,'Sample...89');
insert into `tbl_memo`(`mno`,`memo_text`) values (100,'Sample...90');

CREATE TABLE `tbl_reply` (
  `rno` bigint(10) unsigned NOT NULL,
  `bno` bigint(10) unsigned NOT NULL,
  `reply` varchar(1000) NOT NULL,
  `replyer` varchar(50) NOT NULL,
  `replyDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`rno`),
  KEY `fk_reply_board` (`bno`),
  CONSTRAINT `fk_reply_board` FOREIGN KEY (`bno`) REFERENCES `tbl_board` (`bno`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


insert into `tbl_reply`(`rno`,`bno`,`reply`,`replyer`,`replyDate`,`updateDate`) values (1,3,'댓글 테스트 1','replyer1','2021-01-09 13:07:07','2021-01-09 13:07:07');
insert into `tbl_reply`(`rno`,`bno`,`reply`,`replyer`,`replyDate`,`updateDate`) values (2,3,'댓글 테스트 2','replyer2','2021-01-09 13:07:07','2021-01-09 13:07:07');
insert into `tbl_reply`(`rno`,`bno`,`reply`,`replyer`,`replyDate`,`updateDate`) values (3,3,'Modified Reply....','replyer3','2021-01-09 13:07:07','2021-01-10 10:22:46');
insert into `tbl_reply`(`rno`,`bno`,`reply`,`replyer`,`replyDate`,`updateDate`) values (4,3,'댓글 테스트 4','replyer4','2021-01-09 13:07:07','2021-01-09 13:07:07');
insert into `tbl_reply`(`rno`,`bno`,`reply`,`replyer`,`replyDate`,`updateDate`) values (5,3,'댓글 테스트 5','replyer5','2021-01-09 13:07:07','2021-01-09 13:07:07');
insert into `tbl_reply`(`rno`,`bno`,`reply`,`replyer`,`replyDate`,`updateDate`) values (6,3,'댓글 테스트 6','replyer6','2021-01-09 13:07:07','2021-01-09 13:07:07');
insert into `tbl_reply`(`rno`,`bno`,`reply`,`replyer`,`replyDate`,`updateDate`) values (7,3,'댓글 테스트 7','replyer7','2021-01-09 13:07:07','2021-01-09 13:07:07');
insert into `tbl_reply`(`rno`,`bno`,`reply`,`replyer`,`replyDate`,`updateDate`) values (8,3,'댓글 테스트 8','replyer8','2021-01-09 13:07:07','2021-01-09 13:07:07');
insert into `tbl_reply`(`rno`,`bno`,`reply`,`replyer`,`replyDate`,`updateDate`) values (9,3,'댓글 테스트 9','replyer9','2021-01-09 13:07:07','2021-01-09 13:07:07');
insert into `tbl_reply`(`rno`,`bno`,`reply`,`replyer`,`replyDate`,`updateDate`) values (10,3,'댓글 테스트 10','replyer10','2021-01-09 13:07:07','2021-01-09 13:07:07');
insert into `tbl_reply`(`rno`,`bno`,`reply`,`replyer`,`replyDate`,`updateDate`) values (11,4,'JS Test2','tester','2021-01-10 10:08:04','2021-01-10 17:41:43');
insert into `tbl_reply`(`rno`,`bno`,`reply`,`replyer`,`replyDate`,`updateDate`) values (12,3,'댓글 달기','댓글이','2021-01-10 11:54:20','2021-01-10 11:54:20');
insert into `tbl_reply`(`rno`,`bno`,`reply`,`replyer`,`replyDate`,`updateDate`) values (13,3,'댓글달기2','댓글이','2021-01-10 11:55:22','2021-01-10 11:55:22');
insert into `tbl_reply`(`rno`,`bno`,`reply`,`replyer`,`replyDate`,`updateDate`) values (14,3,'댓글달기3','댓글이','2021-01-10 11:57:47','2021-01-10 11:57:47');
insert into `tbl_reply`(`rno`,`bno`,`reply`,`replyer`,`replyDate`,`updateDate`) values (15,5,'게시판 1개등록','강공','2021-01-16 19:58:15','2021-01-16 19:58:15');

CREATE TABLE `users` (
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `enabled` char(1) DEFAULT '1',
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into `users`(`username`,`password`,`enabled`) values ('admin00','pw00','1');
insert into `users`(`username`,`password`,`enabled`) values ('member00','pw00','1');
insert into `users`(`username`,`password`,`enabled`) values ('user00','pw00','1');

DROP FUNCTION IF EXISTS seckimdb.get_seq;
CREATE FUNCTION seckimdb.`get_seq`(p_seq_name VARCHAR(45)) RETURNS int(11)
    READS SQL DATA
BEGIN
DECLARE RESULT_ID INT;
UPDATE seq_mysql SET id = LAST_INSERT_ID(id+1)
WHERE seq_name = p_seq_name;
SET RESULT_ID = (SELECT LAST_INSERT_ID());
RETURN RESULT_ID;
END;

DROP FUNCTION IF EXISTS seckimdb.initcap;
CREATE FUNCTION seckimdb.`initcap`(x char(30)) RETURNS char(30) CHARSET utf8
BEGIN
SET @str='';
SET @l_str='';
WHILE x REGEXP ' ' DO
SELECT SUBSTRING_INDEX(x, ' ', 1) INTO @l_str;
SELECT SUBSTRING(x, LOCATE(' ', x)+1) INTO x;
SELECT CONCAT(@str, ' ', CONCAT(UPPER(SUBSTRING(@l_str,1,1)),LOWER(SUBSTRING(@l_str,2)))) INTO @str;
END WHILE;
RETURN LTRIM(CONCAT(@str, ' ', CONCAT(UPPER(SUBSTRING(x,1,1)),LOWER(SUBSTRING(x,2)))));
END;












