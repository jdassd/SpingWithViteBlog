# SpingWithViteBlog

## Docker 部署

```bash
# 构建镜像

docker build -t spingwithviteblog:1.0 .

# 运行容器

docker run --rm -p 8080:80 spingwithviteblog:1.0
```

访问：http://localhost:8080
