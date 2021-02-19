## NoSql

------

> 概念: Not only Sql ，非关系性数据库
>
> 关系型数据库: 表格， 行， 列
>
> 数据类型的存储不需要固定的格式，key-value形式存储 
>
> 数量级：读11万/秒，写8万/秒  

- 与传统的关系型数据库的关系

    ```sh
    传统的关系型数据库
    1. 结构化组织，数据和关系都存在单独的表中
    2. 严格的一致性
    3. 基础的事务
    ```

    ```sh
    NoSql
    1. 键值对存储，列存储，文档存储(mongodb)，图数据库(社交关系)
    2. 最终一致性(中间状态可以不一致)
    3. CAP定理和BASE 
    ```

- 分类

    ```sh
    1. 键值(key-value): Redis, Oracle BDB
    2. 列存储: HBase, Cassandra,分布式文件系统，将同一列数据存储在一起
    3. 文档型数据库： MongoDb, CouchDB
    4. 图数据库: Neo4J, InfoGrid, Ininite Graph 社交网络
    ```



## Redis

----

> Remote Dictionary Server 远程字典服务, 是一个开源(BSD许可)的，内存中的数据结构存储系统，它可以用作
>
> 数据库、缓存、消息中间件MQ.支持多种类型的数据结构，如字符串String,散列hash，列表lists，集合sets，有序集合sorted sets与范围查询。
>
> Redis内置了
>
> 1. 主从复制(replication), 
> 2. LUA脚本, 
> 3. LRU eviction， 
> 4. 事务(transactions)
> 5. 不同级别的 磁盘持久化(persistence), rdb, aof
> 6. 高可用 Redis Sentinel哨兵 和 Redis Cluster分片
>
> C语言编写，支持网路，基于内存，支持持久化的日志型，kv数据库.
>
> 
>
> 官网： https://redis.io/
>
> 中文网： https://www.redis.cn/

### 功能

1. 持久化(rdb, aof)
2. 高效率，用于高速缓存(cache)
3. 发布-订阅系统(消息中间件MQ)
4. 地图信息分析
5. 计数器系统(浏览量)-原子增 
6. 集群-高可用
7. 事务 



### 基本指令

----

```sh
## 1.清空内存数据
FLUSHALL
## 2.选择数据库
SELECT dbNUM[0-15]
## 3.设置过期时间
EXPIRE key seconds
ttl key 查看当前key的存活时间
## 4.删除key
del key
## 5.移动当前库的key到其他db
move key db
## 6.查看当前key的类型
type key

## 7.查看当前redis版本
redis-server --version
```



### 5大基础结构

------

#### string

```sh
127.0.0.1:6379[1]> set name ylj
OK
127.0.0.1:6379[1]> get name
"ylj"
127.0.0.1:6379[1]> append name ,niubi  ## 存在则追加，不存在则新建
(integer) 9
127.0.0.1:6379[1]> strlen name ## 字符串长度
(integer) 9
127.0.0.1:6379[1]> get name
"ylj,niubi"

## 截取字符串 getrange key start end
127.0.0.1:6379> get name
"ylj,niubi"
127.0.0.1:6379> getrange name 0 2
"ylj"

## 修改字符串 setrange key offset replacement
127.0.0.1:6379> get name
"ylj,niubi"
127.0.0.1:6379> setrange name 4 handsome 
(integer) 12
127.0.0.1:6379> get name
"ylj,handsome"


##### 原子增减（基于序列化操作）
incr num  					## 原子增1
decr num  					## 原子减1
incrby num increment ## 原子增步长increment
decr num increment  ## 原子减步长increment
```

```sh
## setex, setnx
setex (set with expire)  # 设置key同时有过期时间
setnx (set if not exist) # 如果不存在，则设置， ！！！！！！分布式锁中常用 reddison

127.0.0.1:6379> setnx flag hahahah   # 第一次设置，key不存在，成功--0
(integer) 1
127.0.0.1:6379> get flag
"hahahah"
127.0.0.1:6379> setnx flag hahahah   # 第二次设置，key已存在，不成功--1
(integer) 0
```

