#!/usr/bin/env bash
set -euo pipefail

# =========================
# MIGRAÇÃO PARA MULTI-MÓDULO
# =========================
# Pré-requisitos assumidos:
# - Projeto atual com src/main/java/com/lab4/lab4/... (conforme árvore enviada)
# - Classes: Lab4Application.java e service/CalculadoraNotasService.java
# - Tests: CalculadoraNotasServiceTest.java e Lab4ApplicationTests.java
# - Features: src/test/resources/features/calculadora_notas.feature
#
# Resultado:
# lab4-parent/
#   lab4-domain/
#   lab4-application/
#   lab4-infrastructure/
#   lab4-web/ (Spring Boot)
#
# Observação:
# - Ajustamos os packages:
#   com.lab4.lab4.service -> com.lab4.application.service
#   com.lab4.lab4         -> com.lab4.web (apenas nos arquivos movidos para web)
# - O @SpringBootApplication passa a escanear "com.lab4.application" também.

# ---------- sanity checks ----------
if [[ ! -d "src" ]]; then
  echo "ERRO: não encontrei diretório src/ na pasta atual."
  exit 1
fi

timestamp="$(date +%Y%m%d_%H%M%S)"
backup_dir="_backup_single_module_${timestamp}"
echo "Criando backup em ${backup_dir} ..."
mkdir -p "${backup_dir}"
cp -a src "${backup_dir}/" || true
[[ -f pom.xml ]] && cp pom.xml "${backup_dir}/pom.xml" || true
[[ -d target ]] && cp -a target "${backup_dir}/target" || true

# ---------- cria estrutura multi-módulo ----------
root="lab4-parent"
echo "Criando estrutura multi-módulo em ${root} ..."
mkdir -p "${root}/lab4-domain/src/main/java"
mkdir -p "${root}/lab4-domain/src/test/java"
mkdir -p "${root}/lab4-application/src/main/java"
mkdir -p "${root}/lab4-application/src/test/java"
mkdir -p "${root}/lab4-infrastructure/src/main/java"
mkdir -p "${root}/lab4-infrastructure/src/test/java"
mkdir -p "${root}/lab4-web/src/main/java"
mkdir -p "${root}/lab4-web/src/test/java"
mkdir -p "${root}/lab4-web/src/main/resources"
mkdir -p "${root}/lab4-web/src/test/resources/features"

# ---------- mover arquivos do projeto atual ----------
# Caminhos de origem (conforme sua árvore atual)
LAB_APP_SRC="src/main/java/com/lab4/lab4/Lab4Application.java"
CALC_SERVICE_SRC="src/main/java/com/lab4/lab4/service/CalculadoraNotasService.java"
CALC_TEST_SRC="src/test/java/com/lab4/lab4/CalculadoraNotasServiceTest.java"
LAB_APP_TEST_SRC="src/test/java/com/lab4/lab4/Lab4ApplicationTests.java"
FEATURES_DIR="src/test/resources/features"

# Destinos
LAB_APP_DST="${root}/lab4-web/src/main/java/com/lab4/web/Lab4Application.java"
CALC_SERVICE_DST="${root}/lab4-application/src/main/java/com/lab4/application/service/CalculadoraNotasService.java"
CALC_TEST_DST="${root}/lab4-application/src/test/java/com/lab4/application/service/CalculadoraNotasServiceTest.java"
LAB_APP_TEST_DST="${root}/lab4-web/src/test/java/com/lab4/web/Lab4ApplicationTests.java"

# move Lab4Application -> web
if [[ -f "${LAB_APP_SRC}" ]]; then
  mkdir -p "$(dirname "${LAB_APP_DST}")"
  mv "${LAB_APP_SRC}" "${LAB_APP_DST}"
else
  echo "AVISO: ${LAB_APP_SRC} não encontrado (ok se seu nome/package diferirem)."
fi

