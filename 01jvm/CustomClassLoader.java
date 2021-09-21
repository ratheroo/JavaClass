package com.java.course.class1.jvm;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Base64;

@SpringBootApplication
public class CustomClassLoader extends ClassLoader
{
    public static void main(String[] args) throws Exception{
        Class<?> xlass = new CustomClassLoader().findClass("Hello");
        Object instance = xlass.newInstance();
        Method method = xlass.getMethod("hello");
        method.invoke(instance);
    }

    @Override
    public  Class<?> findClass(String name) throws ClassNotFoundException{
        // 获取xlass文件输入流
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(name + ".xlass");
        try
        {
            // 读取文件内容
            int length = inputStream.available();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            // 解码
            byte[] decodeBytes = decode(bytes);
            return defineClass(name, decodeBytes, 0, decodeBytes.length);
        }
        catch (IOException e)
        {
            throw new ClassNotFoundException(name, e);
        }
        finally
        {
            close(inputStream);
        }
    }

    private byte[] decode(byte[] originBytes)
    {
        byte[] result = new byte[originBytes.length];
        for (int i = 0; i < originBytes.length; i++)
        {
            result[i] = (byte)(255 - originBytes[i]);
        }
        return result;
    }

    // 关闭
    private static void close(Closeable res) {
        if (null != res) {
            try {
                res.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
