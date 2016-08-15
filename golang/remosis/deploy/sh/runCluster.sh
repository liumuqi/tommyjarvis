#!/bin/bash
w=`whoami`
if [[ ! $w == "qishan" ]]; then
    echo "User should qishan"
    exit 1
fi
redispath=$2
indx=(${#redispath}-1)
if [[ '/' == ${redispath:$indx:1} ]];then
    echo "redis path last charactor must not be /"
    exit 1
fi
clusterDir=$redispath/cluster
currentPath=`pwd -P`
templateconfpath=$currentPath/conf/redistemplate.conf

init()
{
    if [[ $# < 2 ]]; then
        echo "must more then 3 param (type path port..port)"
        exit 1
    fi
    if [[  ! "$3" =~ ^[0-9]+$ ]]; then
        echo "port must number!!!!!!!"
        exit 1
    fi
    if [[ ! -r $redispath && ! -w $redispath && ! -x $redispath ]]; then
        echo "need rwx "$redispath
        exit 1
    fi

    if [[ ! -d $redispath ||  ! -f $redispath/src/redis-server ]]; then
        echo " dir or don't have redis-server--->"$redispath/src/redis-server
        exit 1
    fi
    if [[ ! -d $clusterDir  ]]; then
        mkdir -p $clusterDir
        if [[ $? != 0 ]];then
            echo " created cluster dir--->"$clusterDir "fail !!!!!!!!!!"
            exit 1
        fi
        echo " created cluster dir--->"$clusterDir
    else
        echo  $clusterDir "exist"
    fi
    if [[ ! -f $templateconfpath ]]; then
        echo "redis config template file don't exsit -->"$templateconfpath
        exit 1
    fi
    for p in ${@:3:$#}
    do
        if [[ ! -d $clusterDir/$p  ]]; then
            mkdir -p $clusterDir/$p
            if [[ $? != 0 ]];then
                echo " created cluster port dir--->"$clusterDir/$p "fail !!!!!!!!!!"
                exit 1
            fi
            echo $clusterDir/$p" dir created"
        else
            echo $clusterDir/$p "dir  exist"
        fi
        if [[ ! -f $clusterDir/$p/redis$p.conf ]]; then
            cp $templateconfpath $clusterDir/$p/redis$p.conf
            if [[ $? == 0 ]];then
                eval sed -i 's#/usr/local/redis-3.0.5#$redispath#g' $clusterDir/$p/redis$p.conf
                eval sed -i 's#6666#$p#g' $clusterDir/$p/redis$p.conf
            else
                echo  $clusterDir/$p/redis$p.conf" config file does not exited and created fail!!!!!"
            fi
        else
            echo $clusterDir/$p/redis$p.conf " file  exist"
        fi
    done
    #sysctl -w vm.overcommit_momory=1
    #echo never > /sys/kernel/mm/transparent_hugepage/enabled
}

start(){
    init $*
    for p in ${@:3:$#}
    do
        #check redis server alive
        num=$(ps axu|grep -E "redis-server.*:$p.*[cluster]"|grep -v "grep"|wc -l)
        echo "check cluster $p alive :" $num
        if [[ $num == 1 ]]; then
            echo "$p port  has already alive"
        else
            setsid $redispath/src/redis-server  $clusterDir/$p/redis$p.conf > $clusterDir/$p/$p.log 2>&1 &
            sleep 2
            num=$(ps axu|grep -E "redis-server.*:$p.*[cluster]"|grep -v "grep"|wc -l)
            if [[ $num == 1 ]]; then
                echo "$p port start success !!!!!!!!!"
            else
                echo "$p port start failed !!!!!!!!!!!!"
            fi

        fi
    done
}
stop(){
    for p in ${@:3:$#}
    do
        #check redis server alive
        num=$(ps axu|grep -E "redis-server.*:$p.*[cluster]"|grep -v grep |wc -l)
        echo "check cluster $p alive :" $num
        if [[ $num == 1 ]]; then
            $redispath/src/redis-cli -p $p shutdown
        fi
        sleep 2
        num=$(ps axu|grep -E "redis-server.*:$p.*[cluster]"|grep -v "grep"|wc -l)
        echo "check cluster $p alive :" $num "does not killed!!!!!!"
    done
}

case "$1" in
    start)
        start $*
        echo "all started!!!!!!!!!!!!"
        ;;
    stop)
        stop $*
        echo "all stoped!!!!!!!!!!!!"
        ;;
    init)
        init $*
        echo "all inited!!!!!!!!!!!!"
        ;;
    *)
        echo $"Usage: $0 {start|stop|init| redisclusterpath ports}"
        ;;
esac
