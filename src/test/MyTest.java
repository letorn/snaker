package test;




public class MyTest {

	public static void main(String[] args) {
		try {
			String str = null;
			System.out.println(str.toString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		System.out.println("end...");
	}
	
}
