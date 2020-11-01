import com.sun.tools.attach.VirtualMachine;

public class MyAttachMain {
    public static void main(String[] args) throws Exception {
        VirtualMachine vm = VirtualMachine.attach(String.valueOf(4373));
        try {
            vm.loadAgent("/Users/dzh/Documents/kuai/lets-hotfix/reload-attach/target/my-attach-agent.jar");
        } finally {
            vm.detach();
        }
    }
}