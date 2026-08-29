#!/bin/bash

# ============================================================
# Pipeline local - Gestão de Atividades Complementares
# Frontend + Backend + JaCoCo + OpenRewrite + SonarCloud
# ============================================================

set -Eeuo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# ------------------------------------------------------------
# Tratamento de erros
# ------------------------------------------------------------

trap 'echo ""
echo "=================================================="
echo " ERRO"
echo "=================================================="
echo "O pipeline foi interrompido."
echo "Comando: ${BASH_COMMAND}"
echo "Linha: ${LINENO}"
echo "=================================================="
exit 1' ERR

# ------------------------------------------------------------
# Configuração
# ------------------------------------------------------------

FRONT_DIR="$ROOT_DIR/frontend"
BACK_DIR="$ROOT_DIR/backend"

# Força execução não interativa e bloqueia prompts
export CI=true
export npm_config_yes=true

# Detecta Maven Wrapper
if [ -x "$BACK_DIR/mvnw" ]; then
    MVN_CMD="$BACK_DIR/mvnw"
else
    MVN_CMD="mvn"
fi

# ============================================================
# INÍCIO
# ============================================================

echo "=================================================="
echo " PIPELINE - GESTÃO DE ATIVIDADES COMPLEMENTARES"
echo "=================================================="
echo ""

# ============================================================
# 1. FRONTEND - FORMATAÇÃO E LINT
# ============================================================

echo "=================================================="
echo " [1/7] Formatando e lintando o Frontend..."
echo "=================================================="

cd "$FRONT_DIR"

npm run fix-all

echo ""
echo "Frontend formatado e lintado."
echo ""

# ============================================================
# 2. FRONTEND - TESTES
# ============================================================

echo "=================================================="
echo " [2/7] Executando testes do Frontend..."
echo "=================================================="

npx ng test --watch=false < /dev/null

echo ""
echo "Testes do Frontend concluídos."
echo ""

# ============================================================
# 3. FRONTEND - COBERTURA
# ============================================================

echo "=================================================="
echo " [3/7] Gerando cobertura do Frontend..."
echo "=================================================="

npx ng test --configuration coverage --watch=false < /dev/null

echo ""
echo "Cobertura do Frontend gerada."

LCOV_FILE="$(find "$FRONT_DIR/coverage" -type f -name "lcov.info" 2>/dev/null | head -n 1 || true)"

if [ -n "$LCOV_FILE" ] && [ -f "$LCOV_FILE" ]; then
    echo "Relatório lcov.info encontrado: $LCOV_FILE"
else
    echo "Aviso: lcov.info não encontrado em $FRONT_DIR/coverage."
fi

# ============================================================
# 4. BACKEND - SPOTLESS + OPENREWRITE
# ============================================================

echo "=================================================="
echo " [4/7] Formatando e analisando o Backend..."
echo "=================================================="

cd "$BACK_DIR"

echo ""
echo "Aplicando Spotless..."
"$MVN_CMD" spotless:apply

echo ""
echo "Executando OpenRewrite..."
"$MVN_CMD" rewrite:run

echo ""
echo "Spotless e OpenRewrite concluídos."
echo ""

# ============================================================
# 5. BACKEND - TESTES + JACOCO + VERIFY
# ============================================================

echo "=================================================="
echo " [5/7] Executando testes, cobertura e verify do Backend..."
echo "=================================================="

"$MVN_CMD" clean verify

echo ""
echo "Testes e cobertura do Backend concluídos."
echo ""

# Verifica relatório JaCoCo
JACOCO_FILE="$BACK_DIR/target/site/jacoco/jacoco.xml"

if [ -f "$JACOCO_FILE" ]; then
    echo "Relatório JaCoCo encontrado:"
    echo "$JACOCO_FILE"
else
    echo "ERRO: relatório JaCoCo não encontrado:"
    echo "$JACOCO_FILE"
    exit 1
fi

echo ""

# ============================================================
# 6. SONARCLOUD (FRONTEND & BACKEND)
# ============================================================

echo "=================================================="
echo " [6/7] Analisando e enviando para SonarCloud..."
echo "=================================================="

if [ -z "${SONAR_TOKEN:-}" ]; then

    echo ""
    echo "Aviso: SONAR_TOKEN não está definido."
    echo "Análise do SonarCloud será ignorada."

else

    # --- Análise Frontend ---
    echo ""
    echo "Enviando Frontend ao SonarCloud..."
    cd "$FRONT_DIR"

    npx sonar-scanner \
        -Dsonar.host.url=https://sonarcloud.io \
        -Dsonar.organization=sgac-gestaoatividadecomplementarorg \
        -Dsonar.projectKey=sgac-gestaoatividadecomplementarorg_gestaoatividadecomplementar-front \
        -Dsonar.token="$SONAR_TOKEN" \
        -Dsonar.sources=src \
        -Dsonar.tests=src \
        -Dsonar.test.inclusions="**/*.spec.ts" \
        -Dsonar.javascript.lcov.reportPaths="$LCOV_FILE"

    echo "Análise do Frontend enviada com sucesso."

    # --- Análise Backend ---
    echo ""
    echo "Enviando Backend ao SonarCloud..."
    cd "$BACK_DIR"

    "$MVN_CMD" sonar:sonar \
        -Dsonar.host.url=https://sonarcloud.io \
        -Dsonar.organization=sgac-gestaoatividadecomplementarorg \
        -Dsonar.projectKey=sgac-gestaoatividadecomplementarorg_gestaoatividadecomplementarorg \
        -Dsonar.token="$SONAR_TOKEN" \
        -Dsonar.coverage.jacoco.xmlReportPaths="$JACOCO_FILE"

    echo "Análise do Backend enviada com sucesso."

fi

echo ""

# ============================================================
# 7. RESUMO
# ============================================================

echo "=================================================="
echo " [7/7] Validando resultados..."
echo "=================================================="

cd "$ROOT_DIR"

echo ""
echo "Frontend:"
echo "  ✓ Formatação"
echo "  ✓ ESLint"
echo "  ✓ Testes"
echo "  ✓ Cobertura (lcov)"

echo ""
echo "Backend:"
echo "  ✓ Spotless"
echo "  ✓ OpenRewrite"
echo "  ✓ Testes"
echo "  ✓ JaCoCo"
echo "  ✓ Maven Verify"

echo ""
if [ -n "${SONAR_TOKEN:-}" ]; then
    echo "SonarCloud:"
    echo "  ✓ Frontend enviado"
    echo "  ✓ Backend enviado"
else
    echo "SonarCloud:"
    echo "  - Ignorado (SONAR_TOKEN não definido)"
fi

echo ""
echo "=================================================="
echo " SUCESSO"
echo "=================================================="
echo "Pipeline completo concluído."
echo "=================================================="