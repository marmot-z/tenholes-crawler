# tenholes-crawler
[中文](./README-zh.md)|English  
This project is used to download tenholes html page and media resources.

## Requires
- chrome driver  
  You should pre-installed web driver before start project, chromedriver_mac_arm64 in the project only compatible with Apple Silicon architecture Chrome 111 version. You need reinstall the correct web driver when you runtime environment does not following below condition:
  - Mac OS
  - Apple Silicon architecture
  - Chrome version is 111

  chrome driver download link : https://chromedriver.chromium.org/downloads.   
  Make sure installed web driver has execute permission.

- jdk 11

## Start Up
```shell
git clone https://github.com/marmot-z/tenholes-crawler.git
cd tenholes-crawler
mvn clean package assembly:single
cd ./target
# first arguments: download output path
# second arguments：auth token cookie
java -jar tenholes-crawler-${version}.jar /download/output/path xxxx
```
Watch `/download/output/path` folder does is contains downloaded html or media file.

## Declaration
Secondary development and business activity are prohibited in this project.

## LICENSE
This project use Apache-2.0 license.  