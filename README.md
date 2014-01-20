# 我愛卡提諾 groovy_ILoveCK101 

https://github.com/tzangms/iloveck101 (我愛卡提諾/tzangms大 aka 海總理) 的  Groovy 版本。


## 只使用 Groovy

### 如何安裝

#### 安裝 JAVA
* [Download JAVA](http://www.java.com/zh_TW/download/).
* Set your JAVA_HOME environment variable to point to your JDK. On OS X this is /Library/Java/Home, on other unixes its often /usr/java etc. If you've already installed tools like Ant or Maven you've probably already done this step.

參考
* [安裝及設定Java環境](http://it-easy.tw/java-class-1/)

#### 安裝 Groovy

* First, [Download a binary distribution of Groovy](http://groovy.codehaus.org/Download) and unpack it into some file on your local file system.
* Set your GROOVY_HOME environment variable to the directory you unpacked the distribution.
* Add GROOVY_HOME/bin to your PATH environment variable.

### 如何使用

```bash 
$ groovy ILoveCk101.groovy [url]
```

for example

```bash
$ groovy ILoveCk101.groovy http://ck101.com/thread-2876990-1-1.html
$ groovy ILoveCk101.groovy http://ck101.com/beauty/
```

在 windows 桌面上就可以發現 ILoveCk101 資料夾，圖片都按照標題放在各自的資料夾下。

### 看起來像這樣

![截圖](http://i.imgur.com/EsZ6chY.png) 

## 使用 Grandle

#### 安裝 JAVA
* [Download JAVA](http://www.java.com/zh_TW/download/).
* Set your JAVA_HOME environment variable to point to your JDK. On OS X this is /Library/Java/Home, on other unixes its often /usr/java etc. If you've already installed tools like Ant or Maven you've probably already done this step.

#### 安裝 Gradle

* First, [Download a binary distribution of Gradle](http://www.gradle.org/downloads) and unpack it into some file on your local file system.
* For running Gradle, add GRADLE_HOME/bin to your PATH environment variable. Usually, this is sufficient to run Gradle.
* To check if Gradle is properly installed just type gradle -v. The output shows Gradle version and also local environment configuration (groovy and jvm version, etc.).

參考
* [安裝及設定Grandle環境](http://www.gradle.org/docs/current/userguide/userguide_single.html#installation)


#### 如何使用

你可用 gradle build 一個可以執行的 jar，只需要在有裝 jvm 的機器上就可以跑

##### 產生可以執行的 .jar

```bash
#產生 iloveck101-standalone.jar 在 build/libs 資料夾下
$ gradle oneJar

#執行 iloveck101-standalone.jar
$  java -jar build/libs/iloveck101-standalone.jar "[url]"
```

for example

```bash
$ java -jar build/libs/iloveck101-standalone.jar "http://ck101.com/thread-2876990-1-1.html"
```
##### 或是用 gradle 幫你 run groovy_iloveck101

```bash 
$ ~/groovy_ILoveCK101/gradle run -PappProp="[url]"
```

for example

```bash
$ gradle run -PappProp="http://ck101.com/thread-2876990-1-1.html"
$ gradle run -PappProp="http://ck101.com/beauty/"
```

在桌面上就可以發現 ILoveCk101 資料夾，圖片都按照標題放在各自的資料夾下。