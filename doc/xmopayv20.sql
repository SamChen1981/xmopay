/*
 Navicat Premium Data Transfer

 Source Server         : xmopay
 Source Server Type    : MySQL
 Source Server Version : 50715
 Source Host           : localhost
 Source Database       : xmopayv20

 Target Server Type    : MySQL
 Target Server Version : 50715
 File Encoding         : utf-8

 Date: 05/14/2018 22:53:20 PM
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `sys_admin_auth`
-- ----------------------------
DROP TABLE IF EXISTS `sys_admin_auth`;
CREATE TABLE `sys_admin_auth` (
  `ROLE_ID` int(8) NOT NULL COMMENT '角色ID',
  `MENU_ID` int(8) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`ROLE_ID`,`MENU_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='后台角色权限表';

-- ----------------------------
--  Table structure for `sys_admin_menus`
-- ----------------------------
DROP TABLE IF EXISTS `sys_admin_menus`;
CREATE TABLE `sys_admin_menus` (
  `MENU_ID` int(10) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `MENU_CODE` varchar(80) DEFAULT NULL COMMENT '菜单代码',
  `MENU_NAME` varchar(100) DEFAULT NULL COMMENT '菜单名称',
  `MENU_DESC` varchar(200) DEFAULT NULL COMMENT '菜单说明',
  `MENU_URL` varchar(200) DEFAULT NULL COMMENT '菜单地址',
  `MENU_PARENT` int(10) DEFAULT NULL COMMENT '上级菜单',
  `MENU_FLAG` int(10) DEFAULT NULL COMMENT '菜单标志',
  `DISPLAYORDER` int(8) DEFAULT NULL COMMENT '显示顺序',
  `STATUS` int(1) DEFAULT '1' COMMENT '常用菜单',
  `MENU_ICON` varchar(50) DEFAULT NULL COMMENT '菜单icon',
  `ROUTER_URI` varchar(200) DEFAULT '' COMMENT '路由路径',
  `MENU_TYPE` int(1) DEFAULT '0' COMMENT '菜单类型 0：主菜单 1：子菜单 2：操作Action',
  PRIMARY KEY (`MENU_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=141 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='后台菜单表';

-- ----------------------------
--  Table structure for `sys_admin_role`
-- ----------------------------
DROP TABLE IF EXISTS `sys_admin_role`;
CREATE TABLE `sys_admin_role` (
  `RID` int(8) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `ROLENAME` varchar(20) DEFAULT NULL COMMENT '角色名称',
  `ROLEDESC` varchar(200) DEFAULT NULL COMMENT '角色描述',
  `DATELINE` datetime DEFAULT NULL COMMENT '记录时间',
  PRIMARY KEY (`RID`),
  UNIQUE KEY `unique_rolename` (`ROLENAME`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='后台角色表';

-- ----------------------------
--  Table structure for `sys_admin_user`
-- ----------------------------
DROP TABLE IF EXISTS `sys_admin_user`;
CREATE TABLE `sys_admin_user` (
  `MUID` int(10) NOT NULL AUTO_INCREMENT COMMENT '管理员UID',
  `ROLEID` int(6) NOT NULL COMMENT '用户角色',
  `USERNAME` varchar(60) NOT NULL COMMENT '登录名',
  `PASSWORD` char(32) NOT NULL COMMENT '密码',
  `BINDIP` varchar(200) DEFAULT '0.0.0.0' COMMENT '绑定IP',
  `LASTIP` varchar(255) NOT NULL DEFAULT '0.0.0.0' COMMENT '最后一次登录IP',
  `LASTLOGIN` datetime NOT NULL COMMENT '最后一次登录时间',
  `SALTS` char(6) NOT NULL COMMENT '安全密匙',
  `STATUS` int(1) NOT NULL DEFAULT '0' COMMENT '用户状态 0 锁定 1 正常',
  `GOOGLECODE` text COMMENT 'google 身份验证码',
  `PARTNER_ID` varchar(32) DEFAULT '0' COMMENT 'PARTNER_ID',
  PRIMARY KEY (`MUID`),
  UNIQUE KEY `un_username` (`USERNAME`),
  KEY `NORMAL_INDEX` (`LASTLOGIN`,`USERNAME`)
) ENGINE=InnoDB AUTO_INCREMENT=84 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='后台管理员表';

-- ----------------------------
--  Table structure for `sys_settings`
-- ----------------------------
DROP TABLE IF EXISTS `sys_settings`;
CREATE TABLE `sys_settings` (
  `key` varchar(100) NOT NULL,
  `value` text,
  PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='系统配置表';

-- ----------------------------
--  Table structure for `wp_billings`
-- ----------------------------
DROP TABLE IF EXISTS `wp_billings`;
CREATE TABLE `wp_billings` (
  `BID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '帐务明细ID',
  `ORDER_SN` varchar(32) NOT NULL COMMENT '订单号',
  `BILLING_SN` varchar(32) DEFAULT NULL COMMENT '流水号',
  `BILL_TYPE` int(4) DEFAULT '0' COMMENT '账务类型：参见系统常量',
  `PARTNER_ID` varchar(32) DEFAULT NULL COMMENT '商户ID',
  `PARTNER_NAME` varchar(60) DEFAULT NULL COMMENT '商户名',
  `PAY_TYPE` varchar(20) NOT NULL DEFAULT '0' COMMENT '支付类型',
  `BANK_CODE` varchar(60) DEFAULT '0' COMMENT '支付代码',
  `TRADE_AMOUNT` decimal(16,2) NOT NULL DEFAULT '0.00' COMMENT '当笔交易金额',
  `TRADE_FEE` decimal(16,2) NOT NULL DEFAULT '0.00' COMMENT '交易手续费',
  `PAYMENT` decimal(16,2) NOT NULL DEFAULT '0.00' COMMENT '实付金额',
  `ACCOUNT_AMOUNT` decimal(16,2) NOT NULL DEFAULT '0.00' COMMENT '账户当前余额',
  `REMARK` varchar(200) DEFAULT NULL COMMENT '备注信息',
  `TRADE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `PAY_STATUS` int(1) NOT NULL DEFAULT '0' COMMENT '交易状态 -1=TRADE_FAILURE=交易失败 0=WATI_TRADE=交易中 1=TRADE_SUCCESS=交易成功 ',
  PRIMARY KEY (`BID`),
  UNIQUE KEY `UNIQUE_INDEX_PAYSN` (`ORDER_SN`),
  KEY `UN_INDEX_TRADE_TIME` (`TRADE_TIME`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='账单明细表';

-- ----------------------------
--  Table structure for `wp_gateway_agency`
-- ----------------------------
DROP TABLE IF EXISTS `wp_gateway_agency`;
CREATE TABLE `wp_gateway_agency` (
  `GAID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `AGENCY_CODE` varchar(48) NOT NULL COMMENT '机构代码（类名一一对应）',
  `AGENCY_NAME` varchar(60) DEFAULT NULL COMMENT '机构名称',
  `AGENCY_PARAMS` text COMMENT '其他参数:json格式',
  `AGENCY_STATUS` int(1) DEFAULT NULL COMMENT '开启状态 0关闭 1开启',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `DATELINE` datetime DEFAULT NULL COMMENT '添加时间',
  PRIMARY KEY (`GAID`),
  UNIQUE KEY `UN_AGENCY_CODE` (`AGENCY_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='网关机构表';

-- ----------------------------
--  Table structure for `wp_gateway_balance`
-- ----------------------------
DROP TABLE IF EXISTS `wp_gateway_balance`;
CREATE TABLE `wp_gateway_balance` (
  `GAID` int(10) NOT NULL AUTO_INCREMENT,
  `GATEWAY_TYPE` int(1) NOT NULL,
  `GATEWAY_CODE` varchar(60) NOT NULL,
  `GATEWAY_NAME` varchar(100) NOT NULL,
  `BALANCE` decimal(30,2) NOT NULL COMMENT '余额',
  `DATELINE` datetime NOT NULL,
  `MER_VIRAL_ACCT` varchar(50) NOT NULL,
  `BATCH_ID` varchar(48) DEFAULT NULL COMMENT '批次号',
  `LAST_TRADE_TIME` datetime DEFAULT NULL COMMENT '最后交易时间',
  PRIMARY KEY (`GAID`),
  KEY `in_dateline` (`DATELINE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='渠道余额表';

-- ----------------------------
--  Table structure for `wp_gateway_channel`
-- ----------------------------
DROP TABLE IF EXISTS `wp_gateway_channel`;
CREATE TABLE `wp_gateway_channel` (
  `CHANNEL_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `AGENCY_ID` int(8) NOT NULL DEFAULT '0' COMMENT '机构',
  `CHANNEL_TYPE` int(1) DEFAULT '0' COMMENT '网关类型 0＝支付网关 1=下发网关',
  `CHANNEL_CODE` varchar(60) NOT NULL COMMENT '渠道编码',
  `CHANNEL_NAME` varchar(100) DEFAULT NULL COMMENT '网关名称',
  `CHANNEL_KEY` varchar(100) DEFAULT NULL COMMENT '渠道商户号（上游颁发的）',
  `CHANNEL_SECRET` text COMMENT '渠道密钥',
  `CHANNEL_BALANCE` varchar(20) DEFAULT '0' COMMENT '上游渠道余额',
  `CHANNEL_PARAMS` text COMMENT '其他参数:json格式',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `DATELINE` datetime DEFAULT NULL COMMENT '添加时间',
  `STATUS` int(1) DEFAULT NULL COMMENT '开启状态 0关闭 1开启',
  PRIMARY KEY (`CHANNEL_ID`),
  UNIQUE KEY `un_channel_code` (`CHANNEL_CODE`),
  KEY `UN_UPDATE_TIME` (`UPDATE_TIME`)
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='网关渠道表';

-- ----------------------------
--  Table structure for `wp_message_queue`
-- ----------------------------
DROP TABLE IF EXISTS `wp_message_queue`;
CREATE TABLE `wp_message_queue` (
  `MQID` int(10) NOT NULL AUTO_INCREMENT,
  `PARTNER_ID` varchar(32) NOT NULL DEFAULT '0' COMMENT '商户ID',
  `ORDER_SN` varchar(48) NOT NULL DEFAULT '0' COMMENT '订单号',
  `MESSAGE_TOPIC` varchar(60) NOT NULL DEFAULT '0' COMMENT '消息topic',
  `MESSAGE_BODY` text NOT NULL COMMENT '消息内容',
  `MESSAGE_HOST` varchar(10) NOT NULL DEFAULT '0' COMMENT '分布主机',
  `NOTIFY_COUNT` int(2) DEFAULT '0' COMMENT '通知次数',
  `THREAD_STATUS` int(2) DEFAULT '0' COMMENT '线程状态：0=未处理 1=处理中',
  `CONSUMER_STATUS` int(2) DEFAULT '0' COMMENT '消费状态：0=未被消费（默认） 1=消费成功 2=消费处理中 -1=消费失败',
  `DATELINE` datetime NOT NULL,
  PRIMARY KEY (`MQID`),
  KEY `idx_DATELINE` (`DATELINE`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='消息队列表';

-- ----------------------------
--  Table structure for `wp_partners`
-- ----------------------------
DROP TABLE IF EXISTS `wp_partners`;
CREATE TABLE `wp_partners` (
  `PTID` int(10) NOT NULL AUTO_INCREMENT,
  `PARTNER_ID` varchar(32) DEFAULT '0' COMMENT '合作伙伴ID：普商户',
  `PARTNER_NAME` varchar(60) NOT NULL COMMENT '合作商户名',
  `PARTNER_INFO` text COMMENT '商户信息JSON',
  `XMOPAY_PRIVATE_KEY` text,
  `XMOPAY_PUBLIC_KEY` text COMMENT 'XMOPAY公钥',
  `PARTNER_PUBLIC_KEY` text COMMENT '商户公钥',
  `PARTNER_MD5_KEY` varchar(32) DEFAULT NULL COMMENT '商户MD5密钥',
  `PARTNER_AES_KEY` varchar(32) DEFAULT NULL COMMENT '商户AES密钥',
  `CREATE_TIME` datetime NOT NULL COMMENT '录入时间',
  `UPDATE_TIME` datetime NOT NULL COMMENT '更新时间',
  `API_WHITE_IP` text COMMENT '接口IP白名单',
  `API_STATUS` int(1) DEFAULT '0' COMMENT 'API权限状态：-2=总后台关闭 1=开通',
  `STATUS` int(1) NOT NULL DEFAULT '0' COMMENT '商户状态：-2=总后台关闭 1=开通',
  PRIMARY KEY (`PTID`),
  UNIQUE KEY `UN_PARTNER_ID` (`PARTNER_ID`),
  KEY `IN_CREATE_TIME` (`CREATE_TIME`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='合作商户表';

-- ----------------------------
--  Table structure for `wp_partners_account`
-- ----------------------------
DROP TABLE IF EXISTS `wp_partners_account`;
CREATE TABLE `wp_partners_account` (
  `PARTNER_ID` varchar(32) NOT NULL DEFAULT '0',
  `BALANCE` decimal(16,2) NOT NULL DEFAULT '0.00' COMMENT '账户金额',
  `FREEZE_BALANCE` decimal(16,2) NOT NULL DEFAULT '0.00' COMMENT '冻结金额',
  `HASH_CODE` varchar(8) NOT NULL,
  `LAST_TRADE` datetime DEFAULT NULL COMMENT '最后交易时间',
  `LAST_SIGN` varchar(32) DEFAULT NULL,
  UNIQUE KEY `UN_PARTNER_ID` (`PARTNER_ID`),
  KEY `IN_LAST_TRADE` (`LAST_TRADE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='商户账户表';

-- ----------------------------
--  Table structure for `wp_partners_product`
-- ----------------------------
DROP TABLE IF EXISTS `wp_partners_product`;
CREATE TABLE `wp_partners_product` (
  `PPID` int(10) NOT NULL AUTO_INCREMENT,
  `PARTNER_ID` varchar(24) NOT NULL COMMENT '商户ID',
  `PARTNER_NAME` varchar(48) DEFAULT NULL,
  `PRODUCT_TYPE` varchar(18) DEFAULT NULL COMMENT '支付类型',
  `CHANNEL_CODE` varchar(60) NOT NULL COMMENT '渠道编码',
  `CHANNEL_NAME` varchar(100) DEFAULT NULL COMMENT '渠道名称',
  `RATE` decimal(16,4) NOT NULL DEFAULT '0.0000' COMMENT '结算费率',
  `BANK_CODE` varchar(60) NOT NULL COMMENT '银行编码',
  `BANK_NAME` varchar(60) NOT NULL COMMENT '银行名称',
  `UPDATE_TIME` datetime NOT NULL COMMENT '更新时间',
  `CREATE_TIME` datetime NOT NULL COMMENT '创建时间',
  `STATUS` int(1) NOT NULL DEFAULT '0' COMMENT '产品状态：0=关闭 1=开通',
  PRIMARY KEY (`PPID`),
  UNIQUE KEY `un_partnerId_productCode_authTradeParam` (`PARTNER_ID`,`BANK_CODE`) USING BTREE,
  KEY `IN_CREATE_TIME` (`CREATE_TIME`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='商户产品表';

-- ----------------------------
--  Table structure for `wp_trade_order`
-- ----------------------------
DROP TABLE IF EXISTS `wp_trade_order`;
CREATE TABLE `wp_trade_order` (
  `TOID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `TRAN_TYPE` int(4) DEFAULT '0' COMMENT '交易类型：参见系统常量',
  `ORDER_SN` varchar(32) DEFAULT NULL COMMENT '订单号',
  `ORDER_HASH` varchar(32) DEFAULT NULL COMMENT '订单号哈希值',
  `BILLING_SN` varchar(32) DEFAULT NULL COMMENT '流水号',
  `PARTNER_ID` varchar(32) DEFAULT NULL COMMENT '用户ID',
  `PARTNER_NAME` varchar(60) DEFAULT NULL COMMENT '用户名',
  `PAY_TYPE` varchar(20) DEFAULT NULL COMMENT '支付方式',
  `BANK_CODE` varchar(60) DEFAULT NULL COMMENT '银行编码[我方]',
  `CHANNEL_CODE` varchar(60) DEFAULT NULL COMMENT '渠道代码',
  `ORDER_TITLE` varchar(32) DEFAULT NULL COMMENT '订单名称',
  `ORDER_AMOUNT` decimal(16,2) DEFAULT NULL COMMENT '支付金额',
  `ORDER_TIME` datetime NOT NULL COMMENT '平台订单时间',
  `TRADE_TIME` datetime DEFAULT NULL COMMENT '付款时间',
  `FINISH_TIME` datetime DEFAULT NULL COMMENT '交易完成时间',
  `TRADE_IP` varchar(128) DEFAULT NULL COMMENT '交易IP',
  `TRADE_HASH` varchar(32) DEFAULT NULL COMMENT 'COOKIE HASH码',
  `NOTIFY_URL` varchar(250) DEFAULT NULL COMMENT '服务器异步通知页面路径',
  `RETURN_URL` varchar(250) DEFAULT NULL COMMENT '通知商户地址(页面重定向地址)',
  `EXTEND_PARAM` varchar(60) DEFAULT NULL COMMENT '扩展字段',
  `OUT_TRADE_NO` varchar(48) DEFAULT NULL COMMENT '商户提交唯一订单号',
  `OUT_TRADE_TIME` datetime DEFAULT NULL COMMENT '商户订单提交时间',
  `ORDER_SIGN` varchar(32) NOT NULL COMMENT '订单摘要',
  `SIGN_TYPE` varchar(10) DEFAULT NULL COMMENT '加密方式',
  `VERSION` varchar(10) NOT NULL DEFAULT 'V4.0.1' COMMENT '接口版本号',
  `REMARK` varchar(200) DEFAULT NULL COMMENT '订单备注',
  `ORDER_STATUS` int(1) DEFAULT '0' COMMENT '订单状态: 0=WAIT_TO_BANK-未提交到银行;   1=TRADE_SUCCESS-交易成功; 2=WAIT_BANK_HANDLE-已提交待处理; -1=TRADE_FAILURE-交易失败  -9=TRADE_EXCEPTION - 风控订单; -2=TRADE_CLOSE - 订单自动关闭; -3=TRADE_REFUND-订单已退款',
  PRIMARY KEY (`TOID`),
  UNIQUE KEY `ind_trade_paysn_unique` (`ORDER_SN`),
  UNIQUE KEY `un_out_trade_no` (`OUT_TRADE_NO`),
  KEY `in_order_time` (`ORDER_TIME`)
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='交易明细表';

-- ----------------------------
--  Table structure for `wp_trade_refund`
-- ----------------------------
DROP TABLE IF EXISTS `wp_trade_refund`;
CREATE TABLE `wp_trade_refund` (
  `TOID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `REFUND_TYPE` int(4) DEFAULT '0' COMMENT '交易类型：参见系统常量',
  `ORDER_SN` varchar(32) DEFAULT NULL COMMENT '订单号',
  `BILLING_SN` varchar(32) DEFAULT NULL COMMENT '流水号',
  `PARTNER_ID` varchar(32) DEFAULT NULL COMMENT '用户ID',
  `PARTNER_NAME` varchar(60) DEFAULT NULL COMMENT '用户名',
  `ORDER_AMOUNT` decimal(16,2) DEFAULT '0.00' COMMENT '支付金额',
  `UPDATE_TIME` datetime NOT NULL COMMENT '更新时间',
  `DATELINE` datetime DEFAULT NULL COMMENT '创建时间',
  `OUT_TRADE_NO` varchar(48) DEFAULT NULL COMMENT '退款原订单号',
  `REMARK` varchar(200) DEFAULT NULL COMMENT '订单备注',
  `REFUND_STATUS` int(2) DEFAULT '0' COMMENT '退款状态：0-默认状态，1-退款成功(REFUND_SUCCESS)，2-退款审核(REFUND_APPROVE)，3-退款拒绝(REFUND_REFUSE)，-1 退款失败(REFUND_FAILURE)',
  PRIMARY KEY (`TOID`),
  UNIQUE KEY `un_out_trade_no` (`OUT_TRADE_NO`),
  UNIQUE KEY `ind_order_sn_unique` (`ORDER_SN`),
  KEY `in_dateline` (`DATELINE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='退款订单表';

SET FOREIGN_KEY_CHECKS = 1;
