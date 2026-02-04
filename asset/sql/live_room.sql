-- 直播间管理相关表结构

USE `example`;

-- 直播间表
DROP TABLE IF EXISTS `live_room`;
CREATE TABLE IF NOT EXISTS `live_room`
(
    `id`              BIGINT UNSIGNED AUTO_INCREMENT COMMENT '直播间ID' PRIMARY KEY,
    `title`           VARCHAR(200)     NOT NULL COMMENT '直播间标题',
    `description`     VARCHAR(500)     DEFAULT NULL COMMENT '直播间简介',
    `cover_image`     VARCHAR(500)     DEFAULT NULL COMMENT '直播间封面图URL',
    `anchor_id`       BIGINT UNSIGNED  NOT NULL COMMENT '主播ID',
    `anchor_name`     VARCHAR(100)     DEFAULT NULL COMMENT '主播名称',
    `status`          TINYINT UNSIGNED DEFAULT 0 COMMENT '直播状态：0-未开始，1-直播中，2-已结束，3-暂停',
    `start_time`      DATETIME         DEFAULT NULL COMMENT '直播开始时间',
    `end_time`        DATETIME         DEFAULT NULL COMMENT '直播结束时间',
    `current_product_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '当前讲解商品ID',
    `viewer_count`    INT UNSIGNED     DEFAULT 0 COMMENT '当前观看人数',
    `total_viewer_count` INT UNSIGNED  DEFAULT 0 COMMENT '总观看人数',
    `order_count`     INT UNSIGNED     DEFAULT 0 COMMENT '订单数',
    `sales_amount`    DECIMAL(15, 2)   DEFAULT 0.00 COMMENT '销售额',
    `creator_id`      BIGINT UNSIGNED  DEFAULT NULL COMMENT '创建者ID',
    `create_time`     DATETIME         DEFAULT NULL COMMENT '创建时间',
    `updater_id`      BIGINT UNSIGNED  DEFAULT NULL COMMENT '更新者ID',
    `update_time`     DATETIME         DEFAULT NULL COMMENT '更新时间',
    `version`         TINYINT UNSIGNED DEFAULT NULL COMMENT 'MP乐观锁版本',
    `is_deleted`      TINYINT UNSIGNED DEFAULT NULL COMMENT 'MP逻辑删除字段，0 或 1'
) COMMENT '直播间表' COLLATE = utf8mb4_unicode_ci;

-- 直播间商品关联表
DROP TABLE IF EXISTS `live_room_product`;
CREATE TABLE IF NOT EXISTS `live_room_product`
(
    `id`              BIGINT UNSIGNED AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
    `live_room_id`    BIGINT UNSIGNED  NOT NULL COMMENT '直播间ID',
    `product_id`      BIGINT UNSIGNED  NOT NULL COMMENT '商品ID',
    `product_name`    VARCHAR(200)     NOT NULL COMMENT '商品名称',
    `product_image`   VARCHAR(500)     DEFAULT NULL COMMENT '商品图片URL',
    `product_price`   DECIMAL(10, 2)   NOT NULL COMMENT '商品价格',
    `original_price`  DECIMAL(10, 2)   DEFAULT NULL COMMENT '商品原价',
    `selling_points`  VARCHAR(1000)    DEFAULT NULL COMMENT '商品卖点，JSON格式存储',
    `stock`           INT UNSIGNED     DEFAULT 0 COMMENT '库存',
    `sort_order`      INT              DEFAULT 0 COMMENT '排序',
    `status`          TINYINT UNSIGNED DEFAULT 1 COMMENT '状态：0-下架，1-上架',
    `sales_count`     INT UNSIGNED     DEFAULT 0 COMMENT '销量',
    `creator_id`      BIGINT UNSIGNED  DEFAULT NULL COMMENT '创建者ID',
    `create_time`     DATETIME         DEFAULT NULL COMMENT '创建时间',
    `updater_id`      BIGINT UNSIGNED  DEFAULT NULL COMMENT '更新者ID',
    `update_time`     DATETIME         DEFAULT NULL COMMENT '更新时间',
    `version`         TINYINT UNSIGNED DEFAULT NULL COMMENT 'MP乐观锁版本',
    `is_deleted`      TINYINT UNSIGNED DEFAULT NULL COMMENT 'MP逻辑删除字段，0 或 1'
) COMMENT '直播间商品关联表' COLLATE = utf8mb4_unicode_ci;

-- 直播间订单表
DROP TABLE IF EXISTS `live_order`;
CREATE TABLE IF NOT EXISTS `live_order`
(
    `id`              BIGINT UNSIGNED AUTO_INCREMENT COMMENT '订单ID' PRIMARY KEY,
    `order_no`        VARCHAR(64)      NOT NULL UNIQUE COMMENT '订单编号',
    `live_room_id`    BIGINT UNSIGNED  NOT NULL COMMENT '直播间ID',
    `product_id`      BIGINT UNSIGNED  NOT NULL COMMENT '商品ID',
    `user_id`         BIGINT UNSIGNED  NOT NULL COMMENT '用户ID',
    `quantity`        INT UNSIGNED     DEFAULT 1 COMMENT '购买数量',
    `unit_price`      DECIMAL(10, 2)   NOT NULL COMMENT '单价',
    `total_amount`    DECIMAL(15, 2)   NOT NULL COMMENT '订单总金额',
    `status`          TINYINT UNSIGNED DEFAULT 0 COMMENT '订单状态：0-待支付，1-已支付，2-已发货，3-已完成，4-已取消',
    `pay_time`        DATETIME         DEFAULT NULL COMMENT '支付时间',
    `creator_id`      BIGINT UNSIGNED  DEFAULT NULL COMMENT '创建者ID',
    `create_time`     DATETIME         DEFAULT NULL COMMENT '创建时间',
    `updater_id`      BIGINT UNSIGNED  DEFAULT NULL COMMENT '更新者ID',
    `update_time`     DATETIME         DEFAULT NULL COMMENT '更新时间',
    `version`         TINYINT UNSIGNED DEFAULT NULL COMMENT 'MP乐观锁版本',
    `is_deleted`      TINYINT UNSIGNED DEFAULT NULL COMMENT 'MP逻辑删除字段，0 或 1'
) COMMENT '直播间订单表' COLLATE = utf8mb4_unicode_ci;

-- 直播间观众记录表
DROP TABLE IF EXISTS `live_viewer`;
CREATE TABLE IF NOT EXISTS `live_viewer`
(
    `id`              BIGINT UNSIGNED AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
    `live_room_id`    BIGINT UNSIGNED  NOT NULL COMMENT '直播间ID',
    `user_id`         BIGINT UNSIGNED  DEFAULT NULL COMMENT '用户ID（未登录为null）',
    `session_id`      VARCHAR(64)      NOT NULL COMMENT '会话ID',
    `enter_time`      DATETIME         NOT NULL COMMENT '进入时间',
    `leave_time`      DATETIME         DEFAULT NULL COMMENT '离开时间',
    `duration`        INT UNSIGNED     DEFAULT 0 COMMENT '观看时长（秒）',
    `ip_address`      VARCHAR(50)      DEFAULT NULL COMMENT 'IP地址',
    `creator_id`      BIGINT UNSIGNED  DEFAULT NULL COMMENT '创建者ID',
    `create_time`     DATETIME         DEFAULT NULL COMMENT '创建时间',
    `updater_id`      BIGINT UNSIGNED  DEFAULT NULL COMMENT '更新者ID',
    `update_time`     DATETIME         DEFAULT NULL COMMENT '更新时间',
    `version`         TINYINT UNSIGNED DEFAULT NULL COMMENT 'MP乐观锁版本',
    `is_deleted`      TINYINT UNSIGNED DEFAULT NULL COMMENT 'MP逻辑删除字段，0 或 1'
) COMMENT '直播间观众记录表' COLLATE = utf8mb4_unicode_ci;

-- 直播间数据统计表（按小时统计）
DROP TABLE IF EXISTS `live_room_stats`;
CREATE TABLE IF NOT EXISTS `live_room_stats`
(
    `id`              BIGINT UNSIGNED AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
    `live_room_id`    BIGINT UNSIGNED  NOT NULL COMMENT '直播间ID',
    `stats_hour`      DATETIME         NOT NULL COMMENT '统计小时',
    `viewer_count`    INT UNSIGNED     DEFAULT 0 COMMENT '观看人数',
    `new_viewer_count` INT UNSIGNED    DEFAULT 0 COMMENT '新增观看人数',
    `order_count`     INT UNSIGNED     DEFAULT 0 COMMENT '订单数',
    `sales_amount`    DECIMAL(15, 2)   DEFAULT 0.00 COMMENT '销售额',
    `comment_count`   INT UNSIGNED     DEFAULT 0 COMMENT '评论数',
    `like_count`      INT UNSIGNED     DEFAULT 0 COMMENT '点赞数',
    `creator_id`      BIGINT UNSIGNED  DEFAULT NULL COMMENT '创建者ID',
    `create_time`     DATETIME         DEFAULT NULL COMMENT '创建时间',
    `updater_id`      BIGINT UNSIGNED  DEFAULT NULL COMMENT '更新者ID',
    `update_time`     DATETIME         DEFAULT NULL COMMENT '更新时间',
    `version`         TINYINT UNSIGNED DEFAULT NULL COMMENT 'MP乐观锁版本',
    `is_deleted`      TINYINT UNSIGNED DEFAULT NULL COMMENT 'MP逻辑删除字段，0 或 1'
) COMMENT '直播间数据统计表' COLLATE = utf8mb4_unicode_ci;

-- 创建索引
CREATE INDEX idx_live_room_status ON live_room(status);
CREATE INDEX idx_live_room_anchor_id ON live_room(anchor_id);
CREATE INDEX idx_live_room_product_live_room_id ON live_room_product(live_room_id);
CREATE INDEX idx_live_room_product_product_id ON live_room_product(product_id);
CREATE INDEX idx_live_order_live_room_id ON live_order(live_room_id);
CREATE INDEX idx_live_order_product_id ON live_order(product_id);
CREATE INDEX idx_live_order_user_id ON live_order(user_id);
CREATE INDEX idx_live_order_create_time ON live_order(create_time);
CREATE INDEX idx_live_viewer_live_room_id ON live_viewer(live_room_id);
CREATE INDEX idx_live_viewer_enter_time ON live_viewer(enter_time);
CREATE INDEX idx_live_room_stats_live_room_id ON live_room_stats(live_room_id);
CREATE INDEX idx_live_room_stats_hour ON live_room_stats(stats_hour);
