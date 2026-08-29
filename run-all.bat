@echo off
:: Ajusta o encoding para UTF-8 no console do Windows
chcp 65001 >nul
setlocal enabledelayedexpansion

set "ROOT_DIR=%~dp0"
set "FRONT_DIR=%ROOT_DIR%frontend"
set "BACK_DIR=%ROOT_DIR%backend"

:: Força execução não interativa e bloqueia prompts
set "CI=true"
set "npm_config_yes=true"

:: Detecta Maven Wrapper
if exist "%BACK_DIR%\mvnw.cmd" (
    set "MVN_CMD=%BACK_DIR%\mvnw.cmd"
) else (
    set "MVN_CMD=mvn"
)

echo ==================================================
echo  PIPELINE - GESTÃO DE ATIVIDADES COMPLEMENTARES
echo ==================================================
echo.

:: ============================================================
:: 1. FRONTEND - FORMATAÇÃO E LINT
:: ============================================================

echo ==================================================
echo  [1/7] Formatando e lintando o Frontend...
echo ==================================================

cd /d "%FRONT_DIR%"
call npm run fix-all
if %ERRORLEVEL% neq 0 (
    echo [ERRO] Falha na formatacao/lint do Frontend.
    exit /b %ERRORLEVEL%
)

echo.
echo Frontend formatado e lintado.
echo.

:: ============================================================
:: 2. FRONTEND - TESTES
:: ============================================================

echo ==================================================
echo  [2/7] Executando testes do Frontend...
echo ==================================================

call npx ng test --watch=false < nul
if %ERRORLEVEL% neq 0 (
    echo [ERRO] Falha nos testes do Frontend.
    exit /b %ERRORLEVEL%
)

echo.
echo Testes do Frontend concluídos.
echo.

:: ============================================================
:: 3. FRONTEND - COBERTURA
:: ============================================================

echo ==================================================
echo  [3/7] Gerando cobertura do Frontend...
echo ==================================================

call npx ng test --configuration coverage --watch=false < nul
if %ERRORLEVEL% neq 0 (
    echo [ERRO] Falha na geracao de cobertura do Frontend.
    exit /b %ERRORLEVEL%
)

echo.
echo Cobertura do Frontend gerada.

set "LCOV_FILE="
for /r "%FRONT_DIR%\coverage" %%F in (lcov.info) do (
    if exist "%%F" set "LCOV_FILE=%%F"
)

if defined LCOV_FILE (
    echo Relatório lcov.info encontrado: !LCOV_FILE!
) else (
    echo Aviso: nao foi localizado lcov.info no diretorio coverage.
)

:: ============================================================
:: 4. BACKEND - SPOTLESS + OPENREWRITE
:: ============================================================

echo ==================================================
echo  [4/7] Formatando e analisando o Backend...
echo ==================================================

cd /d "%BACK_DIR%"

echo.
echo Aplicando Spotless...
call %MVN_CMD% spotless:apply
if %ERRORLEVEL% neq 0 (
    echo [ERRO] Falha ao aplicar Spotless no Backend.
    exit /b %ERRORLEVEL%
)

echo.
echo Executando OpenRewrite...
call %MVN_CMD% rewrite:run
if %ERRORLEVEL% neq 0 (
    echo [ERRO] Falha ao executar OpenRewrite no Backend.
    exit /b %ERRORLEVEL%
)

echo.
echo Spotless e OpenRewrite concluídos.
echo.

:: ============================================================
:: 5. BACKEND - TESTES + JACOCO + VERIFY
:: ============================================================

echo ==================================================
echo  [5/7] Executando testes, cobertura e verify do Backend...
echo ==================================================

call %MVN_CMD% clean verify
if %ERRORLEVEL% neq 0 (
    echo [ERRO] Falha no build/testes do Backend.
    exit /b %ERRORLEVEL%
)

echo.
echo Testes e cobertura do Backend concluídos.
echo.

set "JACOCO_FILE=%BACK_DIR%\target\site\jacoco\jacoco.xml"
if exist "%JACOCO_FILE%" (
    echo Relatório JaCoCo encontrado:
    echo %JACOCO_FILE%
) else (
    echo ERRO: relatório JaCoCo não encontrado:
    echo %JACOCO_FILE%
    exit /b 1
)

echo.

:: ============================================================
:: 6. SONARCLOUD (FRONTEND & BACKEND)
:: ============================================================

echo ==================================================
echo  [6/7] Analisando e enviando para SonarCloud...
echo ==================================================

if "%SONAR_TOKEN%"=="" (
    echo.
    echo Aviso: SONAR_TOKEN não está definido no Windows.
    echo Análise do SonarCloud será ignorada.
) else (
    :: --- Análise Frontend ---
    echo.
    echo Enviando Frontend ao SonarCloud...
    cd /d "%FRONT_DIR%"

    call npx sonar-scanner ^
        -Dsonar.host.url=https://sonarcloud.io ^
        -Dsonar.organization=sgac-gestaoatividadecomplementarorg ^
        -Dsonar.projectKey=sgac-gestaoatividadecomplementarorg_gestaoatividadecomplementar-front ^
        -Dsonar.token=%SONAR_TOKEN% ^
        -Dsonar.sources=src ^
        -Dsonar.tests=src ^
        -Dsonar.test.inclusions=**/*.spec.ts ^
        -Dsonar.javascript.lcov.reportPaths=coverage/lcov.info
    if %ERRORLEVEL% neq 0 (
        echo [ERRO] Falha ao enviar analise do Frontend ao SonarCloud.
        exit /b %ERRORLEVEL%
    )
    echo Análise do Frontend enviada com sucesso.

    :: --- Análise Backend ---
    echo.
    echo Enviando Backend ao SonarCloud...
    cd /d "%BACK_DIR%"

    call %MVN_CMD% sonar:sonar ^
        -Dsonar.host.url=https://sonarcloud.io ^
        -Dsonar.organization=sgac-gestaoatividadecomplementarorg ^
        -Dsonar.projectKey=sgac-gestaoatividadecomplementarorg_gestaoatividadecomplementarorg ^
        -Dsonar.token=%SONAR_TOKEN% ^
        -Dsonar.coverage.jacoco.xmlReportPaths="%JACOCO_FILE%"
    if %ERRORLEVEL% neq 0 (
        echo [ERRO] Falha ao enviar analise do Backend ao SonarCloud.
        exit /b %ERRORLEVEL%
    )
    echo Análise do Backend enviada com sucesso.
)

echo.

:: ============================================================
:: 7. RESUMO
:: ============================================================

echo ==================================================
echo  [7/7] Validando resultados...
echo ==================================================

cd /d "%ROOT_DIR%"

echo.
echo Frontend:
echo   ✓ Formatação
echo   ✓ ESLint
echo   ✓ Testes
echo   ✓ Cobertura (lcov)

echo.
echo Backend:
echo   ✓ Spotless
echo   ✓ OpenRewrite
echo   ✓ Testes
echo   ✓ JaCoCo
echo   ✓ Maven Verify

echo.
if not "%SONAR_TOKEN%"=="" (
    echo SonarCloud:
    echo   ✓ Frontend enviado
    echo   ✓ Backend enviado
) else (
    echo SonarCloud:
    echo   - Ignorado (SONAR_TOKEN não definido)
)

echo.
echo ==================================================
echo  SUCESSO
echo ==================================================
echo Pipeline completo concluído.
echo ==================================================

endlocal