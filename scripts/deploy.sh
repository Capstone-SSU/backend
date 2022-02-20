REPOSITORY=/home/ubuntu/app #프로젝트 디렉토리 주소를 스크립트 내에서 자주 사용하므로 변수로 저장, $변수명으로 사용
cd $REPOSITORY

APP_NAME=demo # demo-0.0.1-SNAPSHOT.jar 이런식으로 구성되기 때문에 demo만 입력
JAR_NAME=$(ls $REPOSITORY/build/libs/ | grep '.jar' | tail -n 1)
JAR_PATH=$REPOSITORY/build/libs/$JAR_NAME # /home/ubuntu/app/build/libs/demo.xxxx.jar

echo "> 파일명: $JAR_NAME"
echo "> 파일경로: $JAR_NAME"

# 현재 구동 중인 프로세스가 있는지 없는지 판단해서 기능을 수행, 프로세스가 있으면 종료
CURRENT_PID=$(pgrep -f $APP_NAME)
if [ -z $CURRENT_PID ] #2
then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $CURRENT_PID"
  sudo kill -15 $CURRENT_PID
  sleep 5
fi

echo "> 배포 파일 경로 : $JAR_PATH"
# nohup java -jar $JAR_PATH -Dspring.config.location=classpath:/application.properties, \
#     /home/ubuntu/app/src/main/resources/application-mysql.properties, \
#     /home/ubuntu/app/src/main/resources/application-oauth.properties > /dev/null 2> /dev/null < /dev/null &
    
nohup java -jar $JAR_PATH\
-Dspring.config.location=classpath:/application.properties,\
/home/ubuntu/properties/application-oauth.properties,\
/home/ubuntu/properties/application-mysql.properties $REPOSITORY/build/libs > /dev/null 2> /dev/null < /dev/null &

    
#nohup java -jar /home/ubuntu/app/build/libs/demo-0.0.1-SNAPSHOT.jar
#### 1:17 / 

# REPOSITORY=/home/ubuntu/app_test #프로젝트 디렉토리 주소를 스크립트 내에서 자주 사용하므로 변수로 저장, $변수명으로 사용
# JAR_REPOSITORY = /home/ubuntu/capstone2 # jar파일만 저장할 공간
# PROJECT_NAME=backend

# # 제일 처음 git clone을 받았던 디렉토리로 이동 : /home/ubuntu/app_test/backend
# cd $REPOSIROTY/$PROJECT_NAME/
# echo "> Git Pull"

# git pull # 디렉토리 이동 후 main에서 pull 받음
# echo "> 프로젝트 build 시작"

# ./gradlew build # gradlew build 수행 -> /home/ubuntu/app_test/backend 에 build 파일 생성됨

# echo "> Build 파일 복사"

# # cp./build/libs/*.jar $REPOSITORY/ : build의 결과물인 jar 파일을 복사해 capstone2로 복사
# cp ./build/libs/*.jar $JAR_REPOSITORY/

# echo "> 현재 구동중인 애플리케이션 pid 확인"

# # 기존에 수행 중이던 스프링 부트 애플리케이션을 종료
# CURRENT_PID=$(pgrep -f demo.*.jar)

# echo "현재 구동 중인 애플리케이션 pid: $CURRENT_PID"

# # 현재 구동 중인 프로세스가 있는지 없는지 판단해서 기능을 수행, 프로세스가 있으면 종료
# if [ -z "$CURRENT_PID" ]; then
#   echo "> 현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
# else
#   echo "> kill -15 $CURRENT_PID"
#   kill -15 $CURRENT_PID
#   sleep 5
# fi
#   echo "> 새 애플리케이션 배포"
  
# # 새로 실행할 jar 파일명을 찾아 가장 나중의 최신 jar 파일을 변수에 저장
# JAR_NAME=$(ls -tr $REPOSITORY/$PROJECT_NAME/build/libs | grep jar | tail -n 1)

# echo "> JAR Name: $JAR_NAME"

# # 찾은 jar 파일명으로 해당 jar 파일을 nohup으로 실행
# # 내장 톰캣을 사용해서 애플리케이션 서버 실행
# # 애플리케이션 실행자가 터미널을 종료해도 계속 구동되도록 nohup 사용
# nohup java -jar \
#   -Dspring.config.location=classpath:/application.properties, \
#   /home/ubuntu/properties/application-oauth.properties \
#   /home/ubuntu/properties/application-mysql.properties 
#   $REPOSITORY/$PROJECT_NAME/build/libs/$JAR_NAME 2>&1 &

# # 3. 스크립트에 실행 권한 추가
# chmod +x ./scripts/deploy.sh

# # 4. 권한 확인
# cd /home/ubuntu/app_test/backend
# ll

# # 5. 스크립트 실행
# /home/ubuntu/app_test/backend/scripts/deploy.sh

# # 6. nohup.out 파일을 열어 로그 보기 : nohup.out은 실행되는 애플리케이션에서 출력되는 모든 내용을 가짐
# vim nohup.out

# # 7. nohup.out 제일 아래로 가면 ClientRegistrationRepository를 찾을 수 없다는 에러 발생하며,
# # 애플리케이션 실행에 실패
