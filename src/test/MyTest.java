package test;




public class MyTest {

	public static void main(String[] args) {
		Thread thread = new Thread(new Runnable(){
			public void run() {
				try {
					String str = null;
					System.out.println(str.toString());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				
			}
		});
		try {
			thread.run();
		} catch (Exception e) {
			System.out.println("catch...");
			// e.printStackTrace();
		}
		System.out.println("main do something");
	}
	
}
