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

cd $REPOSITORY/build/libs
echo "> 배포 파일 경로 : $JAR_PATH"
nohup java -jar $JAR_NAME --spring.config.location=/home/ubuntu/app/src/main/resources/application.properties > $REPOSITORY/nohup.out 2>&1 &