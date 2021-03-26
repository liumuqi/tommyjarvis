package origin.redisson;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * @Author:mlamp
 * @Date: 2021/3/12
 * @Desc:
 **/
public class Redissons {
    public static void main(String[] args) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        // config.useSingleServer().setPassword("123456");
        RedissonClient redissonClient = Redisson.create(config);
        // 获取一个redis key为users的布隆过滤器
        RBloomFilter<Integer> bloomFilter = redissonClient.getBloomFilter("users");
        // 假设元素个数为10万
        int size = 100000;
        // 进行初始化，预计元素为10万，误差率为1%
        bloomFilter.tryInit(size, 0.01);
        // 将1至100000这十万个数映射到布隆过滤器中
        for (int i = 1; i <= size; i++) {
            bloomFilter.add(i);
        }
        // 检查已在过滤器中的值，是否有匹配不上的
        for (int i = 1; i <= size; i++) {
            if (!bloomFilter.contains(i)) {
                System.out.println("存在不匹配的值：" + i);
            }
        }
        // 检查不在过滤器中的1000个值，是否有匹配上的
        int matchCount = 0;
        for (int i = size + 1; i <= size + 1000; i++) {
            if (bloomFilter.contains(i)) {
                matchCount++;
            }
        }
        System.out.println("误判个数：" + matchCount);
    }

    public static void guavaBloomFilter(String[] args) {
        // 假设元素个数为10万
        int size = 100000;
        // 预计元素为10万，误差率为1%
        BloomFilter<Integer> bloomFilter = BloomFilter.create(Funnels.integerFunnel(), size, 0.01);
        // 将1至100000这十万个数映射到布隆过滤器中
        for (int i = 1; i <= size; i++) {
            bloomFilter.put(i);
        }
        // 检查已在过滤器中的值，是否有匹配不上的
        for (int i = 1; i <= size; i++) {
            if (!bloomFilter.mightContain(i)) {
                System.out.println("存在不匹配的值：" + i);
            }
        }
        // 检查不在过滤器中的1000个值，是否有匹配上的
        int matchCount = 0;
        for (int i = size + 1; i <= size + 1000; i++) {
            if (bloomFilter.mightContain(i)) {
                matchCount++;
            }
        }
        System.out.println("误判个数：" + matchCount);

    }
}
