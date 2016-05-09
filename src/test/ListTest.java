package test;

import java.util.ArrayList;
import java.util.List;

public class ListTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<TestModel> list = new ArrayList<TestModel>();
		for(int i = 0 ;i < 10; i++){
			list.add(new TestModel("str"+i, i*10));
		}
		System.out.println("before modify");
		for(TestModel mem : list){
			System.out.println(mem);
		}
		modifyList(list);
		System.out.println("after modify");
		for(TestModel mem : list){
			System.out.println(mem);
		}
	}
	
	/** 测试如何修改一list中的对象 */
	public static void modifyList(List<TestModel> list){
		TestModel a = list.get(0);
		a.setFieldInt(10000);
		a.setFieldStr("leideyu");
	}
	
	private static class TestModel{
		
		private String fieldStr;
		private int	   fieldInt;
		
		public TestModel(String str, int value){
			fieldStr = str;
			fieldInt = value;
		}
		public String getFieldStr() {
			return fieldStr;
		}
		public void setFieldStr(String fieldStr) {
			this.fieldStr = fieldStr;
		}
		public int getFieldInt() {
			return fieldInt;
		}
		public void setFieldInt(int fieldInt) {
			this.fieldInt = fieldInt;
		}
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "[string:"+fieldStr+"  int:"+fieldInt+"]";
		}
	}

}
