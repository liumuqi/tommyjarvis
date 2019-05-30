package argrith;

/**
 * @Author:qishan
 * @Date: 2019-05-30
 * @Desc:
 **/
public class BitOps {
    /**
     * #136 根据题目描述，由于加上了时间复杂度必须是 O(n) ，并且空间复杂度为 O(1) 的条件，因此不能用排序方法，也不能使用 map 数据结构。答案是使用 位操作Bit Operation 来解此题。
     * 将所有元素做异或运算，即a[1] ⊕ a[2] ⊕ a[3] ⊕ …⊕ a[n]，所得的结果就是那个只出现一次的数字，时间复杂度为O(n)。
     * 根据异或的性质 任何一个数字异或它自己都等于 0
     *
     * @param nums 数组
     * @return 结果
     */
    private int solution(int[] nums) {
        int res = 0;
        for (int num : nums) {
            res ^= num;
        }
        return res;
    }

    /**
     * #137 给定一个非空整数数组，除了某个元素只出现一次以外，其余每个元素均出现了三次。找出那个只出现了一次的元素。
     * 这里使用了异或、与、取反这些运算
     *
     * @param nums 数组
     * @return 出现一次的数字
     */
    public int singleNumber2(int[] nums) {
        // 这里我们改一下变量名
        // 用 one 记录到当前处理的元素为止，二进制1出现“1次”（mod 3 之后的 1）的有哪些二进制位；
        // 用 two 记录到当前计算的变量为止，二进制1出现“2次”（mod 3 之后的 2）的有哪些二进制位。
        int one = 0, two = 0;
        int mask;
        for (int num : nums) {
            // 由于 two 要考虑，one 的已有状态，和当前是否继续出现。所以要先算
            two ^= one & num;
            // one 就是一个0,1的二值位，在两个状态间转换
            one ^= num;
            // 当 one 和 two 中的某一位同时为1时表示该二进制位上1出现了3次，此时需要清零。
            mask = ~(one & two);
            // 清零操作
            one &= mask;
            two &= mask;
        }
        // 即用 二进制 模拟 三进制 运算。最终 one 记录的是最终结果。
        return one;
    }

    /**
     * #260 \
     * 给定一个整数数组 nums，其中恰好有两个元素只出现一次，其余所有元素均出现两次。 找出只出现一次的那两个元素。
     * <p>
     * 示例 :
     * 输入: [1,2,1,3,2,5]
     * 输出: [3,5]
     * 在这里把所有元素都异或，那么得到的结果就是那两个只出现一次的元素异或的结果。
     * 然后，因为这两个只出现一次的元素一定是不相同的，所以这两个元素的二进制形式肯定至少有某一位是不同的，即一个为 0 ，另一个为 1 ，现在需要找到这一位。
     * 根据异或的性质 任何一个数字异或它自己都等于 0 ，得到这个数字二进制形式中任意一个为 1 的位都是我们要找的那一位。
     * 再然后，以这一位是 1 还是 0 为标准，将数组的 n 个元素分成两部分。
     * 1. 将这一位为 0 的所有元素做异或，得出的数就是只出现一次的数中的一个
     * 2. 将这一位为 1 的所有元素做异或，得出的数就是只出现一次的数中的另一个。
     * 这样就解出题目。忽略寻找不同位的过程，总共遍历数组两次，时间复杂度为O(n)。
     * <p>
     * 使用位运算
     *
     * @param nums 数组
     * @return 只出现一次数字的数组
     */
    public int[] singleNumber3(int[] nums) {
        int diff = 0;
        for (int num : nums) {
            diff ^= num;
        }
        // 得到最低的有效位，即两个数不同的那一位
        diff &= -diff;
        int[] result = new int[2];
        for (int num : nums) {
            if ((num & diff) == 0) {
                result[0] ^= num;
            } else {
                result[1] ^= num;
            }
        }
        return result;
    }
}
