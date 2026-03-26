# Camel Event Hub (Apache Camel + SQS/SNS Local)
![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![Apache Camel](https://img.shields.io/badge/Apache%20Camel-3.x-orange)
![AWS](https://img.shields.io/badge/AWS-SNS%20%7C%20SQS-yellow)
![LocalStack](https://img.shields.io/badge/LocalStack-Enabled-purple)
![Docker](https://img.shields.io/badge/Docker-Required-blue)


## 🛠️ Objetivo
Este projeto é um **exemplo de integração de mensageria** usando Apache Camel, simulando filas SQS e tópicos SNS **100% local via Docker**, sem necessidade de AWS real.  
O objetivo é criar um **projeto de portfólio nível produção**, focado em:

- Integração com mensageria (SQS/SNS)
- Rotas Apache Camel
- Transformações, processamento condicional e roteamento
- Simulação de retries e DLQ (Dead Letter Queue)
- Logs estruturados e monitoramento de fluxo de mensagens

> 💡 Observação: Este projeto utiliza **LocalStack** e bibliotecas AWS2 Camel para **simular os serviços AWS localmente**, tornando desnecessário criar recursos na AWS real.

---

## 📦 Stack Tecnológica

- Java 21 + Spring Boot 3.x
- Apache Camel 4.x
- LocalStack (SQS + SNS)
- Docker & Docker Compose
- Maven (build e dependências)
- JUnit + Camel Test Kit (testes de rotas)

---

## 🚀 Como rodar o projeto

### 1️⃣ Pré-requisitos

- Docker & Docker Compose
- Java 21 e Maven
- (Opcional) IDE como IntelliJ ou VS Code
- AWS CLI

### 2️⃣ Subir LocalStack (simulação AWS)

Na raiz do projeto (onde está o `docker-compose.yml`):

```bash id="s69tww"
docker-compose up -d

Isso criará o container LocalStack com suporte a:

SQS
SNS

---
Instalação do AWS CLI (Windows)

Para interagir com o LocalStack e criar filas e tópicos SQS/SNS, é necessário ter o AWS CLI v2 instalado.

Passo 1 – Instalar AWS CLI

Existem duas formas fáceis de instalar no Windows:

Opção 1 – Usando o instalador .msi:

Baixe o instalador oficial: AWS CLI Windows
Execute o arquivo .msi e siga os passos da instalação.
Certifique-se de marcar a opção “Add AWS CLI to PATH”.

Opção 2 – Usando PowerShell com curl:

# Baixar o instalador diretamente
curl "https://awscli.amazonaws.com/AWSCLIV2.msi" -o "$env:USERPROFILE\Downloads\AWSCLIV2.msi"

# Instalar silenciosamente
Start-Process msiexec.exe -Wait -ArgumentList '/i', "$env:USERPROFILE\Downloads\AWSCLIV2.msi", '/quiet'

Opção 3 – Usando winget (Windows 10/11):

winget install --id AWSCLI.AWSCLI
Passo 2 – Verificar instalação

Abra um novo PowerShell e rode:

aws --version

Você deverá ver algo como:

aws-cli/2.x.x Python/3.x Windows/10 exe/AMD64
Passo 3 – Criar fila e tópico no LocalStack

Depois de instalado, você pode criar os recursos simulados no LocalStack:

# Criar fila SQS
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name orders-queue

# Criar tópico SNS
aws --endpoint-url=http://localhost:4566 sns create-topic --name order-notifications

Após isso, o LocalStack estará pronto para o Camel consumir e publicar mensagens.--
---


---
🧪 Verificando se o LocalStack está funcionando

⚠️ Um simples curl http://localhost:4566 pode não retornar nada. Isso é esperado.

O LocalStack expõe APIs compatíveis com a AWS, então os testes devem ser feitos via AWS CLI:

# Listar filas SQS
aws --endpoint-url=http://localhost:4566 --region us-east-1 sqs list-queues

# Criar fila
aws --endpoint-url=http://localhost:4566 --region us-east-1 sqs create-queue --queue-name orders-queue

# Listar tópicos SNS
aws --endpoint-url=http://localhost:4566 --region us-east-1 sns list-topics

Se esses comandos responderem, o ambiente está funcionando corretamente.


---
📢 Criação e Validação de Tópico SNS (LocalStack)

Após subir o LocalStack, é necessário criar manualmente o tópico SNS que será utilizado pela aplicação.

Criar tópico SNS

Execute o comando abaixo:

aws --endpoint-url=http://localhost:4566 --region us-east-1 sns create-topic --name order-notifications

Resposta esperada:

{
  "TopicArn": "arn:aws:sns:us-east-1:000000000000:order-notifications"
}
Validar criação do tópico

Para confirmar que o tópico foi criado corretamente, execute:

aws --endpoint-url=http://localhost:4566 --region us-east-1 sns list-topics

Resposta esperada:

{
  "Topics": [
    {
      "TopicArn": "arn:aws:sns:us-east-1:000000000000:order-notifications"
    }
  ]
}
⚠️ Observações importantes
O TopicArn retornado segue o padrão da AWS, mesmo sendo um ambiente local.
O LocalStack simula o comportamento da AWS, portanto:
Não há necessidade de credenciais reais
A região (us-east-1) deve ser informada
Caso a lista retorne vazia ("Topics": []), significa que o tópico ainda não foi criado.
🧪 Dica de troubleshooting

Se o comando falhar:

Verifique se o container está rodando:
docker ps
Certifique-se de que a porta 4566 está disponível
Confirme que o AWS CLI está configurado corretamente (aws configure)
---



3️⃣ Criar fila e tópico simulados via AWS CLI

Ainda apontando para o LocalStack (localhost):

# Criar fila SQS
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name orders-queue

# Criar tópico SNS
aws --endpoint-url=http://localhost:4566 sns create-topic --name order-notifications

Depois disso, o Camel já pode consumir e publicar mensagens normalmente apontando para http://localhost:4566.


---
## Rodando o projeto local com Camel + LocalStack

Para facilitar o desenvolvimento e testes locais:

1. Configure as variáveis de ambiente do Windows:

```cmd
setx AWS_ACCESS_KEY_ID test
setx AWS_SECRET_ACCESS_KEY test
setx AWS_REGION us-east-1
setx AWS_ENDPOINT_URL http://localhost:4566
Suba o LocalStack:
docker compose up -d
Crie fila SQS e tópico SNS:
aws --endpoint-url=http://localhost:4566 --region us-east-1 sqs create-queue --queue-name orders-queue
aws --endpoint-url=http://localhost:4566 --region us-east-1 sns create-topic --name orders-topic
Certifique-se que application.yml usa variáveis de ambiente:
camel:
  springboot:
    main-run-controller: true

aws:
  accessKey: ${AWS_ACCESS_KEY_ID:test}
  secretKey: ${AWS_SECRET_ACCESS_KEY:test}
  region: ${AWS_REGION:us-east-1}
  sqs:
    endpoint-override: ${AWS_ENDPOINT_URL:http://localhost:4566}
  sns:
    endpoint-override: ${AWS_ENDPOINT_URL:http://localhost:4566}
---

4️⃣ Rodar a aplicação Spring Boot

mvn spring-boot:run


5️⃣ Testar o envio de pedidos

Simulando envio de pedidos via REST ou curl:

curl -X POST http://localhost:8080/orders \
-H "Content-Type: application/json" \
-d '{"id":1,"item":"Notebook","quantity":2}'


O Camel consome a mensagem da fila SQS, processa/valida e publica no tópico SNS. Subscribers do Camel recebem notificações e geram logs ou outras ações simuladas.


---
🔗 Integração SNS → SQS

Essa sessão mostra como configurar o envio de mensagens de um tópico SNS para uma fila SQS usando LocalStack.

1️⃣ Obter ARN da fila SQS

Antes de criar a assinatura, precisamos do ARN da fila (não da URL):

aws --endpoint-url=http://localhost:4566 sqs get-queue-attributes ^
    --queue-url http://localhost:4566/000000000000/orders-queue ^
    --attribute-names QueueArn

Exemplo de saída:

{
    "Attributes": {
        "QueueArn": "arn:aws:sqs:us-east-1:000000000000:orders-queue"
    }
}

⚠️ Para protocol=sqs, o AWS CLI exige o ARN da fila, não a URL.

2️⃣ Criar a assinatura SNS → SQS

Use o ARN da fila obtido no passo anterior:

aws --endpoint-url=http://localhost:4566 sns subscribe ^
    --topic-arn arn:aws:sns:us-east-1:000000000000:orders-topic ^
    --protocol sqs ^
    --notification-endpoint arn:aws:sqs:us-east-1:000000000000:orders-queue

Se executado com sucesso, você verá algo como:

{
    "SubscriptionArn": "arn:aws:sns:us-east-1:000000000000:orders-topic:xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
}

Agora, todas as mensagens publicadas no orders-topic serão entregues automaticamente na orders-queue.

3️⃣ Teste ponta a ponta
Publicar uma mensagem no tópico SNS:
aws --endpoint-url=http://localhost:4566 sns publish ^
    --topic-arn arn:aws:sns:us-east-1:000000000000:orders-topic ^
    --message "Mensagem de teste SNS → SQS"
Verifique se a fila SQS recebeu a mensagem (opcional):
aws --endpoint-url=http://localhost:4566 sqs receive-message ^
    --queue-url http://localhost:4566/000000000000/orders-queue

Se a configuração estiver correta, você verá a mensagem publicada pelo SNS dentro da fila SQS.
---


📝 Funcionalidades Simuladas

Consumo de mensagens de fila SQS
Processamento e validação via Apache Camel
Publicação em tópico SNS
Subscriber Camel recebendo notificações
Retry/DLQ básico
Logs estruturados de todas as rotas


🛠️ Estrutura do Projeto

camel-event-hub/
├── docker-compose.yml
├── pom.xml
├── src/
│   ├── main/java/com/example/camel/
│   │   ├── Application.java
│   │   ├── routes/OrderRoutes.java
│   │   ├── processors/OrderValidationProcessor.java
│   │   └── services/PaymentService.java
│   └── main/resources/application.yml
│   └── test/java/com/example/camel/CamelRouteTest.java
└── README.md