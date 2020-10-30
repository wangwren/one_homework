package server;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 自定义ClassLoader
 * @author wwr
 */
public class WebAppClassLoader extends ClassLoader {

    /**
     * name class 类的文件名
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] datas = loadClassData(name);
        return defineClass(null, datas, 0, datas.length);
    }


    protected byte[] loadClassData(String name) {
        FileInputStream fis = null;
        byte[] datas = null;
        try {
            fis = new FileInputStream(name + ".class");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int b;
            while ((b = fis.read()) != -1) {
                bos.write(b);
            }
            datas = bos.toByteArray();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return datas;

    }

}