<img src="https://github.com/T1GIT/T1GIT/raw/main/covers/T1WEATHER.png">

<h3 align="center"> |
    <a href="#Description"> Description </a> |
    <a href="#Getting-Started"> Getting Started </a> |
    <a href="#Built-With"> Built With </a> |
    <a href="#Author"> Author </a> |
    <a href="#License"> License </a> |
</h3> 

------------------------------------------------

<h1 align="center"> Weather Telegram Bot </h1>

## Description
This Telegram Bot can provide users information about weather

**You can get weather for:**
1. Today
2. Tomorrow
3. Week
4. 12 hours

It has possibility of day-to-day **subscribing**

Information receives from the [OpenWeatherAPI](https://openweathermap.org/api)

## Getting Started

1. Download project
2. Install java version 14 or newer
3. Install [Maven dependencies](#maven-dependencies)
4. Run [Main.java](src/main/java/Main.java) from the IntelliJ IDE

## Using

1. Find bot `@T1WEATHER_bot` [link](https://t.me/t1weather_bot)
2. Write the name of your city. (Ex.: Moscow)

#### Basic

* **/today** _Сегодня_ - getting this day weather
* **/tomorrow** _Завтра_ - getting next day weather
* **/week** _Неделя_ - getting weather for the 7 days
* **/hours** _12 часов_ - getting weather for every of the next 12 hours
* **/subscribe** _Подписаться_ - get today weather every day
* **/unsubscribe** _Отписаться_ - break subscribing

### Administrating

**Your chatID must be in the [list](src/main/deploy/administrators.txt) for getting you administrative access**

Just write to the bot command starting with $ to receiving logs

* **$telegram** - [telegram.log](src/main/deploy/logs/telegram.log) - messages
* **$weather** - [weather.log](src/main/deploy/logs/weather.log) - requests to openweather API and responses
* **$errors** - [errors.log](src/main/deploy/logs/errors.log) - exception traces
* **$key** - [keys.txt](src/main/resources/api_keys.txt) - api key using now

### Maven dependencies

```
<dependency>
    <groupId>log4j</groupId>
    <artifactId>log4j</artifactId>
    <version>1.2.17</version>
</dependency>

<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-nop</artifactId>
    <version>1.7.13</version>
</dependency>

<dependency>
    <groupId>org.telegram</groupId>
    <artifactId>telegrambots</artifactId>
    <version>5.0.1</version>
</dependency>

<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.8.6</version>
</dependency>
```

## Deployment

This bot is ready for deployment, and already deployd on the [heroku.com](https://heroku.com)

**See:**

* [Running file](Procfile)
* [Properties file](system.properties)

## Built With

* [Java 14](https://www.oracle.com/ru/java/) - Language
* [Maven](https://maven.apache.org/) - Dependency Management
* [TelegramBots](https://github.com/rubenlagus/TelegramBots) - Library to interacting with Telegram
* [Gson](https://github.com/google/gson) - Library used for deserializing json
* [Log4j](https://logging.apache.org/log4j/2.x/) - Logging

## Author

### [**Derbin Dmitriy**](https://github.com/T1GIT)

#### Student of the Financial University
##### Group: PI19-5

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

### Version 1.2
#### 09.12.2020
