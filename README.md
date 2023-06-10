# leaf-mini
分布式id
### db 配置
```sql
CREATE DATABASE leaf
CREATE TABLE `leaf_alloc` (
  `biz_tag` varchar(128)  NOT NULL DEFAULT '' COMMENT '业务标识',
  `max_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '最大ID',
  `step` int(11) NOT NULL COMMENT 'ID增长步长',
  `description` varchar(256)  DEFAULT NULL COMMENT '描述',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`biz_tag`)
) ENGINE=InnoDB COMMENT 'ID分配表';

INSERT INTO leaf_alloc(biz_tag, max_id, step, description) values('leaf-segment-test', 1, 2000, 'Test leaf Segment Mode Get Id')
```