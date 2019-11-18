package bd.ibatis;

import java.io.IOException;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class MyBatisUtil
{
	private static final String CONFIG_FILE = "conf/mybatis-config.xml";
	
	private static SqlSessionFactory factory = null;

	private MyBatisUtil() {

	}

	public static SqlSessionFactory getSqlSessionFactory()
	{
		if(factory == null)
		{
			try {
				factory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader(CONFIG_FILE));
			}
			catch (IOException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
		return factory;
	}
}