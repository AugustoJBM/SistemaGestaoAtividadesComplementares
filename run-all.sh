#!/bin/bash

# Interrompe o script imediatamente se algum comando crítico falhar
set -e

cd backend || exit 1 && echo "Erro de CD backend"

# Detecta se o projeto usa o Maven Wrapper (recomendado no Spring Boot)
MVN_CMD="./mvnw"
if [ ! -f "$MVN_CMD" ]; then
    MVN_CMD="mvn"
fi

SONAR_TOKEN=${SONAR_TOKEN:-""}


echo "=================================================="
echo " [1/4] Formatando código e removendo imports (Spotless)..."
echo "=================================================="
$MVN_CMD spotless:apply

echo "=================================================="
echo " [2/4] Aplicando melhorias automáticas (OpenRewrite)..."
echo "=================================================="
$MVN_CMD rewrite:run || echo "OpenRewrite finalizado."

echo "=================================================="
echo " [3/4] Executando build, testes unitários e cobertura (JaCoCo)..."
echo "=================================================="
$MVN_CMD clean verify

echo "=================================================="
echo " [4/4] Analisando e enviando para o SonarCloud..."
echo "=================================================="
if [ -z "$SONAR_TOKEN" ]; then
    echo "Aviso: Variável \$SONAR_TOKEN não definida. Pulando a análise do SonarCloud."
else
    $MVN_CMD sonar:sonar \
      -Dsonar.host.url=https://sonarcloud.io \
      -Dsonar.organization=sgac-gestaoatividadecomplementarorg \
      -Dsonar.projectKey=sgac-gestaoatividadecomplementarorg_gestaoatividadecomplementarorg \
      -Dsonar.token=$SONAR_TOKEN \
      -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
fi

echo "=================================================="
echo " SUCESSO! Pipeline de verificação concluído."
echo "=================================================="