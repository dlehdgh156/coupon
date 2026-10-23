local stockKey = KEYS[1]
local issueKey = KEYS[2]
local userId = ARGV[1]

-- 1. 중복 체크
if redis.call('SISMEMBER', issueKey, userId) == 1 then
return -1
end

-- 2. 재고 차감
local remain = redis.call('DECR', stockKey)
if remain < 0 then
redis.call('INCR', stockKey)
return -2
end

-- 3. 발급 기록
redis.call('SADD', issueKey, userId)
return remain