```sh
## 原子操作-批量
127.0.0.1:6379> mset k1 v1 k2 v2 k3 v3 k4 v4
OK
127.0.0.1:6379> mget k1 k2 k3 k4
1) "v1"
2) "v2"
3) "v3"
4) "v4"
127.0.0.1:6379> msetnx k1 v1 k4 v4  ## 展示原子性 k4虽然不存在，但是k1存在
(integer) 0
```

#### list

```sh
底层链表结构

## 栈
127.0.0.1:6379> LPUSH list one two three  # 左入栈
(integer) 3				
127.0.0.1:6379> RPUSH list zero haha      # 右入栈
(integer) 5

127.0.0.1:6379> lrange list 0 -1					# 遍历
1) "three"
2) "two"
3) "one"
4) "zero"
5) "haha"

127.0.0.1:6379> LPOP list									# 左出栈
"three"
127.0.0.1:6379> RPOP list									# 右出栈
"haha"

127.0.0.1:6379> lrange list 0 -1					# 索引值
1) "two"
2) "one"
3) "zero"
127.0.0.1:6379> lindex list 0
"two"
127.0.0.1:6379> lindex list 1
"one"
127.0.0.1:6379> lindex list 2
"zero"

127.0.0.1:6379> llen list									# list len 查看队列长度
(integer) 3

# 元素在两个栈队列中 转移 rpoplpush source destination
127.0.0.1:6379> lpush list hello1 hello2 hello3 hello4   # rpoplpush 
(integer) 4
127.0.0.1:6379> lrange list 0 -1
1) "hello4"
2) "hello3"
3) "hello2"
4) "hello1"
127.0.0.1:6379> rpoplpush list otherlist
"hello1"
127.0.0.1:6379> rpoplpush list otherlist
"hello2"
127.0.0.1:6379> lrange otherlist 0 -1
1) "hello2"
2) "hello1"

## 链表list修改
127.0.0.1:6379> lrem list 2 two						# 移除特定值，lrem key count value
(integer) 2
127.0.0.1:6379> lrange list 0 -1
1) "three"
2) "three"
3) "one"

127.0.0.1:6379> lrange list 0 -1					# 截取 hello world
1) "one"
2) "two"
3) "hello"
4) "world"
5) "three"
6) "four"
127.0.0.1:6379> ltrim list 3 4
OK

# 索引设置特定位置的值 lset key index value
127.0.0.1:6379> lrange otherlist 0 -1
1) "hello2"
2) "hello1"
127.0.0.1:6379> lset otherlist 1 item
OK
127.0.0.1:6379> lrange otherlist 0 -1
1) "hello2"
2) "item"

# 插入元素在 指定值的前后 linsert key BEFORE|AFTER pivot value
127.0.0.1:6379> lrange otherlist 0 -1
1) "hello2"
2) "item"
127.0.0.1:6379> linsert otherlist before item pivotelement
(integer) 3
127.0.0.1:6379> lrange otherlist 0 -1
1) "hello2"
2) "pivotelement"
3) "item"

```

#### set

