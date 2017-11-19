#!/bin/bash
sleep 10
start_time=`date +%s`
while true
do
    r_host=${HOSTS[$(($RANDOM%10))]}
    r_port=${PORTS[$(($RANDOM%16))]}
    if [ $fail_host == $r_host  ] && [ $fail_port == $r_port  ]; then
        continue
    fi  
    fail_flag=`redis-cli -p $r_port -h $r_host cluster nodes|grep "$host:$port"|grep fail?|wc -l`
    if [ $? -ne 0  ] || [ $fail_flag -ne 0  ]; then
        sleep 1
    else
        break
    fi  
done
end_time=`date +%s`
echo "fail detect cost:$((end_time - start_time + 10))"
