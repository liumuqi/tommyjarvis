-- wrk的全局方法, 可以直接拿到lua中使用的
-- 生成整个request的string，例如：返回
-- GET / HTTP/1.1
-- Host: tool.lu
--function wrk.format(method, path, headers, body)

-- 获取域名的IP和端口，返回table，例如：返回 `{127.0.0.1:80}`
--function wrk.lookup(host, service)

-- 判断addr是否能连接，例如：`127.0.0.1:80`，返回 true 或 false
--function wrk.connect(addr)

--Setup阶段 setup是在线程创建之后，启动之前。
--function setup(threads)

-- thread提供了1个属性，3个方法
-- threads.addr 设置请求需要打到的ip
-- threads:get(name) 获取线程全局变量
-- threads:set(name, value) 设置线程全局变量
-- threads:stop() 终止线程


-- Running阶段
--function init(args)
-- 每个线程仅调用1次，args 用于获取命令行中传入的参数, 例如 --env=pre

--function delay()
-- 每个线程调用多次，发送下一个请求之前的延迟, 单位为ms

--function request()
-- 每个线程调用多次，返回http请求

--function response(status, headers, body)
-- 每个线程调用多次，返回http响应
--request = function()
--local username = randomString(8)
--return wrk.format(nil,nil, nil, '{"service": "'..username..'"}')
--end

--Done阶段
--可以用于自定义结果报表，整个过程中只执行一次
--function done(summary, latency, requests)
--latency.min              -- minimum value seen
--latency.max              -- maximum value seen
--latency.mean             -- average value seen
--latency.stdev            -- standard deviation
--latency:percentile(99.0) -- 99th percentile value
--latency(i)               -- raw value and count

--summary = {
--duration = N,  -- run duration in microseconds
--requests = N,  -- total completed requests
--bytes    = N,  -- total bytes received
--errors   = {
--connect = N, -- total socket connection errors
--read    = N, -- total socket read errors
--write   = N, -- total socket write errors
--status  = N, -- total HTTP status codes > 399
--timeout = N  -- total request timeouts
--}
--}


wrk.method = "POST"
wrk.body ='[]'
wrk.headers["Content-Encoding"] = "Content-Encoding:application/json;charset=utf-8"
wrk.headers["Content-Type"] = "application/json"



-- example script that demonstrates use of setup() to pass
-- data to and from the threads

--local charset = {}
--do -- [0-9a-zA-Z]
--for c = 48, 57  do table.insert(charset, string.char(c)) end
--for c = 65, 90  do table.insert(charset, string.char(c)) end
--for c = 97, 122 do table.insert(charset, string.char(c)) end
--end

--local function randomString(length)
--if not length or length <= 0 then return '' end
--math.randomseed(os.clock()^5)
--return randomString(length - 1) .. charset[math.random(1, #charset)]
--end

--[[
print_dump是一个用于调试输出数据的函数，能够打印出nil,boolean,number,string,table类型的数据，以及table类型值的元表
参数data表示要输出的数据
参数showMetatable表示是否要输出元表
参数lastCount用于格式控制，用户请勿使用该变量
]]
function print_dump(data, showMetatable, lastCount)
    if type(data) ~= "table" then
        --Value
        if type(data) == "string" then
            io.write("\"", data, "\"")
        else
            io.write(tostring(data))
        end
    else
        --Format
        local count = lastCount or 0
        count = count + 1
        io.write("{\n")
        --Metatable
        if showMetatable then
            for i = 1,count do io.write("\t") end
            local mt = getmetatable(data)
            io.write("\"__metatable\" = ")
            print_dump(mt, showMetatable, count)    -- 如果不想看到元表的元表，可将showMetatable处填nil
            io.write(",\n")     --如果不想在元表后加逗号，可以删除这里的逗号
        end
        --Key
        for key,value in pairs(data) do
            for i = 1,count do io.write("\t") end
            if type(key) == "string" then
                io.write("\"", key, "\" = ")
            elseif type(key) == "number" then
                io.write("[", key, "] = ")
            else
                io.write(tostring(key))
            end
            print_dump(value, showMetatable, count) -- 如果不想看到子table的元表，可将showMetatable处填nil
            io.write(",\n")     --如果不想在table的每一个item后加逗号，可以删除这里的逗号
        end
        --Format
        for i = 1,lastCount or 0 do io.write("\t") end
        io.write("}")
    end
    --Format
    if not lastCount then
        io.write("\n")
    end
end

local A1, A2 = 727595+os.clock(), 798405+os.clock()  -- 5^17=D20*A1+A2
local D20, D40 = 1048576, 1099511627776  -- 2^20, 2^40
local X1, X2 = 0, 1
function rand()
    local U = X2*A2
    local V = (X1*A2 + X2*A1) % D20
    V = (V*D20 + U) % D40
    X1 = math.floor(V/D20)
    X2 = V - X1*D20
    return V/D40
end
local function randomTime()
    math.randomseed(rand()*100000000)
    --offset=math.random(1,7)*86400
    offset=math.random(120,86400)
    startTime=os.date("%Y-%m-%d %X",1546272000)
    endTime=os.date("%Y-%m-%d %X",1546272000+offset)
    return {startTime,endTime}
end
for i=100,1,-1 do
    data= randomTime()
    --print(data[1])
    print(data[2])
end


local function urlEncode(s)
    s = string.gsub(s, "([^%w%.%- ])", function(c) return string.format("%%%02X", string.byte(c)) end)
    return string.gsub(s, " ", "+")
end

local function urlDecode(s)
    s = string.gsub(s, '%%(%x%x)', function(h) return string.char(tonumber(h, 16)) end)
    return s
end

local counter = 1
local threads = {}
function setup(thread)
    thread:set("id", counter)
    table.insert(threads, thread)
    counter = counter + 1
end

function init(args)
    requests  = 0
    responses = 0
    statuses = {}
    local msg = "threads %d created"
    print(msg:format(id))
end

function request()
    requests = requests + 1
    se=randomTime()
    local returnRequest = "/history/queryParam?startTime="..urlEncode(se[1]).."&endTime="..urlEncode(se[2]).."&accountId=122718&pageSize=200"
    --print(wrk.format(nil, returnRequest))
    return wrk.format(nil, returnRequest)
end

function response(status, headers, body)
    responses = responses + 1
    local s = statuses[tostring(status)]
    if  s == nil then
        statuses[tostring(status)]=1
        s=1
    end
    if s > 1 then
        statuses[tostring(status)]=s+1
    end
end

function done(summary, latency, requests)
    for index, thread in ipairs(threads) do
        local id        = thread:get("id")
        local requests  = thread:get("requests")
        local responses = thread:get("responses")
        local stat = thread:get("statuses")
        thread:stop()
        local s_s=""
        --for i,v in ipairs(stat) do s_s= s_s.."\ncode:"..i..": "..v end
        local msg = "threads %d made %d requests and got %d responses.\nstats: %s \n err_connect: %d , err_read: %d, err_write: %d, err_status: %d, err_timeout: %d"
        print(msg:format(id, requests, responses,s_s,summary.errors.connect,summary.errors.read,summary.errors.write,summary.errors.status,summary.errors.timeout))
        print("=============================")
    end
end
