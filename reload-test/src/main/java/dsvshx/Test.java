package dsvshx;

/**
 * @author dongzhonghua
 * Created on 2020-10-22
 */
public class Test {
    public Test() {
    }

    public void hello() {
        System.out.println("test 3.0");
        System.out.println("当前使用的类加载器是：" + getClass().getClassLoader());
    }

    public void say() {
        System.out.println("say hello 1.0");
        System.out.println("当前使用的类加载器是：" + getClass().getClassLoader());
    }

    public static void main(String[] args) {
        System.out.println(String.class.getClassLoader());
    }
}
