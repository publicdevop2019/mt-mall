<p align="center" >
    多树商城 / MT-MALL
</p>
<p align="center">
  MT-MALL是一款基于Spring Boot, 事件驱动与分布式事务的网上商城系统，实现了用户端与管理端的基本商城功能
</p>

<p align="center">
    <img src="https://img.shields.io/github/last-commit/publicdevop2019/mt-mall.svg?style=flat-square" />
</p>

# 项目特点
- 基于事件的系统架构
- 分布式事务处理中心
# 技术栈
| 模块                       |编号                                     | 概述                                  | 技术栈                                  |  
|:---------------------------|:---------------------------------------|:--------------------------------------|:--------------------------------------|
| mt-shop      |3| 产品，属性，类目管理 |Spring boot, Redis, RabbitMQ
| mt-profile        |2 | 地址，购物车，订单管理   |Spring boot, Redis, RabbitMQ
| mt-ui-admin |7| 商城管理UI |Angular
| mt-ui-user |16| 商城UI |Angular
| mt-saga|15 | 基于Saga模式的分布式事务管理  |Spring Boot, RabbitMQ
| mt-payment|4 | 一个测试用的支付微服务  |Spring Boot, RabbitMQ
| mt-file|5 | 文件存储服务  |Spring Boot, RabbitMQ