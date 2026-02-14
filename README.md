# Indexed Server

工程根目录，当前包含 **demo** 模块（用于后续测试）。  
服务端由 [Ktor](https://start.ktor.io) 搭建。

Here are some useful links to get you started:

- [Ktor Documentation](https://ktor.io/docs/home.html)
- [Ktor GitHub page](https://github.com/ktorio/ktor)
- The [Ktor Slack chat](https://app.slack.com/client/T09229ZC6/C0A974TJ9). You'll need to [request an invite](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up) to join.

## Features

Here's a list of features included in this project:

| Name                                               | Description                                                 |
| ----------------------------------------------------|------------------------------------------------------------- |
| [Routing](https://start.ktor.io/p/routing-default) | Allows to define structured routes and associated handlers. |

## Building & Running

To build or run the project, use one of the following tasks:

| Task                                    | Description                                                          |
| -----------------------------------------|---------------------------------------------------------------------- |
| `./gradlew :demo:test`                  | Run the tests                                                        |
| `./gradlew :demo:build`                 | Build demo 模块                                                      |
| `./gradlew :demo:buildFatJar`          | 构建可执行 fat JAR                                                   |
| `./gradlew :demo:buildImage`            | 构建 Docker 镜像                                                     |
| `./gradlew :demo:run`                   | 运行 demo 服务                                                       |
| `./gradlew runDocker`                   | Run using the local docker image                                     |

If the server starts successfully, you'll see the following output:

```
2024-12-04 14:32:45.584 [main] INFO  Application - Application started in 0.303 seconds.
2024-12-04 14:32:45.682 [main] INFO  Application - Responding at http://0.0.0.0:8080
```

