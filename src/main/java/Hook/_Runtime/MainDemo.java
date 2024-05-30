package Hook._Runtime;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import java.util.List;

public class MainDemo {
    public static void main(String[] args) throws Exception{
        String path = "./src/main/java/Hook/_Runtime/jars/agentmain.jar";
        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        for (VirtualMachineDescriptor v:list){
            if (v.displayName().contains("MainDemo")){
                System.out.println("已找到目标类，将 jvm 虚拟机的 pid 号传入 attach 来进行远程连接，并将 agent.jar 发送给虚拟机");
                VirtualMachine vm = VirtualMachine.attach(v.id());
                vm.loadAgent(path);

                // 此时 agent 已被使用，尝试调用 Runtime
                Runtime.getRuntime().exec("joizjcxjxozcjizxc");
                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.command("calc");
                processBuilder.start();
                vm.detach();
            }
        }
    }
}