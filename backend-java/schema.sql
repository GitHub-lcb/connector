-- Create database
CREATE DATABASE IF NOT EXISTS connector;
USE connector;

-- Create routes table
CREATE TABLE IF NOT EXISTS routes (
  id VARCHAR(36) PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  source_path VARCHAR(255) NOT NULL UNIQUE,
  target_url VARCHAR(1024) NOT NULL,
  method VARCHAR(10) NOT NULL,
  mapping_config JSON,
  status VARCHAR(20) DEFAULT 'active',
  version INT DEFAULT 1,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create route_logs table
CREATE TABLE IF NOT EXISTS route_logs (
  id VARCHAR(36) PRIMARY KEY,
  route_id VARCHAR(36),
  request_path VARCHAR(255) NOT NULL,
  status_code INT,
  latency_ms INT,
  error_msg TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE SET NULL
);

-- Create config_history table
CREATE TABLE IF NOT EXISTS config_history (
  id VARCHAR(36) PRIMARY KEY,
  route_id VARCHAR(36),
  old_config JSON,
  new_config JSON,
  changed_by VARCHAR(36),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE
);
