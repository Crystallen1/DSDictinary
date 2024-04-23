# 字典查询系统 - COMP90015 Assignment 1

## 项目概述
本项目是墨尔本大学COMP90015 Distributed Systems课程的第一次作业。目标是开发一个基于TCP和多线程的字典查询程序，该程序分为前端和后端两部分。

## 功能特点
- **TCP网络通信**: 使用TCP协议确保数据传输的可靠性。
- **多线程处理**: 支持多个客户端同时查询，提高查询效率。
- **前后端分离**: 明确区分前端用户界面和后端服务逻辑，使得系统结构清晰。

## 技术栈
- 编程语言: Java
- 网络通信: TCP
- 多线程管理
- 前端框架：JavaFX
- 构建工具：Maven
## 快速开始
克隆项目后，直接使用maven工具，分别运行以下命令：
```
mvn clean compile assembly:single -Pclient
mvn clean compile assembly:single -Pserver
```
运行一个命令后，先将生成的jar文件拷贝出来，再运行第二行命令

对于服务器的jar，运行命令如下：
```
java -jar [jar文件名] [端口号] [数据库文件]
```
对于客户端的jar，由于JavaFX在使用Maven构建时存在bug，我们需要先从JavaFX的官网上下载适合你电脑的SDK，然后运行以下命令启动：
```
java --module-path [SDK的lib目录路径] --add-modules javafx.controls,javafx.fxml -jar [jar文件名] [hostname] [端口号] 
```
以上命令中的端口号和数据库文件都设置了默认值，如果是和github仓库相同的目录格式，可以不填写。
### 已知问题
1. 前端的文本输入框不能很好的处理空格和回车，这两种常见的输入会影响查询和添加单词
2. 同时添加多个意思的功能只实现了前端部分，后端部分还没做。
