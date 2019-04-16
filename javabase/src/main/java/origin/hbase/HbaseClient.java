package origin.hbase;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class HbaseClient {
    public static final String FAMILY = "f";
    public static final String COLUMN = "lk";
    @Autowired
    private HTablePool hTablePool;

    public HTablePool gethTablePool() {
        return hTablePool;
    }

    public void sethTablePool(HTablePool hTablePool) {
        this.hTablePool = hTablePool;
    }

//    public void get(String tableName, String rowkey) {
//        HTableInterface table = hTablePool.getTable(tableName);
//        try {
//            Get syncGet = new Get(Bytes.toBytes(rowkey));
//            try {
//                Result result = table.get(syncGet);
//                List<KeyValue> list = result.list();
//                for (KeyValue kv : list) {
//                    System.out.println("family:" + Bytes.toString(kv.getFamily()));
//                    System.out.println("qualifier:" + Bytes.toString(kv.getQualifier()));
//                    System.out.println("value:" + Bytes.toString(kv.getValue()));
//                    System.out.println("Timestamp:" + kv.getTimestamp());
//                }
//            } catch (IOException e) {
//            }
//        } finally {
//            try {
//                table.close();
//            } catch (IOException e) {
//            }
//        }
//    }

//    public List<String> scan(String tableName, String family, String column, String rowKey) {
//        List<String> result = new ArrayList<>();
//        try {
//            byte[] prefix = Bytes.toBytes(rowKey);
//            Scan scan = new Scan(prefix);
//            scan.setStartRow(Bytes.toBytes(rowKey));
//            scan.setStopRow(Bytes.add(rowKey.getBytes(), new byte[]{(byte) 0xff}));
//            scan.addColumn(Bytes.toBytes(family), Bytes.toBytes(column));
//            ResultScanner rs = null;
//            HTable table = (HTable) hTablePool.getTable(tableName);
//            try {
//                rs = table.getScanner(scan);
//                for (Result r : rs) {
//                    for (KeyValue kv : r.list()) {
//                        String value = Bytes.toString(kv.getValue());
//                        //long timestamp = kv.getTimestamp();
//                        result.add(StringUtils.trim(value));
//                    }
//                }
//            } finally {
//                rs.close();
//                table.close();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }

//
//    public Map<String, List<String>> getBatch(String tableName, String family, List<String> rowKeyLists) {
//        Map<String, List<String>> resultValue = new TreeMap<>();
//        try {
//            List<Get> getList = new ArrayList<>();
//            HTable table = (HTable) hTablePool.getTable(tableName);
//
//            for (String rowKey : rowKeyLists) {
//                Get get = new Get(Bytes.toBytes(rowKey));
//                get.addFamily(Bytes.toBytes(family));
//                getList.add(get);
//            }
//            try {
//                Result[] rsArray = table.get(getList);
//                int size = rsArray.length;
//                for (int i = 0; i < size; i++) {
//                    Result rs = rsArray[i];
//                    List<String> result = new ArrayList<>();
//                    if (rs != null && rs.list() != null) {
//                        for (KeyValue kv : rs.list()) {
//                            if (kv != null) {
//                                String value = Bytes.toString(kv.getValue());
//                                String[] dataArray = StringUtils.split(value, ",");
//                                if (dataArray.length >= 3) {
//                                    String userId = dataArray[0];
//                                    if (resultValue.containsKey(userId)) {
//                                        result = resultValue.get(userId);
//                                        result.add(StringUtils.trim(dataArray[2]));
//                                    } else {
//                                        result.add(StringUtils.trim(dataArray[2]));
//                                        resultValue.put(userId, result);
//                                    }
//                                }
//
//
//                            }
//                        }
//                    }
//
//                }
//
//            } finally {
//                table.close();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return resultValue;
//    }
//        public Map<String, List<String>> getBatch(String tableName, List<RowKeyAndColumn> rowKeyAndColumns, String family) {
//            Map<String, List<String>> result = new TreeMap<>();
//            try {
//                List<Get> getList = new ArrayList<Get>();
//                HTable table = (HTable) hTablePool.getTable(tableName);
//
//                for (RowKeyAndColumn rowKeyAndColumn : rowKeyAndColumns) {
//                    String rowKey = rowKeyAndColumn.getRowKey();
//                    Get get = new Get(Bytes.toBytes(rowKey));
//                    if (rowKeyAndColumn.getColumnList() != null && rowKeyAndColumn.getColumnList().size() != 0) {
//                        for (Integer column : rowKeyAndColumn.getColumnList()) {
//                            get.addColumn(Bytes.toBytes(family), Bytes.toBytes(String.valueOf(column)));
//                        }
//                    } else {
//                        get.addFamily(Bytes.toBytes(family));
//                    }
//                    getList.add(get);
//                }
//                try {
//                    Result[] rsArray = table.get(getList);
//                    for (Result rs : rsArray) {
//                        if (rs != null) {
//                            if (rs.getRow() != null) {
//                                String s = Bytes.toString(rs.getRow());
//                                List<String> values = null;
//                                if (result.containsKey(s)) {
//                                    values = result.get(s);
//                                } else {
//                                    values = new ArrayList<>();
//                                }
//                                if (rs.list() != null) {
//                                    for (KeyValue kv : rs.list()) {
//                                        if (kv != null) {
//                                            String value = Bytes.toString(kv.getValue());
//                                            values.add(StringUtils.trim(value));
//                                        }
//                                    }
//                                }
//                                result.put(s, values);
//                            }
//                        }
//                    }
//
//                } finally {
//                    table.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return result;
//        }

//
//        public List<String> getBatchData(String tableName, List<RowKeyAndColumn> rowKeyAndColumns, String family) {
//            List<String> result = new ArrayList<>();
//            try {
//                List<Get> getList = new ArrayList<Get>();
//                HTable table = (HTable) hTablePool.getTable(tableName);
//
//                for (RowKeyAndColumn rowKeyAndColumn : rowKeyAndColumns) {
//                    String rowKey = rowKeyAndColumn.getRowKey();
//                    Get get = new Get(Bytes.toBytes(rowKey));
//                    if (rowKeyAndColumn.getColumnList() != null && rowKeyAndColumn.getColumnList().size() != 0) {
//                        for (Integer column : rowKeyAndColumn.getColumnList()) {
//                            get.addColumn(Bytes.toBytes(family), Bytes.toBytes(String.valueOf(column)));
//                        }
//                    } else {
//                        get.addFamily(Bytes.toBytes(family));
//                    }
//                    getList.add(get);
//                }
//                try {
//                    Result[] rsArray = table.get(getList);
//                    for (Result rs : rsArray) {
//                        if (rs != null) {
//                            if (rs.list() != null) {
//                                for (KeyValue kv : rs.list()) {
//                                    if (kv != null) {
//                                        String value = Bytes.toString(kv.getValue());
//                                        result.add(StringUtils.trim(value));
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                } finally {
//                    table.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return result;
//        }
}
