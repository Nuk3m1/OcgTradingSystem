create database if not exists OCGtradingSystem;

use OCGtradingSystem;
-- 卡牌表
CREATE TABLE if not exists cards (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       seller_id BIGINT,        -- 卖家ID
                       name varchar(256),       -- 卡牌名称
                       card_number VARCHAR(20), -- 卡牌编号
                       card_graph varchar(256), -- 卡图地址
                       rarity VARCHAR(5),       -- 稀有度
                       price DECIMAL(10,2),     -- 价格
                       attention BIGINT DEFAULT 0,       -- 关注量
                       status TINYINT DEFAULT 3 -- 状态（1上架,0下架,2已售出,3待审核）
)AUTO_INCREMENT = 100;
ALTER TABLE cards
    MODIFY COLUMN id BIGINT AUTO_INCREMENT;
-- 用户表
CREATE TABLE if not exists users(
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      account VARCHAR(256) , -- 用户账号
                      password VARCHAR(256),-- 用户密码4
                      type ENUM('USER' , 'ADMIN') DEFAULT 'USER' -- 用户类型


)AUTO_INCREMENT = 100;

-- 帖子表
CREATE TABLE if not exists post(
                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                     seller_id BIGINT , -- 收信方id
                     buyer_id BIGINT , -- 写信方id
                     message TEXT , -- 帖子内容
                     creat_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP

)AUTO_INCREMENT = 100;

-- 用户收藏表
CREATE TABLE if not exists user_favorite (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               user_id BIGINT,
                               card_id BIGINT
)AUTO_INCREMENT = 100;
-- 用户黑名单
CREATE TABLE if not exists user_banned (
    id BIGINT PRIMARY KEY AUTO_INCREMENT, -- 举报信息id
    ban_user BIGINT , -- 用户id
    status TINYINT DEFAULT 0 -- 状态(0待审核,1通过,2未通过)
)AUTO_INCREMENT = 100;
INSERT INTO users (account,password,type) VALUES ('Admin','Ee123456','ADMIN')


