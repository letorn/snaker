package test;

import java.util.HashMap;
import java.util.Map;



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
		Thread thread2 = new Thread(new Runnable(){
			public void run() {
				try {
					System.out.println("thread2 sleep 3000");
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("thread2 do something");
				
			}
		});
		thread.run();
		thread2.run();
		
		System.out.println("main do something");
	}
	
}