```sh
# 集合中添加元素 set add
127.0.0.1:6379> sadd myset m1 m2 m3
(integer) 3
# 获取元素中值的数量 set card
127.0.0.1:6379> scard myset
(integer) 3
# 查看集合中的成员 set members
127.0.0.1:6379> smembers myset
1) "m3"
2) "m2"
3) "m1"
# 判断元素是否在集合中 set ismember
127.0.0.1:6379> sismember myset hello
(integer) 0
127.0.0.1:6379> sismember myset m1
(integer) 1
## 随机获取集合的成员 srandmember key [count]     抽随机
127.0.0.1:6379> srandmember myset 1
1) "m3"
127.0.0.1:6379> srandmember myset 2
1) "m2"
2) "m1"

## 随机删除 spop
127.0.0.1:6379> smembers myset
1) "m3"
2) "m2"
3) "m1"
127.0.0.1:6379> spop myset 1
1) "m3"

## 转移元素从一个集合到另一个集合			smove source destination member
127.0.0.1:6379> smove myset myotherset three
(integer) 1
127.0.0.1:6379> smembers myset
1) "one"
2) "thwo"
127.0.0.1:6379> smembers myotherset
1) "three"

## 集合差集
127.0.0.1:6379> sadd myset1 t1 t2 t3 t4
(integer) 4
127.0.0.1:6379> sadd myset2 t2 t3 t4 t5
(integer) 4
127.0.0.1:6379> sdiff myset1 myset2				## 集合myset1比集合myset2多的元素
1) "t1"
127.0.0.1:6379> sdiff myset2 myset1
1) "t5"

## 集合交集
127.0.0.1:6379> sinter myset1 myset2      ## 集合myset1和集合myset2共同拥有的元素
1) "t2"
2) "t4"
3) "t3"

## 集合并集
127.0.0.1:6379> sunion myset1 myset2
1) "t2"
2) "t5"
3) "t3"
4) "t1"
5) "t4"
```

#### hash

```sh
## 数据结构
key field1 value1  field2 value2  field3 value3

## 基础存取操作
127.0.0.1:6379> hset myhash f1 hello			
(integer) 1
127.0.0.1:6379> hset myhash f2 world 
(integer) 1
127.0.0.1:6379> hget myhash f1
"hello"
127.0.0.1:6379> hmset key field value [field value ...]	## 多值设置

## 获取大键下的所有k-v   hset key field value
127.0.0.1:6379> hset myhash f11 wowo
(integer) 1
127.0.0.1:6379> hset myhash f22 haha
(integer) 1
127.0.0.1:6379> hgetall myhash													
1) "f1"
2) "hello"
3) "f2"
4) "world"
5) "f11"
6) "wowo"
7) "f22"
8) "haha"

## 删除大键下的小键   hgetall key
127.0.0.1:6379> hgetall myhash
1) "f1"
2) "hello"
3) "f2"
4) "world"
5) "f11"
6) "wowo"
7) "f22"
8) "haha"
127.0.0.1:6379> hdel myhash f1 f2
(integer) 2
127.0.0.1:6379> hgetall myhash
1) "f11"
2) "wowo"
3) "f22"
4) "haha"

## 查看大键下的kv长度  hlen key
127.0.0.1:6379> hlen myhash
(integer) 2

## 判断key是否存在  hexists key field
127.0.0.1:6379> HEXISTS myhash f1
(integer) 0

## 获取特定key下的所有field， hkeys key
127.0.0.1:6379> hkeys myhash
1) "f11"
2) "f22"

## 获取特定key下的所有value，  hvals key
127.0.0.1:6379> hvals myhash
1) "wowo"
2) "haha"

## 原子增减	hincrby key field increment
127.0.0.1:6379> hset num id1 1
(integer) 1
127.0.0.1:6379> hincrby num id1 1
(integer) 2
（没有命令：hincr、hdecry、hdecriby、hdecribyfloat

## 幂等创建 hsetnx key field value  用在分布式锁
127.0.0.1:6379> hkeys myhash
1) "f11"
2) "f22"
127.0.0.1:6379> hsetnx myhash f33 ylj
(integer) 1
127.0.0.1:6379> hsetnx myhash f33 niubi
(integer) 0
```

#### zset

