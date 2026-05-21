-- ----------------------------
-- 问诊管理模块 - 数据库表结构
-- ----------------------------

-- ----------------------------
-- 1. 问诊单表
-- ----------------------------
DROP TABLE IF EXISTS t_consultation;
CREATE TABLE t_consultation (
  consultation_id   BIGINT(20)    NOT NULL AUTO_INCREMENT COMMENT '问诊单ID',
  patient_id        BIGINT(20)    NOT NULL                COMMENT '病人用户ID',
  doctor_id         BIGINT(20)    DEFAULT NULL            COMMENT '医生用户ID',
  room_name         VARCHAR(64)   DEFAULT ''              COMMENT 'LiveKit房间名',
  status            CHAR(1)       DEFAULT '0'             COMMENT '问诊状态（0=待接诊 1=进行中 2=已结束 3=已取消）',
  title             VARCHAR(200)  DEFAULT ''              COMMENT '问诊标题/主诉',
  description       TEXT          DEFAULT NULL            COMMENT '病情描述',
  start_time        DATETIME      DEFAULT NULL            COMMENT '实际开始时间',
  end_time          DATETIME      DEFAULT NULL            COMMENT '实际结束时间',
  create_by         VARCHAR(64)   DEFAULT ''              COMMENT '创建者',
  create_time       DATETIME      DEFAULT NULL            COMMENT '创建时间',
  update_by         VARCHAR(64)   DEFAULT ''              COMMENT '更新者',
  update_time       DATETIME      DEFAULT NULL            COMMENT '更新时间',
  remark            VARCHAR(500)  DEFAULT NULL            COMMENT '备注',
  del_flag          CHAR(1)       DEFAULT '0'             COMMENT '删除标志（0=正常 1=删除）',
  PRIMARY KEY (consultation_id),
  KEY idx_patient_id (patient_id),
  KEY idx_doctor_id (doctor_id),
  KEY idx_room_name (room_name)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='问诊单表';

-- ----------------------------
-- 2. 问诊参与者表
-- ----------------------------
DROP TABLE IF EXISTS t_consultation_participant;
CREATE TABLE t_consultation_participant (
  participant_id    BIGINT(20)    NOT NULL AUTO_INCREMENT COMMENT '参与者ID',
  consultation_id   BIGINT(20)    NOT NULL                COMMENT '关联问诊单ID',
  user_id           BIGINT(20)    NOT NULL                COMMENT '用户ID',
  user_type         CHAR(1)       DEFAULT '1'             COMMENT '用户类型（1=医生 2=病人 3=AI Agent）',
  join_time         DATETIME      DEFAULT NULL            COMMENT '加入房间时间',
  leave_time        DATETIME      DEFAULT NULL            COMMENT '离开房间时间',
  livekit_identity  VARCHAR(64)   DEFAULT ''              COMMENT 'LiveKit参与者标识',
  status            CHAR(1)       DEFAULT '0'             COMMENT '状态（0=已邀请 1=已加入 2=已离开）',
  create_time       DATETIME      DEFAULT NULL            COMMENT '创建时间',
  PRIMARY KEY (participant_id),
  KEY idx_consultation_id (consultation_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='问诊参与者表';

-- ----------------------------
-- 3. 问诊消息记录表
-- ----------------------------
DROP TABLE IF EXISTS t_consultation_message;
CREATE TABLE t_consultation_message (
  message_id        BIGINT(20)    NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  consultation_id   BIGINT(20)    NOT NULL                COMMENT '关联问诊单ID',
  sender_id         BIGINT(20)    DEFAULT NULL            COMMENT '发送者用户ID',
  sender_type       CHAR(1)       DEFAULT '1'             COMMENT '发送者类型（1=医生 2=病人 3=AI Agent）',
  message_type      CHAR(1)       DEFAULT '1'             COMMENT '消息类型（1=文本 2=图片 3=系统通知）',
  content           TEXT          DEFAULT NULL            COMMENT '消息内容',
  create_time       DATETIME      DEFAULT NULL            COMMENT '发送时间',
  PRIMARY KEY (message_id),
  KEY idx_consultation_id (consultation_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='问诊消息记录表';

-- ----------------------------
-- 4. 问诊管理菜单（sys_menu）
-- ----------------------------

-- 一级菜单：问诊管理
INSERT INTO sys_menu VALUES (2000, '问诊管理', 0, 4, 'consultation', null, '', '', 1, 0, 'M', '0', '0', '', 'peoples', 'admin', sysdate(), '', null, '问诊管理目录');

-- 二级菜单：问诊列表
INSERT INTO sys_menu VALUES (2001, '问诊列表', 2000, 1, 'list', 'consultation/list', '', '', 1, 0, 'C', '0', '0', 'consultation:consultation:list', 'list', 'admin', sysdate(), '', null, '问诊列表菜单');

-- 问诊列表按钮权限
INSERT INTO sys_menu VALUES (2002, '问诊查询', 2001, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'consultation:consultation:query', '#', 'admin', sysdate(), '', null, '');
INSERT INTO sys_menu VALUES (2003, '问诊新增', 2001, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'consultation:consultation:add', '#', 'admin', sysdate(), '', null, '');
INSERT INTO sys_menu VALUES (2004, '问诊修改', 2001, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'consultation:consultation:edit', '#', 'admin', sysdate(), '', null, '');
INSERT INTO sys_menu VALUES (2005, '问诊删除', 2001, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'consultation:consultation:remove', '#', 'admin', sysdate(), '', null, '');
INSERT INTO sys_menu VALUES (2006, '获取Token', 2001, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'consultation:consultation:token', '#', 'admin', sysdate(), '', null, '');

-- 二级菜单：消息记录
INSERT INTO sys_menu VALUES (2010, '消息记录', 2000, 2, 'message', 'consultation/message', '', '', 1, 0, 'C', '0', '0', 'consultation:message:list', 'message', 'admin', sysdate(), '', null, '消息记录菜单');

-- 消息记录按钮权限
INSERT INTO sys_menu VALUES (2011, '消息查询', 2010, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'consultation:message:list', '#', 'admin', sysdate(), '', null, '');
INSERT INTO sys_menu VALUES (2012, '消息发送', 2010, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'consultation:message:add', '#', 'admin', sysdate(), '', null, '');

-- 给管理员角色（role_id=1）分配问诊菜单权限
INSERT INTO sys_role_menu VALUES (1, 2000);
INSERT INTO sys_role_menu VALUES (1, 2001);
INSERT INTO sys_role_menu VALUES (1, 2002);
INSERT INTO sys_role_menu VALUES (1, 2003);
INSERT INTO sys_role_menu VALUES (1, 2004);
INSERT INTO sys_role_menu VALUES (1, 2005);
INSERT INTO sys_role_menu VALUES (1, 2006);
INSERT INTO sys_role_menu VALUES (1, 2010);
INSERT INTO sys_role_menu VALUES (1, 2011);
INSERT INTO sys_role_menu VALUES (1, 2012);
