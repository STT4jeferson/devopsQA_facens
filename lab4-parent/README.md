# Lab4 (multi-módulo)
- `lab4-domain`: Regras de domínio (VOs/Entidades/Policies)
- `lab4-application`: Casos de uso (services) + portas de entrada
- `lab4-infrastructure`: Adapters/portas de saída (DB, etc.)
- `lab4-web`: App Spring Boot (controllers, configs)

## Comandos
```bash
cd lab4-parent
mvn -q -DskipTests clean package
mvn -q test
mvn -q -pl lab4-web spring-boot:run