```sh
## 添加 zadd key [NX|XX] [CH] [INCR] score member [score member ...]
127.0.0.1:6379> zadd myset 3 three
(integer) 1
127.0.0.1:6379> zadd myset 5 five
(integer) 1
127.0.0.1:6379> zadd myset 2 two
(integer) 1
127.0.0.1:6379> zrange myset 0 -1
1) "one"
2) "two"
3) "three"
4) "five"

## 删除 zrem key member [member ...]
127.0.0.1:6379> zrem grade z1
(integer) 1


## 按分数排序 zrangebyscore key min max [WITHSCORES] [LIMIT offset count]
## min 查询范围下限 -inf 为负无穷
## max 查询范围上限 +inf 为正无穷
## WITHSCORES 显示分数
## limit offset count 分页参数，类似sql
127.0.0.1:6379> zadd salary 1700 ylj
(integer) 1
127.0.0.1:6379> zadd salary 1200 wlb 
(integer) 1
127.0.0.1:6379> zadd salary 699 jay
(integer) 1
127.0.0.1:6379> zrangebyscore salary -inf +inf
1) "jay"
2) "wlb"
3) "ylj"

## 获取zset中的个数
127.0.0.1:6379> zcard grade
(integer) 5

## 统计指定分数区间的数量 zcount key min max
127.0.0.1:6379> zrange myset 0 -1 withscores
1) "one"
2) "1"
3) "two"
4) "2"
5) "three"
6) "3"
7) "five"
8) "5"
127.0.0.1:6379> zcount myset 2 3
(integer) 2


```



### 3大特殊数据

------

#### geospatial

>redis维护一个地球的数学模型，存入地理位置信息，可以做距离计算

```sh
## 添加城市经纬度 
## geoadd key longitude latitude member [longitude latitude member ...]
127.0.0.1:6379> geoadd china:city 116.23128 40.22077 beijing
(integer) 1
127.0.0.1:6379> geoadd china:city 121.48941 31.40527 shanghai
(integer) 1
127.0.0.1:6379> geoadd china:city 120.21201 30.2084 hangzhou
(integer) 1
127.0.0.1:6379> geoadd china:city 106.54041 29.40268 chongqin
(integer) 1

## 查询城市经纬度
127.0.0.1:6379> geopos china:city hangzhou
1) 1) "120.21200805902481079"
   2) "30.20839995425554747"

## 查询两个城市之间的位置距离
## geodist key member1 member2 [unit]
## m 表示单位为米。缺省值
## km 表示单位为千米。
## mi 表示单位为英里。
## ft 表示单位为英尺。
127.0.0.1:6379> geopos china:city hangzhou
1) 1) "120.21200805902481079"
   2) "30.20839995425554747"

## 查询指定坐标半径内的城市 e.g查询距离绍兴半径在1500km内的城市
## eoradius key longitude latitude radius m|km|ft|mi [WITHCOORD] [WITHDIST] [WITHHASH] [COUNT count] [ASC|DESC] [STORE key] [STOREDIST key]
georadius china:city 120.49476 30.08189 1500 km WITHDIST ASC
127.0.0.1:6379> georadius china:city 120.49476 30.08189 1500 km WITHDIST
1) 1) "chongqin"
   2) "1348.9055"
2) 1) "hangzhou"
   2) "30.6207"
3) 1) "shanghai"
   2) "175.2320"
4) 1) "beijing"
   2) "1192.0841"

## 查询距离杭州最近的一个城市
## georadiusbymember key member radius m|km|ft|mi [WITHCOORD] [WITHDIST] [WITHHASH] [COUNT count] [ASC|DESC] [STORE key] [STOREDIST key]
127.0.0.1:6379> georadiusbymember china:city hangzhou 800 km COUNT 1
1) "hangzhou"

## 查询特定城市的hash(由longitude, latitude 计算而得，有损)
## geohash key member [member ...]
127.0.0.1:6379> geohash china:city hangzhou chongqing beijing shanghai
1) "wtm7z7r8wv0"
2) (nil)
3) "wx4sucvncn0"
4) "wtw6st1uuq0"


## geo数据结构底层是zset
127.0.0.1:6379> zrange china:city 0 -1 WITHSCORES
1) "chongqin"
2) "4026046519194590"
3) "hangzhou"
4) "4054122608102013"
5) "shanghai"
6) "4054807796443227"
7) "beijing"
8) "4069896088584598"
```

