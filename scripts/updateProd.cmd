cd ../

call mvn clean package -T 8C

IF NOT %ERRORLEVEL% == 0 (
    exit /b %ERRORLEVEL%
)

git fetch origin

git checkout develop
git pull origin develop

git checkout main
git pull origin main

git merge origin/develop
git push origin main

git checkout develop

ssh ubuntu@64.181.177.7 "pm2 delete api.finax"

scp -r ./target/finax.jar ubuntu@64.181.177.7:/home/ubuntu/finax_builds/back/

ssh ubuntu@64.181.177.7 "/home/ubuntu/start_scripts/start_finax_api.sh"
