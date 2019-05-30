package origin.utils;

import java.util.List;

/**
 * @Author:qishan
 * @Date: 2019-05-07
 * @Desc:
 **/
public class Geo {
    public static float calculateArea(List<LonLat> paramList) {
        if ((paramList == null) || (paramList.size() < 3)) {
            return 0.F;
        }
        double d1 = 0.0D;
        int i = paramList.size();
        for (int j = 0; j < i; j++) {
            LonLat localLatLng1 = paramList.get(j);
            LonLat localLatLng2 = paramList.get((j + 1) % i);
            double d2 = localLatLng1.getX() * 111319.49079327357D * Math.cos(localLatLng1.getY() * 0.017453292519943295D);
            double d3 = localLatLng1.getY() * 111319.49079327357D;
            double d4 = localLatLng2.getX() * 111319.49079327357D * Math.cos(localLatLng2.getY() * 0.017453292519943295D);
            double d5 = localLatLng2.getY() * 111319.49079327357D;
            d1 += d2 * d5 - d4 * d3;
        }
        return (float) Math.abs(d1 / 2.0D);
    }

    public static  class LonLat {
        public static Double ONE = 1000000D;
        private Double x;
        private Double y;

        public static Double getONE() {
            return ONE;
        }

        public static void setONE(Double ONE) {
            LonLat.ONE = ONE;
        }

        public Double getX() {
            return x;
        }

        public void setX(Double x) {
            this.x = x;
        }

        public Double getY() {
            return y;
        }

        public void setY(Double y) {
            this.y = y;
        }
    }
}