#### hyperloglog

> Redis 在 2.8.9 版本添加了 HyperLogLog 结构。
>
> Redis HyperLogLog 是用来做基数统计的算法，HyperLogLog 的优点是，在输入元素的数量或者体积非常非常大时，计算基数所需的空间总是固定 的、并且是很小的。
>
> 在 Redis 里面，每个 HyperLogLog 键只需要花费 12 KB 内存，就可以计算接近 2^64 个不同元素的基 数。这和计算基数时，元素越多耗费内存就越多的集合形成鲜明对比。
>
> 但是，因为 HyperLogLog 只会根据输入元素来计算基数，而不会储存输入元素本身，所以 HyperLogLog 不能像集合那样，返回输入的各个元素。
>
> 什么是基数?
>
> 比如数据集 {1, 3, 5, 7, 5, 7, 8}， 那么这个数据集的基数集为 {1, 3, 5 ,7, 8}, 基数(不重复元素)为5。 基数估计就是在误差可接受的范围内，快速计算基数。

- 应用 

    用来统计网站UV(Unique View): 独立访客， redis的hyperLoglog在固定内存的12k的前提下，达到了统计2^64的人次，虽然官方宣称有0.81%的错误率，因为是统计可以接受

- ```sh
    ## 添加
    127.0.0.1:6379> PFADD myset1 a b c d e f g g g 
    (integer) 1
    ## 基数统计
    127.0.0.1:6379> PFCOUNT myset1 
    (integer) 7
    127.0.0.1:6379> PFADD myset2 f f g g g h i j k 
    (integer) 1
    127.0.0.1:6379> PFCOUNT myset2
    (integer) 6
    ## 融合基数
    127.0.0.1:6379> PFMERGE myset3 myset1 myset2
    OK
    127.0.0.1:6379> PFCOUNT myset3
    (integer) 11
    ```

#### bitmap

>位操作，类似string，可以根据offset操作bit

```sh
## 统计id=1的员工，1周的打卡情况
127.0.0.1:6379> setbit user:sign:id1 0 1	## 周日打卡
(integer) 0
127.0.0.1:6379> setbit user:sign:id1 1 0  ## 周一打卡
(integer) 0
127.0.0.1:6379> setbit user:sign:id1 2 1
(integer) 0
127.0.0.1:6379> setbit user:sign:id1 3 1
(integer) 0
127.0.0.1:6379> setbit user:sign:id1 4 0
(integer) 0
127.0.0.1:6379> setbit user:sign:id1 5 1
(integer) 0
127.0.0.1:6379> setbit user:sign:id1 6 1
(integer) 0
127.0.0.1:6379> getbit user:sign:id1 4		## 获取周四的打卡情况
(integer) 0
127.0.0.1:6379> getbit user:sign:id1 6
(integer) 1
127.0.0.1:6379> bitcount user:sign:id1    ## 统计
(integer) 5
```



### 事务

------

>关系型数据库-事务基本特性 ACID
>
>1. Atomic 原子
>2. Consistency 一致性
>3. isolation 隔离性
>4. durability 持久性
>
>redis事务-一次执行多条命令，要么全部执行，不么全部不执行
>
>1. 串行性: 按入队的顺序按序执行命令
>2. 排他性: 一个事务在执行过程中，不会被其他客户端发来的命令所打断
>3. 一次性: multi,exec 后，事务执行后不能被重复执行
>4. 队列中的每一条命令保证原子性，但是整个事务不保证原子操作，只是一次性执行而已
>
>redis事务优点批处理的味道

#### 基础流程

