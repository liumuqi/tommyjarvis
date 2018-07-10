package excel;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author muqi.lmq
 * @date 23/05/2017.
 */
public class ExcelProcessor {
    private static ExcelProcessor eu = new ExcelProcessor();

    private ExcelProcessor() {
    }

    public static ExcelProcessor getInstance() {
        return eu;
    }

    public Workbook write2Excel(String sheetName, List objs, Class clz) {
        Workbook wb = new SXSSFWorkbook();
        workbooking(wb, sheetName, objs, clz);
        return wb;
    }

    public Workbook appendExcel(Workbook wb, String sheetName, List objs, Class clz) {
        workbooking(wb, sheetName, objs, clz);
        return wb;
    }

    private void workbooking(Workbook wb, String sheetName, List objs, Class clz) {
        try {
            Sheet sheet = wb.createSheet(sheetName);
            Row r = sheet.createRow(0);
            List<ExcelHeader> headers = getHeaderList(clz);
            //写标题
            for (int i = 0; i < headers.size(); i++) {
                r.createCell(i).setCellValue(headers.get(i).getTitle());
            }
            //写数据
            Object obj = null;
            for (int i = 0; i < objs.size(); i++) {
                r = sheet.createRow(i + 1);
                obj = objs.get(i);
                for (int j = 0; j < headers.size(); j++) {
                    String property = BeanUtils.getProperty(obj, headers.get(j).getMethodName());
                    r.createCell(j).setCellValue(property);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * excel表头
     *
     * @param clz
     * @return
     */
    private List<ExcelHeader> getHeaderList(Class clz) {
        List<ExcelHeader> headers = new ArrayList<>();
        Field[] declaredFields = clz.getDeclaredFields();
        for (Field field : declaredFields) {
            String name = field.getName();
            ExcelResourceTitle annotation = field.getAnnotation(ExcelResourceTitle.class);
            if (annotation != null) {
                headers.add(new ExcelHeader(annotation.title(), annotation.order(), name));
            }
        }
        Collections.sort(headers);
        return headers;
    }
}
