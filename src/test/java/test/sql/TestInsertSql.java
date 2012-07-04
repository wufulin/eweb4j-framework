package test.sql;

import java.util.HashMap;
import java.util.Map;

import org.eweb4j.config.EWeb4JConfig;
import org.eweb4j.orm.sql.InsertSqlCreator;
import org.eweb4j.orm.sql.SqlFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import test.po.Master;
import test.po.Pet;


public class TestInsertSql {
	private static Pet pet;
	private static InsertSqlCreator<Pet> insert;

	@BeforeClass
	public static void prepare() throws Exception {
		String err = EWeb4JConfig.start("start.eweb.xml");
		if (err != null){
			System.out.println(">>>EWeb4J Start Error --> " + err);
			System.exit(-1);
		}
		pet = new Pet();
		insert = SqlFactory.getInsertSql(pet);
	}
	
	@Test
	public void testMap() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("table", "t_pet");
		map.put("idColumn", "id");
		map.put("columns", new String[]{"num","name","age","cate"});
		map.put("values", new Object[]{123,"weiwei", 3, "dog"});
		Pet pet = new Pet();
		pet.setName("xxx");
		pet.setNumber("4444");
		pet.setAge(3);
		pet.setType("cat");
		final InsertSqlCreator<?> insert = SqlFactory.getInsertSql(map, pet);
		String[] sql = insert.create();
		Assert.assertEquals("INSERT INTO t_pet(num,name,age,cate) VALUES('123','weiwei','3','dog') ;", sql[0]);
		Assert.assertEquals("INSERT INTO t_pet(num,name,age,cate) VALUES('4444','xxx','3','cat') ;", sql[1]);
	}

	/**
	 * 将若干个POJO的所有属性值插入数据库 例如： <code>
	 *  class Pet{
	 *  	private Integer id;
	 *  	private String name;
	 *  	private int age;
	 *      //此处省略setter和getter方法
	 *  }
	 * 	Pet pet = new Pet();
	 *  pet.setName("小黑");
	 *  pet.setAge(3);
	 *  insert(pet);
	 * </code> 会执行sql:INSERT INTO $table values('小黑','3');
	 * 
	 * POJO的类型
	 * 
	 * @param ts
	 *            带有数据的POJO,可多个不同类型或同类型
	 * @return 如果插入成功,返回true,否则返回false.
	 */
	@Test
	public <T> void testInsert() {

		pet.setName("小黑");
		pet.setAge(3);
		pet.setType("dog");

		String sql = insert.create()[0];
		Assert.assertEquals(
				"INSERT INTO t_pet(name,age,cate) VALUES('小黑','3','dog') ;",
				sql);

	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInsertWithOneRel() throws Exception {
		Master master = new Master();
		master.setId(5L);
		pet.setName("xiaohei");
		pet.setAge(2);
		pet.setMaster(master);
		String[] fields = { "name", "age", "master" };
		String sql = insert.createByFields(fields)[0];
		Assert.assertEquals(
				"INSERT INTO t_pet(name,age,master_id) VALUES('xiaohei','2','5') ;",
				sql);
	}

	/**
	 * 带Where条件子句,将若干个POJO所有属性值插入数据库 <code>
	 * 	class Pet{
	 * 		private Integer id;
	 * 		private String name;
	 * 		private int age;
	 * 		//此处省略setter和getter方法
	 * 	}
	 *  Pet pet = new Pet();
	 *  pet.setName("小白");
	 *  pet.setAge(4);
	 *  insertByCondition("xxx", pet);
	 * </code> 会执行sql:INSERT INTO $table values('小黑','3') WHERE xxx ;
	 * 
	 * POJO的类型
	 * 
	 * @param condition
	 *            Where条件
	 * @param ts
	 *            带有数据的POJO,可多个不同类型或同类型
	 * @return 如果插入成功,返回true,否则返回false
	 */
	@Test
	public void testInsertByCondition() {
		pet.setName("小黑");
		pet.setAge(3);
		pet.setType("dog");
		pet.setMaster(null);
		String sql = insert.create("xxx = 'ooo'")[0];
		Assert.assertEquals(
				"INSERT INTO t_pet(name,age,cate) VALUES('小黑','3','dog')  WHERE xxx = 'ooo' ;",
				sql);
	}

	/**
	 * 给定POJO属性值,插入数据库 <code>
	 * 	class Pet{
	 * 		private Integer id;
	 * 		private String name;
	 * 		private int age;
	 * 		//此处省略setter和getter方法
	 * 	}
	 *  class Master{
	 *      private Integer id;
	 *      private String name;
	 *      private String gender;
	 *  }
	 *  
	 * 	Pet pet = new Pet();
	 *  pet.setName("小黄");
	 *  
	 *  Master master = new Master();
	 *  master.setGender("女");
	 *  
	 *  Object[] objs = new Object[]{master, pet};
	 *  String[] masterFields = new String[]{"gender"};
	 *  String[] petFields = new String[]{"name"};
	 *  String[][] fields = new String[][]{masterFields, petFields};
	 *  insertByFields(objs,masterFields, petFields);
	 * </code>会执行sql:INSERT INTO $masterTable(gender) values('女');INSERT INTO
	 * $petTable(name) values('小黄') ;
	 * 
	 * POJO的类型
	 * 
	 * @param ts
	 *            带有数据的POJO,多个不同类型或同类型
	 * @param fields
	 *            按数组下标对应POJO的属性名
	 * @return 如果插入成功,返回true,否则返回false
	 */
	@Test
	public void testInsertByFields() {
		pet.setName("小黑");
		String sql = insert.createByFields(new String[] { "name" })[0];
		Assert.assertEquals("INSERT INTO t_pet(name) VALUES('小黑') ;", sql);

	}
}
