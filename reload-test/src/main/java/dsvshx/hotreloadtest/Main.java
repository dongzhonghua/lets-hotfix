package dsvshx.hotreloadtest;

/**
 * @author dongzhonghua
 * Created on 2020-11-02
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        while (true) {
            System.out.println(new A().a());
            System.out.println(new B().b());
            System.out.println(B.bb());
            System.out.println("2");
            System.out.println("asdfasdfasdf");
            System.out.println("----------------" + System.currentTimeMillis());
            Thread.sleep(3000);
        }
    }
}
