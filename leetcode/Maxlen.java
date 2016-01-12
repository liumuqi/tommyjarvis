public class Maxlen {
    public int maxProduct(String[] words) {
        int len = words.length;
        int[] bit = new int[len];
        int[] wlen = new int[len];
        int max = 0;
        for (int i = 0; i < len; i++) {
            String word = words[i];
            int length = word.length();
            wlen[i] = length;
            for (int j = 0; j < length; j++) {
                bit[i] = bit[i] | (1 << word.charAt(j) - 'a');
            }
        }
    int i1=0;
        for (int i = 0; i < len - 1; i++) {
            for (int j = i+1; j < len; j++) {
                if ((bit[i] & bit[j]) == 0) {
                     i1= wlen[i] * wlen[j];
                    if (max < i1) {
                        max = i1;
                    }
                }
            }
        }
        return max;
    }
}
