CREATE DATABASE  IF NOT EXISTS `login_database` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `login_database`;
-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: login_database
-- ------------------------------------------------------
-- Server version	8.0.40

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `accounts`
--

DROP TABLE IF EXISTS `accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `accounts` (
  `AccountID` int NOT NULL AUTO_INCREMENT,
  `Username` varchar(50) DEFAULT NULL,
  `Pass` varchar(50) DEFAULT NULL,
  `profilePhoto` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`AccountID`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `accounts`
--

LOCK TABLES `accounts` WRITE;
/*!40000 ALTER TABLE `accounts` DISABLE KEYS */;
INSERT INTO `accounts` VALUES (1,'RrOTeo','miaumiau','file:/C:/Users/teodo/Desktop/WhatsApp%20Image%202025-02-21%20at%2011.05.35%20(1).jpeg'),(2,'Mihai','miaumiau','file:/C:/Users/teodo/IdeaProjects/LaboratorMostenire/Aplicatie/src/main/resources/com/example/aplicatie/icons/accept.png'),(3,'ana','miaumiau',NULL),(4,'marius','miaumiau',NULL),(9,'Ana','123',NULL),(10,'Mircea','123',NULL),(11,'Andrei','123',NULL),(12,'Covrigel','miaumiau',NULL);
/*!40000 ALTER TABLE `accounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `friendrequests`
--

DROP TABLE IF EXISTS `friendrequests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `friendrequests` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `AccountID` int DEFAULT NULL,
  `RequestedTo` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `AccountID` (`AccountID`),
  CONSTRAINT `friendrequests_ibfk_1` FOREIGN KEY (`AccountID`) REFERENCES `accounts` (`AccountID`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `friendrequests`
--

LOCK TABLES `friendrequests` WRITE;
/*!40000 ALTER TABLE `friendrequests` DISABLE KEYS */;
/*!40000 ALTER TABLE `friendrequests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `friends`
--

DROP TABLE IF EXISTS `friends`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `friends` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `AccountID` int DEFAULT NULL,
  `FriendUsername` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `AccountID` (`AccountID`),
  CONSTRAINT `friends_ibfk_1` FOREIGN KEY (`AccountID`) REFERENCES `accounts` (`AccountID`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `friends`
--

LOCK TABLES `friends` WRITE;
/*!40000 ALTER TABLE `friends` DISABLE KEYS */;
/*!40000 ALTER TABLE `friends` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `messages`
--

DROP TABLE IF EXISTS `messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `messages` (
  `messageID` int NOT NULL AUTO_INCREMENT,
  `sentToID` int DEFAULT NULL,
  `sentFromID` int DEFAULT NULL,
  `content` varchar(500) DEFAULT NULL,
  `ServerPort` int DEFAULT NULL,
  `messageTimestamp` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`messageID`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `messages`
--

LOCK TABLES `messages` WRITE;
/*!40000 ALTER TABLE `messages` DISABLE KEYS */;
INSERT INTO `messages` VALUES (1,1,2,'a',1000,'2025-03-20 16:29:42'),(2,2,1,'b',1000,'2025-03-20 16:29:43'),(3,1,2,'a',1000,'2025-03-20 16:29:45'),(4,2,1,'b',1000,'2025-03-20 16:29:47'),(5,1,2,'a',1000,'2025-03-20 16:42:40'),(6,1,2,'a',1000,'2025-03-20 16:42:41'),(7,1,2,'c',1000,'2025-03-20 16:42:55'),(8,1,2,'d',1000,'2025-03-20 16:43:35'),(9,1,2,'d',1000,'2025-03-20 16:43:37'),(10,1,2,'a',1000,'2025-03-20 17:37:44'),(11,2,1,'b',1000,'2025-03-20 17:37:46'),(12,1,2,'a',1000,'2025-03-21 07:00:11'),(13,2,1,'b',1000,'2025-03-21 07:00:13'),(14,10,1,'a',1003,'2025-03-21 07:00:19'),(15,1,10,'b',1003,'2025-03-21 07:00:21'),(16,2,1,'sa',1000,'2025-03-21 07:00:33'),(17,1,10,'s',1003,'2025-03-21 07:00:38'),(18,1,10,'a',1003,'2025-03-21 07:00:49'),(19,10,1,'ad',1003,'2025-03-21 07:00:53'),(20,10,1,'a',1003,'2025-03-21 07:01:02'),(21,1,10,'ad',1003,'2025-03-21 07:01:15'),(22,10,1,'ad',1003,'2025-03-21 07:01:17'),(23,1,10,'d',1003,'2025-03-21 07:01:22'),(24,1,10,'ad',1003,'2025-03-21 07:01:29'),(25,1,2,'a',1000,'2025-03-21 07:08:06'),(26,2,1,'a',1000,'2025-03-21 07:08:07'),(27,1,10,'a',1003,'2025-03-21 07:08:09'),(28,1,2,'a',1000,'2025-03-21 23:17:44'),(29,2,1,'b',1000,'2025-03-21 23:17:46'),(30,2,1,'a',1000,'2025-03-21 23:17:47'),(31,1,2,'a',1000,'2025-03-21 23:17:49'),(32,1,2,'b',1000,'2025-03-22 10:24:36'),(33,1,2,'a',1000,'2025-03-22 10:52:33'),(34,1,2,'a',1000,'2025-03-22 11:15:50'),(35,1,2,'adawda',1000,'2025-03-24 05:56:55'),(36,1,2,'a',1000,'2025-03-24 07:06:44'),(37,1,2,'a',1000,'2025-03-24 08:00:41'),(38,1,2,'b',1000,'2025-03-24 08:00:42'),(39,2,10,'Salut',1004,'2025-03-24 12:50:37'),(40,2,11,'Noroc',1013,'2025-03-24 18:57:49'),(41,11,2,'Salut salut',1013,'2025-03-24 18:58:17'),(42,1,2,'a',1019,'2025-03-24 22:03:43'),(43,2,1,'a',1019,'2025-03-24 22:04:01'),(44,1,2,'b',1019,'2025-03-24 22:04:06'),(45,2,1,'a',1019,'2025-03-24 22:04:09'),(46,1,2,'b',1019,'2025-03-24 22:04:13'),(47,1,2,'a',1019,'2025-03-24 22:06:24');
/*!40000 ALTER TABLE `messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pendings`
--

DROP TABLE IF EXISTS `pendings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pendings` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `AccountID` int DEFAULT NULL,
  `RequestedFrom` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `AccountID` (`AccountID`),
  CONSTRAINT `pendings_ibfk_1` FOREIGN KEY (`AccountID`) REFERENCES `accounts` (`AccountID`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pendings`
--

LOCK TABLES `pendings` WRITE;
/*!40000 ALTER TABLE `pendings` DISABLE KEYS */;
/*!40000 ALTER TABLE `pendings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `serverdetail`
--

DROP TABLE IF EXISTS `serverdetail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `serverdetail` (
  `ServerPort` int NOT NULL AUTO_INCREMENT,
  `AccountID` int DEFAULT NULL,
  `SecondAccountID` int DEFAULT NULL,
  `isOpen` tinyint(1) DEFAULT NULL,
  `firstAccountTimestamp` timestamp NULL DEFAULT NULL,
  `secondAccountTimestamp` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`ServerPort`)
) ENGINE=InnoDB AUTO_INCREMENT=1020 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `serverdetail`
--

LOCK TABLES `serverdetail` WRITE;
/*!40000 ALTER TABLE `serverdetail` DISABLE KEYS */;
/*!40000 ALTER TABLE `serverdetail` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-03-25 14:26:28
