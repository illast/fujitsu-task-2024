-- DROP TABLE IF EXISTS station;


CREATE TABLE IF NOT EXISTS station (
   id INT AUTO_INCREMENT PRIMARY KEY,
   name VARCHAR(255),
   wmo_code INT,
   air_temperature DOUBLE,
   wind_speed DOUBLE,
   phenomenon VARCHAR(255),
   timestamp TIMESTAMP
);