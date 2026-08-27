#!/bin/bash

# Interrompe o script imediatamente se algum comando crítico falhar
set -e

cd backend || exit 1 && echo "Erro de CD backend"

# Detecta se o projeto usa o Maven Wrapper (recomendado no Spring Boot)
MVN_CMD="./mvnw"
if [ ! -f "$MVN_CMD" ]; then
    MVN_CMD="mvn"
fi

echo "=================================================="
echo " [1/5] Formatando e lintando o Frontend..."
echo "=================================================="
cd Front
npm run fix-all
cd ..

echo "=================================================="
echo " [2/5] Formatando código backend e removendo imports (Spotless)..."
echo "=================================================="
cd backend
$MVN_CMD spotless:apply

echo "=================================================="
echo " [3/5] Aplicando melhorias automáticas no backend (OpenRewrite)..."
echo "=================================================="
$MVN_CMD rewrite:run || echo "OpenRewrite finalizado."

echo "=================================================="
echo " [4/5] Executando build, testes unitários e cobertura do Backend (JaCoCo)..."
echo "=================================================="
$MVN_CMD clean verify

echo "=================================================="
echo " [5/5] Analisando e enviando para o SonarCloud..."
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

cd ..
echo "=================================================="
echo " SUCESSO! Pipeline completo (Front + Back) concluído."
echo "=================================================="