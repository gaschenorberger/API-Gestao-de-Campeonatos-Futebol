# API de Gestão de Campeonatos

API REST para cadastrar campeonatos de futebol amador, organizar times e elencos, agendar partidas, registrar placares e consultar a classificação.

## Como executar

1. Instale o JDK 21 e tenha uma instância do PostgreSQL em execução.
2. No PostgreSQL, crie o banco usado pela aplicação.

   ```sql
   CREATE DATABASE gestao_campeonatos_db;
   ```

3. Abra `campeonato-api/src/main/resources/application.properties` e configure a conexão conforme a sua instalação. A configuração do projeto usa `localhost:5432` e o usuário padrão `postgres`. Informe a senha do seu PostgreSQL em `spring.datasource.password` ou defina a variável de ambiente `SPRING_DATASOURCE_PASSWORD`. O Hibernate cria ou atualiza as tabelas ao iniciar.
4. Abra um PowerShell na raiz do projeto e execute o comando abaixo.

   ```powershell
   cd campeonato-api
   .\mvnw.cmd spring-boot:run
   ```

A API fica disponível em `http://localhost:8080` com as configurações padrão.

## Teste rápido no Postman

Use `Content-Type: application/json` quando houver corpo. Substitua os IDs de exemplo pelos IDs retornados pela API. O passo 3 considera que o campeonato tem ID 1 e que os times têm IDs 1 e 2.

| Passo | Requisição | Corpo JSON | Resultado esperado |
|---|---|---|---|
| 1 | `POST /campeonatos` | `{"nome":"Copa Amadora","temporada":2026}` | `201 Created` |
| 2 | `POST /times` (duas vezes) | `{"nome":"Cascavel","sigla":"CAS","campeonatoId":1}` e `{"nome":"Toledo","sigla":"TOL","campeonatoId":1}` | `201 Created` para cada time |
| 3 | `POST /partidas` | `{"campeonatoId":1,"mandanteId":1,"visitanteId":2,"rodada":1,"dataHora":"2026-11-22T15:00:00","local":"Estádio Municipal"}` | `201 Created`, status `AGENDADA` |
| 4 | Repita `POST /partidas` | Mesmo corpo do passo anterior | `409 Conflict` pelo horário ocupado |
| 5 | `PATCH /partidas/1/status` | `{"status":"EM_ANDAMENTO"}` | `200 OK`, placar inicial de 0 a 0 |
| 6 | `PATCH /partidas/1/placar` | `{"golsMandante":1,"golsVisitante":1}` | `200 OK`, placar de 1 a 1 |
| 7 | `PATCH /partidas/1/status` | `{"status":"FINALIZADA"}` | `200 OK` |
| 8 | `GET /campeonatos/1/classificacao` | Sem corpo | `200 OK`, 1 ponto por time se for a única partida finalizada |

Para testar elencos, envie `POST /jogadores` com `{"nome":"Ana Lima","posicao":"Atacante","numeroCamisa":9,"timeId":1}` e consulte `GET /jogadores?timeId=1`. Para testar uma correção de resultado, envie outro `PATCH /partidas/1/placar` após a finalização e consulte a classificação novamente.

O [documento de CRUD](docs/CRUD.docx) descreve as demais operações. O [DER](docs/Modelagem/DER.png) mostra os relacionamentos entre as entidades.