# move service -> application
if [[ -f "${CALC_SERVICE_SRC}" ]]; then
  mkdir -p "$(dirname "${CALC_SERVICE_DST}")"
  mv "${CALC_SERVICE_SRC}" "${CALC_SERVICE_DST}"
else
  echo "AVISO: ${CALC_SERVICE_SRC} não encontrado (ok se seu nome/package diferirem)."
fi

# move tests
if [[ -f "${CALC_TEST_SRC}" ]]; then
  mkdir -p "$(dirname "${CALC_TEST_DST}")"
  mv "${CALC_TEST_SRC}" "${CALC_TEST_DST}"
else
  echo "AVISO: ${CALC_TEST_SRC} não encontrado."
fi

if [[ -f "${LAB_APP_TEST_SRC}" ]]; then
  mkdir -p "$(dirname "${LAB_APP_TEST_DST}")"
  mv "${LAB_APP_TEST_SRC}" "${LAB_APP_TEST_DST}"
else
  echo "AVISO: ${LAB_APP_TEST_SRC} não encontrado."
fi

# move resources principais para web
if [[ -d "src/main/resources" ]]; then
  shopt -s dotglob
  if compgen -G "src/main/resources/*" > /dev/null; then
    mv src/main/resources/* "${root}/lab4-web/src/main/resources/" || true
  fi
  shopt -u dotglob
fi

# move features BDD para web
if [[ -d "${FEATURES_DIR}" ]]; then
  cp -a "${FEATURES_DIR}/." "${root}/lab4-web/src/test/resources/features/"
fi

# ---------- atualizar packages/imports ----------
# - service -> com.lab4.application.service
# - app/tests web -> com.lab4.web
# - ajustar scanBasePackages no Lab4Application

# função auxiliar de replace de package de um arquivo, se existir
replace_pkg() {
  local file="$1"
  local from="$2"
  local to="$3"
  if [[ -f "$file" ]]; then
    sed -i "s|package ${from};|package ${to};|g" "$file"
  fi
}

# função auxiliar de replace de imports
replace_import() {
  local file="$1"
  local from="$2"
  local to="$3"
  if [[ -f "$file" ]]; then
    sed -i "s|${from}|${to}|g" "$file"
  fi
}

# Ajusta CalculadoraNotasService.java
replace_pkg "${CALC_SERVICE_DST}" "com.lab4.lab4.service" "com.lab4.application.service"

# Ajusta CalculadoraNotasServiceTest.java (package + imports)
replace_pkg "${CALC_TEST_DST}" "com.lab4.lab4" "com.lab4.application.service"
replace_import "${CALC_TEST_DST}" "com.lab4.lab4.service" "com.lab4.application.service"

# Ajusta Lab4Application.java e seu teste para pacote web
replace_pkg "${LAB_APP_DST}" "com.lab4.lab4" "com.lab4.web"
replace_pkg "${LAB_APP_TEST_DST}" "com.lab4.lab4" "com.lab4.web"

# Inserir/ajustar scanBasePackages na @SpringBootApplication
if [[ -f "${LAB_APP_DST}" ]]; then
  # se não houver scanBasePackages, adiciona
  if ! grep -q "scanBasePackages" "${LAB_APP_DST}"; then
    sed -i 's/@SpringBootApplication/@SpringBootApplication(scanBasePackages = {"com.lab4.web","com.lab4.application"})/' "${LAB_APP_DST}"
  fi
fi

# ---------- criar POMs ----------
PARENT_POM="${root}/pom.xml"
cat > "${PARENT_POM}" <<'XML'
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.lab4</groupId>
  <artifactId>lab4-parent</artifactId>
  <version>1.0.0</version>
  <packaging>pom</packaging>

  <modules>
    <module>lab4-domain</module>
    <module>lab4-application</module>
    <module>lab4-infrastructure</module>
    <module>lab4-web</module>
  </modules>

  <properties>
    <java.version>17</java.version>
    <spring.boot.version>3.3.4</spring.boot.version>
    <maven.compiler.source>${java.version}</maven.compiler.source>
    <maven.compiler.target>${java.version}</maven.compiler.target>
    <junit.jupiter.version>5.10.2</junit.jupiter.version>
    <cucumber.version>7.18.0</cucumber.version>
  </properties>

  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-dependencies</artifactId>
        <version>${spring.boot.version}</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>
</project>
XML

# Domain POM (vazio por agora; você pode adicionar VOs/Entidades depois)
cat > "${root}/lab4-domain/pom.xml" <<'XML'
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <parent>
    <groupId>com.lab4</groupId>
    <artifactId>lab4-parent</artifactId>
    <version>1.0.0</version>
  </parent>
  <artifactId>lab4-domain</artifactId>
  <dependencies>
    <dependency>
      <groupId>org.junit.jupiter</groupId>
      <artifactId>junit-jupiter</artifactId>
      <version>${junit.jupiter.version}</version>
      <scope>test</scope>
    </dependency>
  </dependencies>
</project>
XML

# Application POM
cat > "${root}/lab4-application/pom.xml" <<'XML'
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <parent>
    <groupId>com.lab4</groupId>
    <artifactId>lab4-parent</artifactId>
    <version>1.0.0</version>
  </parent>
  <artifactId>lab4-application</artifactId>
  <dependencies>
    <dependency>
      <groupId>com.lab4</groupId>
      <artifactId>lab4-domain</artifactId>
      <version>1.0.0</version>
    </dependency>
    <dependency>
      <groupId>org.junit.jupiter</groupId>
      <artifactId>junit-jupiter</artifactId>
      <version>${junit.jupiter.version}</version>
      <scope>test</scope>
    </dependency>
  </dependencies>
</project>
XML

# Infrastructure POM
cat > "${root}/lab4-infrastructure/pom.xml" <<'XML'
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <parent>
    <groupId>com.lab4</groupId>
    <artifactId>lab4-parent</artifactId>
    <version>1.0.0</version>
  </parent>
  <artifactId>lab4-infrastructure</artifactId>
  <dependencies>
    <dependency>
      <groupId>com.lab4</groupId>
      <artifactId>lab4-domain</artifactId>
      <version>1.0.0</version>
    </dependency>
    <dependency>
      <groupId>com.lab4</groupId>
      <artifactId>lab4-application</artifactId>
      <version>1.0.0</version>
    </dependency>
    <dependency>
      <groupId>org.junit.jupiter</groupId>
      <artifactId>junit-jupiter</artifactId>
      <version>${junit.jupiter.version}</version>
      <scope>test</scope>
    </dependency>
  </dependencies>
</project>
XML

# Web POM
cat > "${root}/lab4-web/pom.xml" <<'XML'
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <parent>
    <groupId>com.lab4</groupId>
    <artifactId>lab4-parent</artifactId>
    <version>1.0.0</version>
  </parent>
  <artifactId>lab4-web</artifactId>
  <dependencies>
    <dependency>
      <groupId>com.lab4</groupId>
      <artifactId>lab4-application</artifactId>
      <version>1.0.0</version>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>io.cucumber</groupId>
      <artifactId>cucumber-java</artifactId>
      <version>${cucumber.version}</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>io.cucumber</groupId>
      <artifactId>cucumber-junit-platform-engine</artifactId>
      <version>${cucumber.version}</version>
      <scope>test</scope>
    </dependency>
  </dependencies>
  <build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
    </plugins>
  </build>
</project>
XML

# ---------- root readme/ponteiro ----------
cat > "${root}/README.md" <<'MD'
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

MD

#---------- feedback final ----------

echo
echo "==============================================="
echo "Migração concluída!"
echo "Backup salvo em: ${backup_dir}"
echo
echo "Agora rode:"
echo " cd ${root}"
echo " mvn -q -DskipTests clean package && mvn -q test"
echo " mvn -q -pl lab4-web spring-boot:run"
echo "==============================================="