```sh
## redis事务执行过程
开始事务。
命令入队。
执行事务。
## redis事务执行过程
127.0.0.1:6379> MULTI								## 开启事务
OK
127.0.0.1:6379> SET name yanglujian ## 命令入队
QUEUED
127.0.0.1:6379> get name
QUEUED
127.0.0.1:6379> sadd myset id1
QUEUED
127.0.0.1:6379> sadd myset id2
QUEUED
127.0.0.1:6379> smembers myset
QUEUED
127.0.0.1:6379> EXEC								## 执行
1) OK
2) "yanglujian"
3) (integer) 1
4) (integer) 1
5) 1) "id1"
   2) "id2"

## discard 取消事务，事务开启后入队的命令都不会被执行
```

```sh
## 编译期错误--整个事务不会被执行
127.0.0.1:6379> multi
OK
127.0.0.1:6379> getset name ylj
QUEUED
127.0.0.1:6379> getset ylj
(error) ERR wrong number of arguments for 'getset' command
127.0.0.1:6379> set user u1
QUEUED
127.0.0.1:6379> exec
(error) EXECABORT Transaction discarded because of previous errors.
127.0.0.1:6379> get user   ## 证明上面的事务没有被执行
(nil)

## 运行期错误--除了错误的那条命令外，其余正常执行
127.0.0.1:6379> multi
OK
127.0.0.1:6379> set name 'ylj'
QUEUED
127.0.0.1:6379> incr name
QUEUED
127.0.0.1:6379> set user id1
QUEUED
127.0.0.1:6379> exec
1) OK
2) (error) ERR value is not an integer or out of range
3) OK
127.0.0.1:6379> get user	## 说明运行期错误命令后面的命令依旧会被顺利执行
"id1"
```

#### 乐观锁

