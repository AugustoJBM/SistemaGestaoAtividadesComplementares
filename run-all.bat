@echo off
:: Ajusta o encoding para evitar caracteres estranhos no console do Windows
chcp 65001 >nul
setlocal

cd backend || exit /b 1 && echo "Erro de CD backend"

:: Detecta se o projeto usa o Maven Wrapper
set MVN_CMD=mvnw.cmd
if not exist "%MVN_CMD%" (
    set MVN_CMD=mvn
)

echo ==================================================
echo  [1/4] Formatando codigo e removendo imports (Spotless)...
echo ==================================================
call %MVN_CMD% spotless:apply
if %ERRORLEVEL% neq 0 exit /b %ERRORLEVEL%

echo ==================================================
echo  [2/4] Aplicando melhorias automaticas (OpenRewrite)...
echo ==================================================
call %MVN_CMD% rewrite:run

echo ==================================================
echo  [3/4] Executando build, testes unitarios e cobertura (JaCoCo)...
echo ==================================================
call %MVN_CMD% clean verify
if %ERRORLEVEL% neq 0 exit /b %ERRORLEVEL%

echo ==================================================
echo  [4/4] Analisando e enviando para o SonarCloud...
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
    if %ERRORLEVEL% neq 0 exit /b %ERRORLEVEL%
)

echo ==================================================
echo  SUCESSO! Pipeline de verificacao concluido.
echo ==================================================
endlocal