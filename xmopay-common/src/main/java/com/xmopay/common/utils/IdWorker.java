package com.xmopay.common.utils;

/**
 * 根据twitter的snowflake算法生成唯一ID
 * snowflake算法 64 位
 * 0---0000000000 0000000000 0000000000 0000000000 0 --- 00000 ---00000 ---000000000000
 * 第一位为未使用（实际上也可作为long的符号位），接下来的41位为毫秒级时间，然后5位datacenter标识位，
 * 5位机器ID（并不算标识符，实际是为线程标识），然后12位该毫秒内的当前毫秒内的计数，加起来刚好64位，为一个Long型。
 * 其中datacenter标识位起始是机器位，机器ID其实是线程标识，可以同一一个10位来表示不同机器
 */
public class IdWorker {

    private final long twepoch = 1288834974657L;  //唯一时间，这是一个避免重复的随机量，自行设定不要大于当前时间戳
    private final long workerIdBits = 5L; //机器码字节数。5个字节用来保存机器码
    private final long datacenterIdBits = 5L;
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits); //最大机器ID
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
    private final long sequenceBits = 12L; //计数器字节数，10个字节用来保存计数码
    private final long workerIdShift = sequenceBits; //机器码数据左移位数，就是后面计数器占用的位数
    private final long datacenterIdShift = sequenceBits + workerIdBits; //时间戳左移动位数就是机器码和计数器总字节数
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private final long sequenceMask = -1L ^ (-1L << sequenceBits); //一微秒内可以产生计数，如果达到该值则等到下一微妙在进行生成

    private long workerId; //机器ID
    private long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public IdWorker(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public synchronized long nextId() {
        long timestamp = timeGen();
        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) { //如果当前时间戳比上一次生成ID时时间戳还小，抛出异常，因为不能保证现在生成的ID之前没有生成过
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        //如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {//同一微妙中生成ID
            sequence = (sequence + 1) & sequenceMask; //用&运算计算该微秒内产生的计数是否已经到达上限
            //毫秒内序列溢出
            if (sequence == 0) {//一微妙内产生的ID计数已达上限，等待下一微妙
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {//不同微秒生成ID
            sequence = 0L;//计数清0
        }

        lastTimestamp = timestamp;//把当前时间戳保存为最后生成ID的时间戳

        return ((timestamp - twepoch) << timestampLeftShift) | (datacenterId << datacenterIdShift) | (workerId << workerIdShift) | sequence;
    }

    //获取下一微秒时间戳
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    //生成当前时间戳
    protected long timeGen() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        IdWorker idWorker = new IdWorker(1, 5);
        for (int i = 0; i < 10; i++) {
            long id = idWorker.nextId();
            System.out.println(id + " " + (id+"").length());
        }
    }
}
