# Calculadora de Notas Mínimas

API em Spring Boot 3.5 seguindo Clean Architecture + DDD para simular notas mínimas (target padrão 7) a partir dos pesos das atividades.

## Arquitetura (Clean + DDD)
- **Domain**: Entities `CoursePlan` e `Assessment`, Value Objects `GradeValue` (0-10) e `WeightValue` (0-1), enum `SimulationStatus`.
- **Domain Repository**: `CoursePlanRepository` (contrato).
- **Infrastructure**: `CoursePlanRepositoryAdapter` + `CoursePlanJpaRepository` (Spring Data JPA/H2).
- **Application/Use Cases**: `CoursePlanService` (CRUD/salvar notas), `SimulationService` (aplica overrides e chama `CalculationService`), `CalculationService` (regra de negócio do déficit de média e status alcançável/inviável), mapeadores e DTOs.
- **Interface**: `CoursePlanController`, `RestExceptionHandler`, Swagger via `OpenApiConfig`.

### Por que Entity precisa de getters/setters/ctor/toString/hashCode?
- **Getters/Setters**: expõem estado de forma controlada (essencial para JPA/lombok) e permitem invariantes/validações.
- **Construtor**: garante criação de agregados válidos (DDD) e é exigido pelo JPA (construtor default).
- **toString**: facilita inspeção/log sem depurar (útil em testes e troubleshooting).
- **hashCode/equals**: necessários para entidades em coleções/sets, comparações e uso como chaves em caches ou quando o JPA gerencia identidade.

## Rotas principais (também no Swagger)
- `POST /api/v1/plans` cria um plano (aluno opcional, `targetAverage` opcional, atividades com `weight` fracionado e nota opcional).
- `GET /api/v1/plans` lista planos.
- `GET /api/v1/plans/{id}` retorna um plano específico.
- `POST /api/v1/plans/{id}/simulate?persist=false` simula e devolve `requiredGrade` e status `ALCANCAVEL`/`INVIAVEL` (envie `grades` com `assessmentId` + nota). Com `persist=true`, salva as notas simuladas.
- H2: `GET /h2-console` (JDBC: `jdbc:h2:mem:grades_dev`).

## Perfis/Config
- `application.properties` ativa `dev` por padrão.
- `application-dev.properties`: H2 em memória, `ddl-auto=update`, SQL logado.
- `application-test.properties`: H2 em memória `create-drop` para testes isolados.

## Como rodar localmente
```bash
cd af
chmod +x mvnw
MAVEN_USER_HOME=.m2 ./mvnw clean verify
./mvnw spring-boot:run
# Swagger: http://localhost:8080/swagger-ui.html
# H2: http://localhost:8080/h2-console
```

## Testes e Qualidade
- Cobertura Jacoco com gate de 99% (plugin em `pom.xml` + stage QualityGate no Jenkins).
- PMD executa em validate/verify e o pipeline publica `target/pmd.xml`.
- Testes criados para entity/value objects, repository (@DataJpaTest), services (Mockito/H2), controller (@WebMvcTest) e contexto (@SpringBootTest com porta randômica).

## Jenkins (Pipeline DEV + Staging)
- `Jenkinsfile` único (Groovy Pipeline) com estágios: **Pre-Build** (pmd), **Pipeline-test-dev** (clean verify + JUnit/Jacoco/PMD), **QualityGate-99** (jacoco.xml), **Image_Docker** (build `lab4/calculator:dev` só se gate aprovar) e **Deploy_Staging** (sobe container via `docker-compose.staging.yml` em 8081 e faz smoke `curl` no Swagger).
- Quality Gate >=99% bloqueia build da imagem e o deploy.
- Relatórios arquivados: Jacoco + PMD + JUnit.

## Docker
```bash
cd af
MAVEN_USER_HOME=.m2 ./mvnw clean package
docker build -t lab4/calculator:dev .
docker-compose up -d
```

## Swagger em PDF
1. Suba a aplicação, abra `http://localhost:8080/swagger-ui.html`.
2. Use "Print / Save as PDF" do navegador para gerar o PDF dos endpoints (resumo em `docs/swagger-endpoints.md`).
