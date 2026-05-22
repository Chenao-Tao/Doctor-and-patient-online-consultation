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

## 本地启动

### 1. 准备 LiveKit

首次运行下载本地 LiveKit Server:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\install-livekit.ps1
```

单独启动 LiveKit:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\start-livekit.ps1
```

本地开发默认配置:

```text
LIVEKIT_API_KEY=devkey
LIVEKIT_API_SECRET=secret
LIVEKIT_WS_URL=ws://127.0.0.1:7880
```

### 2. 启动整套服务

```powershell
powershell -ExecutionPolicy Bypass -File scripts\start-all.ps1 -Rebuild
```

启动后访问:

```text
后端: http://localhost:8081
前端: http://localhost:80
LiveKit: ws://127.0.0.1:7880
```

### 3. 测试视频会诊

1. 登录管理员创建问诊单, 选择医生和病人。
2. 医生账号登录后点击接诊, 再进入房间。
3. 病人账号用另一个浏览器登录, 进入同一个问诊房间。
4. 两边允许摄像头和麦克风权限。
5. 测试音视频、聊天、挂断和结束问诊。
