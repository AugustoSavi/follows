# 📸 Follows - Monitoramento de Seguidores do Instagram

Este projeto é uma aplicação backend em **Spring Boot 3**, que monitora os perfis seguidos por um usuário do Instagram e gera alertas sempre que houver uma nova conta seguida. 
Utiliza a biblioteca `JxInsta` para interagir com a API privada do Instagram e mantém um registro das relações de "seguindo", com alertas persistentes para consulta futura.

> [!WARNING]
> ESSE PROJETO É APENAS PARA ME DIVERTIR, NÃO É UM PRODUTO FINALIZADO OU PRONTO PARA PRODUÇÃO. USE POR SUA CONTA E RISCO!

---

## 🚀 Funcionalidades

- 🔍 Adicionar um usuário e todas as contas que ele segue do instagram.
- ♻️ Atualizar os dados de seguidos de um usuário do instagram.
- 📩 Gerar alertas para cada nova conta seguida do instagram.
- 🗂 Armazenar histórico de "seguindo".
- 📊 Monitoramento via Prometheus + Grafana.
- 🌐 APIs REST para integração com frontends ou sistemas externos.

## TODO
- [ ] Postar a notificação timeline da conta

---

## ⚙️ Tecnologias Utilizadas

- Java 21 + Spring Boot 3
- Spring Data JPA + PostgreSQL
- [JxInsta](https://github.com/ErrorxCode/JxInsta)
- Actuator + Prometheus + Grafana
- Docker + Docker Compose
- OpenAPI (Swagger) para documentação de endpoints

---

## 📦 Execução com Docker

### Pré-requisitos

* [Docker](https://www.docker.com/)
* [Docker Compose](https://docs.docker.com/compose/)

### Subir os serviços (PostgreSQL, pgAdmin, Prometheus, Grafana)

```bash
docker-compose up -d
```


## 🛠 Configuração

### Variáveis no `application.yml`

```yaml
instagram:
  username: <seu_usuario>
  password: <sua_senha>

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/follows
    username: user
    password: password
```

### Autenticação Instagram

Utilizamos o `JxInsta` com autenticação web. Certifique-se de que a conta utilizada tem permissão para visualizar os perfis seguidos.

---

## 📈 Monitoramento

### Prometheus

* Endpoint exposto: `http://localhost:9090`
* Coleta de métricas: `http://localhost:8080/actuator/prometheus`

### Grafana

* Interface: `http://localhost:3000`
* Usuário/padrão: `admin / admin`
* Fonte de dados Prometheus já configurada por padrão.

---

## 📄 Documentação da API

* Swagger UI disponível em: `http://localhost:8080/swagger-ui.html`

---

## 🧪 Testes

* Sem testes, estou de férias! 😎

## 🧩 Estrutura de Entidades

* `Username` → ID do usuário + nome de login
* `Follow` → Registro do relacionamento seguidor/seguido (com versionamento)
* `FollowAlert` → Alerta com timestamp para novos seguidos

---

## 🧰 Scripts úteis

### Build da aplicação

```bash
mvn clean install -DskipTests
```

### Rodar localmente

```bash
./mvnw spring-boot:run
```

---

## 🧠 Considerações

* A biblioteca `JxInsta` utiliza scraping da API privada do Instagram, e está sujeita a mudanças na estrutura da plataforma.
* Evite múltipos acessos simultâneos com a mesma conta para evitar bloqueios temporários pelo Instagram.
* A busca de seguidores é paginada em lotes de 12 para respeitar os limites e simular comportamento humano.

