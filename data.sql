-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: zorvyn
-- ------------------------------------------------------
-- Server version	8.4.3

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
-- Table structure for table `financial_records`
--

DROP TABLE IF EXISTS `financial_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `financial_records` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `amount` decimal(14,2) NOT NULL,
  `category` varchar(255) NOT NULL,
  `date` date NOT NULL,
  `deleted` bit(1) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `type` enum('EXPENSE','INCOME') NOT NULL,
  `created_by` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3iiyhh031thqq7e200pa1gohq` (`created_by`),
  CONSTRAINT `FK3iiyhh031thqq7e200pa1gohq` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `financial_records`
--

LOCK TABLES `financial_records` WRITE;
/*!40000 ALTER TABLE `financial_records` DISABLE KEYS */;
INSERT INTO `financial_records` VALUES (4,'2026-04-05 18:34:33.886114','2026-04-05 18:34:33.886114',7800.00,'Salary','2026-03-01',_binary '\0','March salary credited','INCOME',6),(5,'2026-04-05 18:34:33.917159','2026-04-05 18:34:33.917159',220.50,'Groceries','2026-03-02',_binary '\0','Weekly grocery purchase','EXPENSE',6),(6,'2026-04-05 18:34:33.944102','2026-04-05 18:34:33.944102',1450.00,'Rent','2026-03-03',_binary '\0','Apartment rent for March','EXPENSE',6),(7,'2026-04-05 18:34:33.956663','2026-04-05 18:34:33.956663',320.00,'Utilities','2026-03-05',_binary '\0','Electricity and internet bill','EXPENSE',6),(8,'2026-04-05 18:34:33.966881','2026-04-05 18:34:33.966881',540.75,'Freelance','2026-03-07',_binary '\0','UI consultation payment','INCOME',6),(9,'2026-04-05 18:34:33.982936','2026-04-05 18:34:33.982936',190.00,'Transport','2026-03-09',_binary '\0','Fuel and cab expenses','EXPENSE',6),(10,'2026-04-05 18:34:33.994102','2026-04-05 18:34:33.994102',6900.00,'Salary','2026-03-01',_binary '\0','March salary credited','INCOME',7),(11,'2026-04-05 18:34:34.004387','2026-04-05 18:34:34.004387',180.40,'Dining','2026-03-04',_binary '\0','Team dinner with clients','EXPENSE',7),(12,'2026-04-05 18:34:34.014319','2026-04-05 18:34:34.014319',950.00,'Travel','2026-03-06',_binary '\0','Conference trip booking','EXPENSE',7),(13,'2026-04-05 18:34:34.024463','2026-04-05 18:34:34.024463',300.00,'Bonus','2026-03-10',_binary '\0','Quarterly performance bonus','INCOME',7),(14,'2026-04-05 18:34:34.034937','2026-04-05 18:34:34.034937',120.99,'Subscriptions','2026-03-11',_binary '\0','Software renewals','EXPENSE',7),(15,'2026-04-05 18:34:34.045831','2026-04-05 18:34:34.045831',210.00,'Health','2026-03-12',_binary '\0','Routine medical checkup','EXPENSE',7),(16,'2026-04-05 18:34:34.059154','2026-04-05 18:34:34.059154',4300.00,'Salary','2026-03-01',_binary '\0','March salary credited','INCOME',8),(17,'2026-04-05 18:34:34.073142','2026-04-05 18:34:34.073142',130.50,'Groceries','2026-03-03',_binary '\0','Fresh produce and essentials','EXPENSE',8),(18,'2026-04-05 18:34:34.085559','2026-04-05 18:34:34.085559',780.00,'Education','2026-03-08',_binary '\0','Online certification fee','EXPENSE',8),(19,'2026-04-05 18:34:34.097912','2026-04-05 18:34:34.097912',420.00,'Part Time','2026-03-14',_binary '\0','Weekend tutoring payout','INCOME',8),(20,'2026-04-05 18:34:34.111962','2026-04-05 18:34:34.111962',95.75,'Entertainment','2026-03-15',_binary '\0','Movie and dinner outing','EXPENSE',8),(21,'2026-04-05 18:34:34.132612','2026-04-05 18:34:34.132612',160.00,'Shopping','2026-03-17',_binary '\0','Workwear purchase','EXPENSE',8),(22,'2026-04-05 18:34:34.165279','2026-04-05 18:34:34.165279',8200.00,'Salary','2026-03-01',_binary '\0','March salary credited','INCOME',9),(23,'2026-04-05 18:34:34.180325','2026-04-05 18:34:34.180325',1800.00,'Rent','2026-03-02',_binary '\0','Apartment rent for March','EXPENSE',9),(24,'2026-04-05 18:34:34.194750','2026-04-05 18:34:34.194750',260.00,'Utilities','2026-03-05',_binary '\0','Water and electricity bills','EXPENSE',9),(25,'2026-04-05 18:34:34.206750','2026-04-05 18:34:34.206750',650.00,'Investments','2026-03-13',_binary '\0','Mutual fund redemption','INCOME',9),(26,'2026-04-05 18:34:34.217838','2026-04-05 18:34:34.217838',340.60,'Insurance','2026-03-16',_binary '\0','Vehicle insurance premium','EXPENSE',9),(27,'2026-04-05 18:34:34.232303','2026-04-05 18:34:34.232303',210.25,'Dining','2026-03-18',_binary '\0','Family dinner','EXPENSE',9),(28,'2026-04-05 18:34:34.244304','2026-04-05 18:34:34.244304',5100.00,'Salary','2026-03-01',_binary '\0','March salary credited','INCOME',10),(29,'2026-04-05 18:34:34.256523','2026-04-05 18:34:34.256523',900.00,'Rent','2026-03-04',_binary '\0','Shared apartment rent','EXPENSE',10),(30,'2026-04-05 18:34:34.268519','2026-04-05 18:34:34.268519',110.00,'Transport','2026-03-06',_binary '\0','Metro and cab recharge','EXPENSE',10),(31,'2026-04-05 18:34:34.280694','2026-04-05 18:34:34.280694',250.00,'Refund','2026-03-19',_binary '\0','Travel reimbursement received','INCOME',10),(32,'2026-04-05 18:34:34.292103','2026-04-05 18:34:34.292103',145.35,'Groceries','2026-03-20',_binary '\0','Household essentials','EXPENSE',10),(33,'2026-04-05 18:34:34.302185','2026-04-05 18:34:34.302185',89.99,'Subscriptions','2026-03-21',_binary '\0','Streaming and tools renewal','EXPENSE',10);
/*!40000 ALTER TABLE `financial_records` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `email` varchar(255) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('ADMIN','ANALYST','VIEWER') NOT NULL,
  `status` tinyint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (6,'2026-04-05 18:34:33.705337','2026-04-05 18:34:33.705337','admin@zorvyn.dev','ADMIN','$2a$10$CNYLCNNnBizfuDO/P5tHNOt3pmiGIt2PduHG7mfphXQySo6DL4iDq','ADMIN',0),(7,'2026-04-05 18:34:33.815607','2026-04-05 18:34:33.815607','analyst@zorvyn.dev','ANALYST','$2a$10$e30hPz0yeFyHKAuZRIQj8OfAqZG6EqAitoWdUmPYd2DhWE3Oy9HX.','ANALYST',0),(8,'2026-04-05 18:34:33.831322','2026-04-05 18:34:33.831322','viewer@zorvyn.dev','VIEWER','$2a$10$Jkys8sy9aDs3xq8QE5myHepTAxS2tN11egY/7cF1i63iL/VwkNyxS','VIEWER',0),(9,'2026-04-05 18:34:33.847125','2026-04-05 18:34:33.847125','rohan.iyer@zorvyn.dev','Rohan Iyer','$2a$10$6kjM6vkE8p8C8WaTWVRSY.ugIqpb1zzNAyL9aHP4531H0W9/9HGXm','ANALYST',0),(10,'2026-04-05 18:34:33.865124','2026-04-05 18:34:33.865124','sana.khan@zorvyn.dev','Sana Khan','$2a$10$ILaCUi17EJxItPpKichq8uQU0HEST1PayRRpWjmf5vj4CyTMvIJpG','VIEWER',1);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-05 18:36:10
