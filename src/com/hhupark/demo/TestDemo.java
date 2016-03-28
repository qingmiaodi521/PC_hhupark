package com.hhupark.demo;

import java.util.Date;

import com.hhupark.bmob.bson.BSON;
import com.hhupark.bmob.bson.BSONObject;
import com.hhupark.bmob.restapi.Bmob;

public class TestDemo {
    
	public static void main(String[] args) {
		//BSONObject ��ʹ��
		CreateClassBSONObject();
		
		initBmob();//��ʼ��
//		Search();//��ѯ
//		update();//�޸�
//		delete();//ɾ��
		insert();//����
//		callFunction();//�����ƴ���
//		findPayOrder();//��ѯ֧������
//		count();//����
//		upload();//�ļ��ϴ�
	}
	
	//ʹ��RestAPIǰ�����ȳ�ʼ����KEY����BmobӦ����Ϣ���ѯ��
	private static void initBmob(){	
		Bmob.initBmob("e8dc389c8da455c74d657ede10ad3de5",
				"c388a53fd081ae4e253871f7d6aaaada");
		//�õ�����Ȩ����Ҫע���Key
//		Bmob.initMaster("Your Bmob-Master-Key");
	}
	private static void Search(){
		//where�����ܶ࣬�ɲο�����RestAPI�ĵ�
		BSONObject where1 = new BSONObject(Bmob.whereLess(10));
		BSONObject where = new BSONObject();
		where.put("score", where1);
		//find�����ܶ࣬�����г���
		String result = Bmob.find("Your TableName", where.toString(), 0, 50, "order");
		Bmob.findBQL("BQL");
		Bmob.findBQL("BQL","value");
		//��ʹ��JSON ���� BSON ת����Object
//		BSONObject bson = new BSONObject(result);
	}
	
	private static void update(){
		BSONObject bson = new BSONObject();
		bson.put("score", 100);
		//score �޸�Ϊ100
		Bmob.update("Your TableName", "Your objectId", bson.toString());
	}
	
	private static void delete(){
		Bmob.delete("Your TableName", "Your objectId");
	}
	
	private static void insert(){
		BSONObject bson = new BSONObject();
		bson.put("student", "zhangsan");
		Bmob.insert("student", bson.toString());
	}
	
	private static void callFunction(){
		BSONObject bson = new BSONObject();
		bson.put("param1", "a");
		bson.put("param2", 0);
		bson.put("param3", true);
		
		Bmob.callFunction("Your functionName", bson.toString());
	}
	
	private static void findPayOrder(){
		Bmob.findPayOrder("Your PayId");
	}
	
	private static void count(){
		BSONObject where = new BSONObject();
		where.put("score", 100);
		Bmob.count("Your TableName", where.toString());
	}
	
	private static void upload(){
		String res=Bmob.uploadFile("/tmp/myPicture.jpg");
		System.out.println(res);
	}
	
	private static void CreateClassBSONObject(){
		BSONObject class1, teacher, students;
		BSONObject zhangsan, lisi, wangwu;
		
		class1 = new BSONObject();
		class1.put("name", "Class 1");
		class1.put("build", new Date());
		
		teacher = new BSONObject();
		teacher.put("name", "Miss Wang");
		teacher.put("sex", "female");
		teacher.put("age", 30);
		teacher.put("offer", true);
		
		students = new BSONObject();
		students.put("number", 45).put("boy", 23).put("girl", 22);
		
		zhangsan = new BSONObject().put("name", "ZhangSan");
		zhangsan.put("age", 12).put("sex", "male");

		lisi = new BSONObject();
		lisi.put("name", "LiSi");
		lisi.put("age", 12);
		lisi.put("sex", "female");
		
		wangwu = new BSONObject();
		wangwu.put("name", "WangWu");
		wangwu.put("age", 13);
		wangwu.put("sex", "male");
		
		students.put("student", new BSONObject[]{zhangsan, lisi, wangwu});
		class1.put("teacher", teacher);
		class1.put("students", students);
		
		BSONObject bson_class = new BSONObject(class1.toString());
		BSON.Log("BSON:"+bson_class+"\n");
		BSON.Log("Build date:"+bson_class.getDate("build"));
		BSON.Log("Is teacher offer? "+bson_class.getBSONObject("teacher").getBoolean("offer"));
		BSON.Log("Teacher's age:"+bson_class.getBSONObject("teacher").getInt("age"));
		BSON.Log("First student's name:"+bson_class.getBSONObject("students").getBSONArray("student")[0].getString("name"));
		
		
	}
	
}
