# tenholes-crawler
[English](./README.md)|中文  
用于下载相关页面及页面上的媒体资源

## 安装依赖项
- chrome driver  
  启动本项目需要预安装 web driver，项目中 chromedriver_mac_arm64 仅适用于 Apple Silicon 的 chrome 111 版本。如果你当前的运行环境不符合以下条件则需要重新安装 web driver：
    - 操作系统为 mac OS
    - CPU 架构为 Apple Silicon
    - 浏览器版本为 Chrome 111

      chrome driver 安装地址为：https://chromedriver.chromium.org/downloads   
      _**请确保安装后的 web driver 有执行权限**_

- jdk 11

## 启动
```shell
git clone https://github.com/marmot-z/tenholes-crawler.git
cd tenholes-crawler
mvn clean package assembly:single
cd ./target
# 第一个参数为：下载输出路径
# 第二个参数为：验证 token cookie
java -jar tenholes-crawler-${version}.jar /download/output/path xxxx
```
观察 `/download/output/path` 是否有下载好的 html 文件及其媒体资源

## 声明
本项目禁止二次扩展，禁止用于营利等商业活动

## LICENSE
本项目使用 Apache-2.0 license 开发