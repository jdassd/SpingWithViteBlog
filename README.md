# SpingWithViteBlog V1.0

This repository contains a Spring Boot (JDK 21) backend and a Vite + Vue 3 frontend.
The Dockerfile builds both parts and produces a runnable container without any extra manual build steps.

## Package & Run (Docker)

Build:

```
docker build -t spring-with-vite-blog .
```

Run (H2 persisted to host):

Linux/macOS (bash):

```
docker run -d \
  -p 8080:8080 \
  -e APP_JWT_SECRET=change_me_in_prod_change_me_in_prod_32 \
  -v "${PWD}/data:/app/data" \
  --name spring-with-vite-blog \
  spring-with-vite-blog
```

Windows PowerShell:

```
docker run -d `
  -p 8080:8080 `
  -e APP_JWT_SECRET=change_me_in_prod_change_me_in_prod_32 `
  -v "${PWD}/data:/app/data" `
  --name spring-with-vite-blog `
  spring-with-vite-blog
```

Default admin account:

- Username: admin
- Password: 123456

## Data Persistence (H2)

The H2 database is configured as a file database at `/app/data` inside the container.
If you do not mount a host directory, data will be lost when the container is removed.
Use a volume or bind mount like the example above to keep data between restarts.

Uploads, theme assets, and custom code versions are stored under `/app/data` as well.

## Configuration

- `APP_JWT_SECRET`: JWT signing secret (must be 32+ characters). Set your own value in production.
- `SPRING_PROFILES_ACTIVE`: defaults to `prod` in the Docker image.

## Local Development (optional)

Backend:

```
./mvnw -f backend/pom.xml spring-boot:run
```

Frontend:

```
cd frontend
npm install
npm run dev
```

---

# SpingWithViteBlog V1.0（中文）

本仓库包含 Spring Boot（JDK 21）后端与 Vite + Vue 3 前端。
Dockerfile 会自动构建前后端并打包成可运行容器，不需要额外手动构建。

## 打包与运行（Docker）

构建镜像：

```
docker build -t spring-with-vite-blog .
```

运行（H2 数据持久化到主机）：

Linux/macOS（bash）：

```
docker run -d \
  -p 8080:8080 \
  -e APP_JWT_SECRET=change_me_in_prod_change_me_in_prod_32 \
  -v "${PWD}/data:/app/data" \
  --name spring-with-vite-blog \
  spring-with-vite-blog
```

Windows PowerShell：

```
docker run -d `
  -p 8080:8080 `
  -e APP_JWT_SECRET=change_me_in_prod_change_me_in_prod_32 `
  -v "${PWD}/data:/app/data" `
  --name spring-with-vite-blog `
  spring-with-vite-blog
```

默认管理员账号：

- 用户名：admin
- 密码：123456

## 数据持久化（H2）

H2 使用文件模式，数据存放在容器内 `/app/data`。
如果不挂载主机目录，删除容器后数据会丢失。
请使用上面的挂载方式或 Docker Volume 保证数据持久化。

上传文件、主题资源、自定义代码版本也都存放在 `/app/data`。

## 配置说明

- `APP_JWT_SECRET`：JWT 签名密钥（至少 32 字符），生产环境请自行替换。
- `SPRING_PROFILES_ACTIVE`：Docker 镜像默认使用 `prod`。

## 本地开发（可选）

后端：

```
./mvnw -f backend/pom.xml spring-boot:run
```

前端：

```
cd frontend
npm install
npm run dev
```
