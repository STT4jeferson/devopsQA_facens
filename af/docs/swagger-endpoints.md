# Endpoints (Swagger)

Abra `http://localhost:8080/swagger-ui.html` com a aplicação rodando e use "Print to PDF" do navegador para gerar o PDF solicitado.

Principais rotas:
- `POST /api/v1/plans` cria um plano de avaliação (targetAverage padrão 7, lista de atividades com peso fracionado).
- `GET /api/v1/plans` lista planos criados.
- `GET /api/v1/plans/{id}` retorna um plano específico.
- `POST /api/v1/plans/{id}/simulate?persist=false` simula notas (ou persiste se `persist=true`) informando lista `grades` com `assessmentId` e nota.
- `GET /h2-console` abre o console do H2 (JDBC URL: `jdbc:h2:mem:grades_dev`).
