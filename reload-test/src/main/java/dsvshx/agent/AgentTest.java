package dsvshx.agent;

/**
 * @author dongzhonghua
 * Created on 2020-10-28
 */

import java.lang.instrument.Instrumentation;

public class AgentTest {
    public static void premain(String agentOps, Instrumentation inst) {

        System.out.println("=========premain方法执行========");
        // 添加Transformer
        inst.addTransformer(new MyTransformer());
        System.out.println(agentOps);
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=======main方法执行=========");
    }

}
