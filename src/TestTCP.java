import java.io.IOException;
import java.util.Scanner;

public class TestTCP {

    private static TCPConnection connection=null;
    public static void Test(){

        
        Scanner scanner = new Scanner(System.in);
        String ans="";

        try {
            while (connection==null) {
                System.out.print("Podaj czy hustujesz(Y/N):");
                ans = scanner.nextLine();
                System.out.println(ans);
                if(ans.equals("Y")){
                    connection = new TCPServer();
                }else if(ans.equals("N")){
                    connection = new TCPClient();
                }else{
                    System.out.println("zly input");
                }
            }
            
        } catch (IOException e) {
            // TODO: handle exception
        }

        
        
        while(!ans.equals("end")){
            System.out.print("Podaj czy wysylasz(Y/N/end)(N=odbierasz):");
            ans = scanner.nextLine();
            System.out.println(ans);
            if(ans.equals("Y")){
                connection.sendString("kek");
            }else if(ans.equals("N")){
                System.out.print(connection.awaitString());
            }else if(!ans.equals("end")){
                System.out.println("zly input");
            }
        }

        scanner.close();
    }

}
