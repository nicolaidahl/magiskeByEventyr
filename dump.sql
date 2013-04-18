-- MySQL dump 10.14  Distrib 5.5.30-MariaDB, for osx10.8 (i386)
--
-- Host: localhost    Database: eventyr
-- ------------------------------------------------------
-- Server version	5.5.30-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `creating`
--

DROP TABLE IF EXISTS `creating`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `creating` (
  `user` varchar(30) NOT NULL DEFAULT '',
  `fairyTaleId` int(11) NOT NULL DEFAULT '0',
  `privilege` varchar(20) NOT NULL DEFAULT '',
  PRIMARY KEY (`user`,`fairyTaleId`,`privilege`),
  KEY `fairyTaleId` (`fairyTaleId`),
  KEY `privilege` (`privilege`),
  CONSTRAINT `creating_ibfk_1` FOREIGN KEY (`user`) REFERENCES `user` (`email`),
  CONSTRAINT `creating_ibfk_2` FOREIGN KEY (`fairyTaleId`) REFERENCES `fairy_tale` (`id`),
  CONSTRAINT `creating_ibfk_3` FOREIGN KEY (`privilege`) REFERENCES `privilege` (`identifier`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `creating`
--

LOCK TABLES `creating` WRITE;
/*!40000 ALTER TABLE `creating` DISABLE KEYS */;
/*!40000 ALTER TABLE `creating` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES (10,'Nilu'),(11,'Nikolaj');
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fairy_tale`
--

DROP TABLE IF EXISTS `fairy_tale`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fairy_tale` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `customerId` int(11) DEFAULT NULL,
  `name` varchar(40) DEFAULT NULL,
  `dueDate` date DEFAULT NULL,
  `briefing` text,
  PRIMARY KEY (`id`),
  KEY `customerId` (`customerId`),
  CONSTRAINT `fairy_tale_ibfk_1` FOREIGN KEY (`customerId`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fairy_tale`
--

LOCK TABLES `fairy_tale` WRITE;
/*!40000 ALTER TABLE `fairy_tale` DISABLE KEYS */;
INSERT INTO `fairy_tale` VALUES (4,10,'Eventyret om retina!','2013-04-10','Det skal være godt!'),(5,11,'Test','2013-04-18','lalaj');
/*!40000 ALTER TABLE `fairy_tale` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lead`
--

DROP TABLE IF EXISTS `lead`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lead` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `fairyTaleId` int(11) DEFAULT NULL,
  `name` varchar(40) DEFAULT NULL,
  `soundFile` varchar(50) DEFAULT NULL,
  `imageFile` varchar(50) DEFAULT NULL,
  `priority` int(11) DEFAULT NULL,
  `story` varchar(256) DEFAULT NULL,
  `anchoring` varchar(60) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `soundFile` (`soundFile`),
  UNIQUE KEY `imageFile` (`imageFile`),
  KEY `fairyTaleId` (`fairyTaleId`),
  CONSTRAINT `lead_ibfk_1` FOREIGN KEY (`fairyTaleId`) REFERENCES `fairy_tale` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lead`
--

LOCK TABLES `lead` WRITE;
/*!40000 ALTER TABLE `lead` DISABLE KEYS */;
INSERT INTO `lead` VALUES (22,4,'Første ledetråd','2013-04-14T17:39:47.600+02:00','2013-04-14T17:39:47.587+02:00.jpg',0,'',''),(23,4,'Anden','2013-04-18T11:04:26.201+02:00','2013-04-14T17:42:08.417+02:00.jpg',0,'hej','');
/*!40000 ALTER TABLE `lead` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `leadState`
--

DROP TABLE IF EXISTS `leadState`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `leadState` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `leadState`
--

LOCK TABLES `leadState` WRITE;
/*!40000 ALTER TABLE `leadState` DISABLE KEYS */;
INSERT INTO `leadState` VALUES (1,'image'),(2,'audio');
/*!40000 ALTER TABLE `leadState` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `leadStepComment`
--

DROP TABLE IF EXISTS `leadStepComment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `leadStepComment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `leadId` int(11) NOT NULL DEFAULT '0',
  `leadState` enum('image','story','audio') NOT NULL,
  `userMail` varchar(100) NOT NULL,
  `postDate` date DEFAULT NULL,
  `comment` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `leadId` (`leadId`),
  KEY `userMail` (`userMail`),
  CONSTRAINT `leadIdForeign_1` FOREIGN KEY (`leadId`) REFERENCES `lead` (`id`),
  CONSTRAINT `userMailForeign_1` FOREIGN KEY (`userMail`) REFERENCES `user` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `leadStepComment`
--

LOCK TABLES `leadStepComment` WRITE;
/*!40000 ALTER TABLE `leadStepComment` DISABLE KEYS */;
/*!40000 ALTER TABLE `leadStepComment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `play_evolutions`
--

DROP TABLE IF EXISTS `play_evolutions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `play_evolutions` (
  `id` int(11) NOT NULL,
  `hash` varchar(255) NOT NULL,
  `applied_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `apply_script` text,
  `revert_script` text,
  `state` varchar(255) DEFAULT NULL,
  `last_problem` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `play_evolutions`
--

LOCK TABLES `play_evolutions` WRITE;
/*!40000 ALTER TABLE `play_evolutions` DISABLE KEYS */;
/*!40000 ALTER TABLE `play_evolutions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `privilege`
--

DROP TABLE IF EXISTS `privilege`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `privilege` (
  `identifier` varchar(20) NOT NULL,
  PRIMARY KEY (`identifier`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `privilege`
--

LOCK TABLES `privilege` WRITE;
/*!40000 ALTER TABLE `privilege` DISABLE KEYS */;
INSERT INTO `privilege` VALUES ('photography'),('script'),('sound');
/*!40000 ALTER TABLE `privilege` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `email` varchar(30) NOT NULL,
  `name` varchar(30) DEFAULT NULL,
  `password` varchar(20) DEFAULT NULL,
  `salt` varchar(20) DEFAULT NULL,
  `type` enum('admin','creator') NOT NULL,
  PRIMARY KEY (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES ('lalaj@lalaj.com','lalaj','secret',NULL,'admin');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-04-18 11:25:36
