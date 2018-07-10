sdk=jdkversion
build.tool.maven=3.2.5
build.out=xxxx
build.command=mvn clean package -U  -DtarLongFileMode=gnu -Dmaven.test.skip=true -Dappname=traffic-base-platform -P${SCHEMA_NAME}
env.test.java-opts = -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=9999
