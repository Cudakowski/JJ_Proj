package com.jjproj;

public class TestLogin {
    public static void test(){
        NetworkManager.login("Emilia", "4321");
        
        try {
            Thread.sleep(3000); 
            //Thread.activeCount();
            System.out.println("Wcisnij ENTER aby zakonczyc program");
            System.in.read();
        } catch (Exception e) {
            System.out.println("cos nie tak w tescie");
        }

        
        System.out.println("zamykanie");
        System.exit(0);
    }
}
