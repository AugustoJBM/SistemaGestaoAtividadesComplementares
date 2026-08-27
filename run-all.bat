@echo off
:: Ajusta o encoding para evitar caracteres estranhos no console do Windows
chcp 65001 >nul
setlocal

cd backend || exit /b 1 && echo "Erro de CD backend"

:: Detecta se o projeto usa o Maven Wrapper
set MVN_CMD=mvnw.cmd
if not exist "backend\%MVN_CMD%" (
    set MVN_CMD=mvn
)

echo ==================================================
echo  [1/5] Formatando e lintando o Frontend...
echo ==================================================
cd Front
call npm run fix-all
if %ERRORLEVEL% neq 0 (
    cd ..
    exit /b %ERRORLEVEL%
)
cd ..

echo ==================================================
echo  [2/5] Formatando codigo backend e removendo imports (Spotless)...
echo ==================================================
cd backend
call %MVN_CMD% spotless:apply
if %ERRORLEVEL% neq 0 (
    cd ..
    exit /b %ERRORLEVEL%
)

echo ==================================================
echo  [3/5] Aplicando melhorias automaticas no backend (OpenRewrite)...
echo ==================================================
call %MVN_CMD% rewrite:run

echo ==================================================
echo  [4/5] Executando build, testes unitarios e cobertura do Backend (JaCoCo)...
echo ==================================================
call %MVN_CMD% clean verify
if %ERRORLEVEL% neq 0 (
    cd ..
    exit /b %ERRORLEVEL%
)

echo ==================================================
echo  [5/5] Analisando e enviando para o SonarCloud...
echo ==================================================
if "%SONAR_TOKEN%"=="" (
    echo Aviso: Variavel SONAR_TOKEN nao definida no Windows. Pulando a analise do SonarCloud.
) else (
    call %MVN_CMD% sonar:sonar ^
      -Dsonar.host.url=https://sonarcloud.io ^
      -Dsonar.organization=sgac-gestaoatividadecomplementarorg ^
      -Dsonar.projectKey=sgac-gestaoatividadecomplementarorg_gestaoatividadecomplementarorg ^
      -Dsonar.token=%SONAR_TOKEN% ^
      -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
    if %ERRORLEVEL% neq 0 (
        cd ..
        exit /b %ERRORLEVEL%
    )
)

cd ..
echo ==================================================
echo  SUCESSO! Pipeline completo (Front + Back) concluido.
echo ==================================================
endlocal