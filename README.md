# 医生与病人在线问诊系统

## 项目简介

本项目是一个基于 RuoYi-Vue 二次开发的在线问诊系统，面向医生、患者、管理员三类角色，支持问诊预约、医生接诊、实时音视频问诊、问诊消息记录、参与者管理、问诊状态流转等功能。

项目后端基于 Spring Boot、Spring Security、JWT、MyBatis、Redis 构建，前端基于 Vue、Element UI 实现，实时音视频能力通过 LiveKit 接入。

## 技术栈

### 后端
- Spring Boot
- Spring Security
- JWT
- MyBatis
- Redis
- MySQL
- LiveKit Server SDK

### 前端
- Vue
- Element UI
- Axios
- Vue Router

## 核心功能

- 用户登录与权限控制
- 医生/患者角色区分
- 问诊单创建、查询、修改、删除
- 医生接诊、结束问诊、取消问诊
- 问诊参与者管理
- 问诊消息记录
- LiveKit Token 生成
- 在线音视频问诊房间接入

## 项目亮点

- 基于 RuoYi 权限体系扩展医疗问诊业务模块
- 独立封装 ruoyi-consultation 模块，降低业务耦合
- 接入 LiveKit 实现实时音视频问诊能力
- 设计问诊状态流转，模拟真实互联网医院业务流程