import sun.management.snmp.jvminstr.JvmThreadingImpl;

public class Tests {
    public static void main(String[] args) {
        int a=0;
        if(a==0)
            a++;
        else if (a==1) {
            a++;
        }
        System.out.println(a);
    }
}

