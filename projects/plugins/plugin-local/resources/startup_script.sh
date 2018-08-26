#!/bin/bash

APP_NAME=application.sh
echo "#!/bin/bash" > ${APP_NAME}
echo "" >> ${APP_NAME}
echo "cat \$1 > \$3" >> ${APP_NAME}
echo "cat \$2 >> \$3" >> ${APP_NAME}
echo "" >> ${APP_NAME}
echo "echo \"Execution time: \`date\`\" >> \$3" >> ${APP_NAME}
echo "" >> ${APP_NAME}
echo "echo \"Extra file execution time: \`date\`\" >> \$4" >> ${APP_NAME}
chmod 777 ${APP_NAME}


COORDINATOR=executor-coordinator-0.1.jar
curl -o ${COORDINATOR} http://localhost:8282/spaces/test/file/${COORDINATOR}/download
apt-get install -y openjdk-8-jdk && java -jar ${COORDINATOR}
