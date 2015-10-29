package test;



public class MyTest {

	public static void main(String[] args) {
		Thread thread = new Thread(new Runnable(){
			public void run() {
				try {
					System.out.println("thread sleep 3000");
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("thread do something");
				
			}
		});
		thread.setDaemon(false);
		thread.start();
		
		System.out.println("main do something");
	}
	
}