> CAS思想: 事务执行前,watch一个键值，执行事务时若监视的key发生了变化，则事务不执行，重新获取键值
>
> 更新之前查看version ，redis Watch命令实现
>
> [WATCH key [key ...\]](https://www.redis.net.cn/order/3642.html) 监视一个(或多个) key ，如果在事务执行之前这个(或这些) key 被其他命令所改动，那么事务将被打断。

```sh
## 事务场景:转账
## money 100
## watch multi ... exec ...  
127.0.0.1:6379> set money 100			## 余额
OK
127.0.0.1:6379> set out 0					## 转出金额
OK


127.0.0.1:6379> watch money				## 监控money
OK
127.0.0.1:6379> multi							## 在此连接的事务期间，另一个连接操作了money，使得money变为90
OK
127.0.0.1:6379> decrby money 20
QUEUED
127.0.0.1:6379> incrby out 20
QUEUED
127.0.0.1:6379> exec							## nil表示不执行, 后续使用unwatch,取消监控，在重新走流程
(nil)

```



### 整合SpringBoot

----

> 使用spring-data下的spring-data-redis
>
>SpringBoot2.x之后，原来使用的jedis被替换为lettuce
>
>Jedis: 采用的直接创建connect，多个线程操作会不安全，使用jedis pool池化技术可以避免， 更像BIO模式
>
>Lettuce: 采用netty技术，connect可以在多个线程中共享，更像NIO模式

### redis.conf配置文件

------

#### 基本

```sh
# 在client会话中，发送config命令来修改配置，
config get requirepass ## 查询当前配置文件的密码配置
config set requirepass newpwd ## 给当前redis实例设置新的密码

## 当命令中要指定内存单位，规则如下，大小写不敏感
# 1k => 1000 bytes
# 1kb => 1024 bytes
# 1m => 1000000 bytes
# 1mb => 1024*1024 bytes
# 1g => 1000000000 bytes
# 1gb => 1024*1024*1024 bytes
#
# units are case insensitive so 1GB 1Gb 1gB are all the same.

# 在client会话中使用save命令可以出发rdb持久化
save

# redis-server 后台进程退出
SHUTDOWN

# 开启redis-server
redis-server redis.conf

```



#### 网络-NETWORK

- bind 127.0.0.1，只能在本地连接，注释后可以解除 或者 bind 0.0.0.0 绑定到本机的所有IP
-  port 6379
- protected-mode yes 保护模式，一般默认开启，不去动

#### 通用-GENERAL

```sh
daemonize yes ## 默认是no，自己设置成yes，后台运行
pidfile /var/run/redis_6379.pid ## 若server以后台进程的形式来运行，则会将pid写入到该文件
supervised no ## 管理守护进程选项 一般不改动，默认no

## 日志级别以及日志文件路径
# Specify the server verbosity level.
# This can be one of:
# debug (a lot of information, useful for development/testing)
# verbose (many rarely useful info, but not a mess like the debug level)
# notice (moderately verbose, what you want in production probably)
# warning (only very important / critical messages are logged)
loglevel notice
# Specify the log file name. Also the empty string can be used to force
# Redis to log on the standard output. Note that if you use standard
# output for logging but daemonize, logs will be sent to /dev/null
logfile /var/log/redis/redis.log

databases 16  ## 默认的数据库数量，且缺省库为0号库

always-show-logo yes ## 是否显示redislogo 不用管
```

#### 快照-SNAPSHOTTING

```sh
## 触发持久化条件 rdb
save 900 1     ## 如果900秒内，且至少有1个key被修改，就进行持久化
save 300 10
save 60 10000

## 后台持久化程序出错，redis是否停止服务，默认是yes 停止服务
stop-writes-on-bgsave-error yes

## rdb文件是否需要压缩，要额外的cpu资源
rdbcompression yes 

## 校验和，保证了数据的强一致，但是在saving和loading时会损耗10%的性能 
rdbchecksum yes 

## rdb文件的dump的路径和名称
dbfilename dump.rdb
dir /var/lib/redis

```

#### 主从复制-REPLICATION

- slaveof host ip 选择master
- master 可以读写， slave只读
- 缺点: 当master宕机时，运维人员需要手动将其中一个slave 执行命令slaveof no one，成为master

#### 安全-SECURITY

```sh
## 设置密码,默认是没有密码的
requirepass foobared
```

#### 极限情况-LIMITS

```sh
## 最大连接的客户端数量
maxclients 10000

## 最大使用内存
maxmemory <bytes>

## 达到最大内存后的策略 6种
maxmemory-policy noeviction
1、volatile-lru：只对设置了过期时间的key进行LRU,Least Recently Used,最近最少使用
2、allkeys-lru ： 删除lru算法的key   
3、volatile-random：随机删除即将过期key   
4、allkeys-random：随机删除   
5、volatile-ttl ： 删除即将过期的   
6、noeviction ： 永不过期，redis拒绝服务，返回错误 (默认)
```

#### AOF-APPEND ONLY

```SH
## 模式开启标记，默认是不开启的，使用rdb模式进行持久化，足够用了
appendonly no
appendfilename "appendonly.aof" 

## 触发
# appendfsync always  ## 每次修改触发
appendfsync everysec  ## 每秒执行1次
# appendfsync no      ## 不执行同步，由操作系统来自动同步，基本不用

```



### 持久化

------

#### rdb

Redis Database

<img src="https://yljnote.oss-cn-hangzhou.aliyuncs.com/2021-02-18-083845.jpg" alt="image-20210218163714191" style="zoom:50%;" />

```sh
## 触发规则
1. save命令后会触发save
2. flushdb 命令会触发save
3. 退出shutdown 命令会触发save
4. 达到配置的触发条件后 会触发save
save 900 1     ## 如果900秒内，且至少有1个key被修改，就进行持久化
save 300 10
save 60 10000

## 启动redis-server时候，若没有指定redis.conf文件，会自动在当前目录下load dump.rdb数据库
redis-server /to/conf/redis.conf ## 服务启动时需要指定配置文件，且在conf文件中，指定rdb的路径

## 在cli端命令查看rdb配置路径
127.0.0.1:6379> config get dir
1) "dir"
2) "/usr/bin"

## 优点: 
父进程接受client请求，fork子进程执行rdb备份(dump.rdb)。
默认的配置下，dump.rdb文件会在redis-server的同级目录下。
```

#### aof 

Apend Only File

<img src="https://yljnote.oss-cn-hangzhou.aliyuncs.com/2021-02-18-084844.png" alt="image-20210218164844041" style="zoom:50%;" />

```sh
## 过程
aof 模式会将所有的 写命令 以日志的形式记录下来，不记录读命令。
当重新启动实例时，会根据 日志 重新构建出内存数据。
所以不适合大数据场景下，rdb更适合。

## 使用
appendonly yes  			## 修改配置yes即可
# appendfsync always  ## 每次修改触发
appendfsync everysec  ## 每秒将缓存日志同步
# appendfsync no      ## 不执行同步，由操作系统来自动同步，基本不用
```



#### 拓展

1. RDB持久化方式能够在指定的时间间隔内对你的数据进行快债
2. AOF几乎不用
3. 若redis只作为高速缓存的话，可以不进行持久化。
4. 同时开启两种持久化方式
    - redis重启会优先载入AOF文件来恢复原始数据，因为通常aof文件的数据完整性要比rdb要好。
    - 虽然如此，也不建议关闭rdb，rdb文件方便备份。aof不停在追加变化中。
5. 性能建议
    - rdb只用作后备用途，建议只在Slave上持久化RDB文件，且只保留 save 900 1 这条规则，表示15分钟有1条修改命令就持久化。
    - aof的好处是极端情况下，数据也就丢失了1秒。风险是 1. 持续的io 2. rewrite的最后将新数据写到新文件会有阻塞。建议临界值由64M改为5G以上。
    - 不启用aof，靠Master-Slave Replication实现高可用可不错，能省掉IO开销，也减小了rewrite时系统的波动。风险是： M/S同时宕机会丢失十几分钟的数据。启动时，也要比较Master/Slaver中的RDB文件，会载入最新的那个版本。微博就是这种架构。

### 主从复制和哨兵

----

![ ](https://yljnote.oss-cn-hangzhou.aliyuncs.com/2021-02-18-130430.png)



```sh
slaveof host port
```





### 发布订阅模式

----

 >1. Subscriber 订阅channel ，监听
 >2. publish 发布消息到channel

订阅者

```sh
127.0.0.1:6379> SUBSCRIBE ccc
Reading messages... (press Ctrl-C to quit)
1) "subscribe"
2) "ccc"
3) (integer) 1
1) "message"
2) "ccc"
3) "hello,man!!!"
1) "message"
2) "ccc"
3) "hello,ylj"
```

发布者

```sh
127.0.0.1:6379> PUBLISH ccc "hello,man!!!"
(integer) 1
127.0.0.1:6379> PUBLISH ccc "hello,ylj"
(integer) 1
127.0.0.1:6379> PUBLISH ccc "hello,redis"
(integer) 1
127.0.0.1:6379> 
```

 

### 运维

------

#### 1.查看rdb路径

```sh
1. 方式一
server-cli -p 6379 建立会话
config get dir 获取rdb路径

2. 方式二
ps -ef|grep redis  ## 得到了进程号 xxxx
ls -l /proc/xxxx/cwd
```

#### 2.查看实例的redis.conf位置

```sh
1. 登录redis
2. info Server
```

#### 3. 创建redis实例使用已存在rdb

```sh
## 若不指定redis.conf，默认在当前目录下load dump.rdb文件
redis-server 

## 指定redis.conf，加载已存在的dump.rdb
redis-server /to/redis/redis.conf
```

#### 4. 重启redis服务

```sh
## 方式一. 
shutdown关闭
redis-server /to/redis/redis.conf 启动

## 方式二

```

