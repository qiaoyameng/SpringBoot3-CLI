USE `example`;

DROP TABLE IF EXISTS `live_room`;
CREATE TABLE IF NOT EXISTS `live_room`
(
    `id`               BIGINT UNSIGNED AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
    `room_name`        VARCHAR(200)     DEFAULT NULL COMMENT '直播间名称',
    `room_desc`        VARCHAR(1000)    DEFAULT NULL COMMENT '直播间描述',
    `cover_url`        VARCHAR(500)     DEFAULT NULL COMMENT '封面图片URL',
    `streamer_id`      BIGINT UNSIGNED  DEFAULT NULL COMMENT '主播ID',
    `streamer_name`    VARCHAR(100)     DEFAULT NULL COMMENT '主播名称',
    `status`           TINYINT UNSIGNED DEFAULT 0 COMMENT '状态：0-未开播，1-直播中，2-已结束',
    `start_time`       DATETIME         DEFAULT NULL COMMENT '开始时间',
    `end_time`         DATETIME         DEFAULT NULL COMMENT '结束时间',
    `total_viewers`    INT UNSIGNED     DEFAULT 0 COMMENT '总观看人数',
    `peak_viewers`     INT UNSIGNED     DEFAULT 0 COMMENT '峰值观看人数',
    `total_orders`     INT UNSIGNED     DEFAULT 0 COMMENT '总订单数',
    `total_sales`      DECIMAL(18, 2)   DEFAULT 0.00 COMMENT '总销售额',
    `creator_id`       BIGINT UNSIGNED  DEFAULT NULL COMMENT '创建者ID',
    `create_time`      DATETIME         DEFAULT NULL COMMENT '创建时间',
    `updater_id`       BIGINT UNSIGNED  DEFAULT NULL COMMENT '更新者ID',
    `update_time`      DATETIME         DEFAULT NULL COMMENT '更新时间',
    `version`          TINYINT UNSIGNED DEFAULT NULL COMMENT 'MP乐观锁版本',
    `is_deleted`       TINYINT UNSIGNED DEFAULT 0 COMMENT 'MP逻辑删除字段，0或1'
) COMMENT '直播间表' COLLATE = utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `product`;
CREATE TABLE IF NOT EXISTS `product`
(
    `id`               BIGINT UNSIGNED AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
    `product_name`     VARCHAR(200)     DEFAULT NULL COMMENT '商品名称',
    `product_desc`     VARCHAR(1000)    DEFAULT NULL COMMENT '商品描述',
    `selling_points`   VARCHAR(2000)    DEFAULT NULL COMMENT '商品卖点，JSON格式存储',
    `cover_url`        VARCHAR(500)     DEFAULT NULL COMMENT '商品封面URL',
    `price`            DECIMAL(18, 2)   DEFAULT NULL COMMENT '商品价格',
    `original_price`   DECIMAL(18, 2)   DEFAULT NULL COMMENT '原价',
    `stock`            INT UNSIGNED     DEFAULT 0 COMMENT '库存',
    `category`         VARCHAR(100)     DEFAULT NULL COMMENT '商品分类',
    `status`           TINYINT UNSIGNED DEFAULT 1 COMMENT '状态：0-下架，1-上架',
    `creator_id`       BIGINT UNSIGNED  DEFAULT NULL COMMENT '创建者ID',
    `create_time`      DATETIME         DEFAULT NULL COMMENT '创建时间',
    `updater_id`       BIGINT UNSIGNED  DEFAULT NULL COMMENT '更新者ID',
    `update_time`      DATETIME         DEFAULT NULL COMMENT '更新时间',
    `version`          TINYINT UNSIGNED DEFAULT NULL COMMENT 'MP乐观锁版本',
    `is_deleted`       TINYINT UNSIGNED DEFAULT 0 COMMENT 'MP逻辑删除字段，0或1'
) COMMENT '商品表' COLLATE = utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `live_room_product`;
CREATE TABLE IF NOT EXISTS `live_room_product`
(
    `id`               BIGINT UNSIGNED AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
    `live_room_id`     BIGINT UNSIGNED  DEFAULT NULL COMMENT '直播间ID',
    `product_id`       BIGINT UNSIGNED  DEFAULT NULL COMMENT '商品ID',
    `is_explaining`    TINYINT UNSIGNED DEFAULT 0 COMMENT '是否正在讲解：0-否，1-是',
    `sort_order`       INT              DEFAULT 0 COMMENT '排序顺序',
    `sales_count`      INT UNSIGNED     DEFAULT 0 COMMENT '该直播间该商品销量',
    `sales_amount`     DECIMAL(18, 2)   DEFAULT 0.00 COMMENT '该直播间该商品销售额',
    `creator_id`       BIGINT UNSIGNED  DEFAULT NULL COMMENT '创建者ID',
    `create_time`      DATETIME         DEFAULT NULL COMMENT '创建时间',
    `updater_id`       BIGINT UNSIGNED  DEFAULT NULL COMMENT '更新者ID',
    `update_time`      DATETIME         DEFAULT NULL COMMENT '更新时间',
    `version`          TINYINT UNSIGNED DEFAULT NULL COMMENT 'MP乐观锁版本',
    `is_deleted`       TINYINT UNSIGNED DEFAULT 0 COMMENT 'MP逻辑删除字段，0或1',
    INDEX `idx_live_room_id` (`live_room_id`),
    INDEX `idx_product_id` (`product_id`)
) COMMENT '直播间商品关联表' COLLATE = utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `live_order`;
CREATE TABLE IF NOT EXISTS `live_order`
(
    `id`               BIGINT UNSIGNED AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
    `order_no`         VARCHAR(64)      DEFAULT NULL COMMENT '订单号',
    `live_room_id`     BIGINT UNSIGNED  DEFAULT NULL COMMENT '直播间ID',
    `product_id`       BIGINT UNSIGNED  DEFAULT NULL COMMENT '商品ID',
    `user_id`          BIGINT UNSIGNED  DEFAULT NULL COMMENT '用户ID',
    `product_name`     VARCHAR(200)     DEFAULT NULL COMMENT '商品名称快照',
    `product_price`    DECIMAL(18, 2)   DEFAULT NULL COMMENT '商品价格快照',
    `quantity`         INT UNSIGNED     DEFAULT 1 COMMENT '购买数量',
    `total_amount`     DECIMAL(18, 2)   DEFAULT NULL COMMENT '订单总金额',
    `status`           TINYINT UNSIGNED DEFAULT 0 COMMENT '状态：0-待支付，1-已支付，2-已取消',
    `pay_time`         DATETIME         DEFAULT NULL COMMENT '支付时间',
    `creator_id`       BIGINT UNSIGNED  DEFAULT NULL COMMENT '创建者ID',
    `create_time`      DATETIME         DEFAULT NULL COMMENT '创建时间',
    `updater_id`       BIGINT UNSIGNED  DEFAULT NULL COMMENT '更新者ID',
    `update_time`      DATETIME         DEFAULT NULL COMMENT '更新时间',
    `version`          TINYINT UNSIGNED DEFAULT NULL COMMENT 'MP乐观锁版本',
    `is_deleted`       TINYINT UNSIGNED DEFAULT 0 COMMENT 'MP逻辑删除字段，0或1',
    UNIQUE KEY `uk_order_no` (`order_no`),
    INDEX `idx_live_room_id` (`live_room_id`),
    INDEX `idx_product_id` (`product_id`),
    INDEX `idx_user_id` (`user_id`)
) COMMENT '直播间订单表' COLLATE = utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `audience_record`;
CREATE TABLE IF NOT EXISTS `audience_record`
(
    `id`               BIGINT UNSIGNED AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
    `live_room_id`     BIGINT UNSIGNED  DEFAULT NULL COMMENT '直播间ID',
    `user_id`          BIGINT UNSIGNED  DEFAULT NULL COMMENT '用户ID',
    `join_time`        DATETIME         DEFAULT NULL COMMENT '进入时间',
    `leave_time`       DATETIME         DEFAULT NULL COMMENT '离开时间',
    `duration`         INT UNSIGNED     DEFAULT 0 COMMENT '停留时长(秒)',
    `is_converted`     TINYINT UNSIGNED DEFAULT 0 COMMENT '是否转化下单：0-否，1-是',
    `creator_id`       BIGINT UNSIGNED  DEFAULT NULL COMMENT '创建者ID',
    `create_time`      DATETIME         DEFAULT NULL COMMENT '创建时间',
    `updater_id`       BIGINT UNSIGNED  DEFAULT NULL COMMENT '更新者ID',
    `update_time`      DATETIME         DEFAULT NULL COMMENT '更新时间',
    `version`          TINYINT UNSIGNED DEFAULT NULL COMMENT 'MP乐观锁版本',
    `is_deleted`       TINYINT UNSIGNED DEFAULT 0 COMMENT 'MP逻辑删除字段，0或1',
    INDEX `idx_live_room_id` (`live_room_id`),
    INDEX `idx_user_id` (`user_id`)
) COMMENT '观众记录表' COLLATE = utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `viewers_stats_minute`;
CREATE TABLE IF NOT EXISTS `viewers_stats_minute`
(
    `id`               BIGINT UNSIGNED AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
    `live_room_id`     BIGINT UNSIGNED  DEFAULT NULL COMMENT '直播间ID',
    `stats_time`       DATETIME         DEFAULT NULL COMMENT '统计时间点',
    `viewers_count`    INT UNSIGNED     DEFAULT 0 COMMENT '当前观看人数',
    `creator_id`       BIGINT UNSIGNED  DEFAULT NULL COMMENT '创建者ID',
    `create_time`      DATETIME         DEFAULT NULL COMMENT '创建时间',
    `updater_id`       BIGINT UNSIGNED  DEFAULT NULL COMMENT '更新者ID',
    `update_time`      DATETIME         DEFAULT NULL COMMENT '更新时间',
    `version`          TINYINT UNSIGNED DEFAULT NULL COMMENT 'MP乐观锁版本',
    `is_deleted`       TINYINT UNSIGNED DEFAULT 0 COMMENT 'MP逻辑删除字段，0或1',
    INDEX `idx_live_room_id` (`live_room_id`),
    INDEX `idx_stats_time` (`stats_time`)
) COMMENT '每分钟观看人数统计表' COLLATE = utf8mb4_unicode_ci;
