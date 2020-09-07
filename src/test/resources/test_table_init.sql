-- 测试表
CREATE TABLE test.record
(
    uid        VARCHAR(5) NOT NULL,
    punch_date TIMESTAMP  NOT NULL,
    amount     INTEGER,
    username   VARCHAR,
    CONSTRAINT ROWKEY PRIMARY KEY (uid, punch_date)
);
-- 倒排索引
CREATE INDEX idx_record_username ON test.record(username);