package hotLoadClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author seven
 *	类加载器
 */
public class MyClassLoads extends ClassLoader {
	
	//构造器私有化,禁止使用者直接生成实例
	private MyClassLoads() {}
	
	//获取MyClassLoads的唯一方法
	public static MyClassLoads GetInstance() {
		return new MyClassLoads();
	}

		
	/**
	 * @param classPaht
	 * @return Object
	 * 热加载新的Class类
	 */
	public Object FindNewClass(String classPaht) {
		try {
			byte[] b = getBytes(classPaht);
			return defineClass(null, b, 0, b.length).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	
	/**
	 * @param o
	 * @return T
	 * 包装返回类对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T ReLoadClass(Object o) {
		return (T) o;
	}

	
	/**
	 * @param filename
	 * @return Byte[]
	 * @throws IOException
	 *  返回class文件的Byte
	 */
	private byte[] getBytes(String filename) throws IOException {
		File file = new File(filename);
		byte raw[] = new byte[(int) file.length()];
		FileInputStream fin = new FileInputStream(file);
		fin.read(raw);
		fin.close();
		return raw;
	}
}
