package Hook._ProcessBuilder;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class AgentDemo implements ClassFileTransformer {
    private static final String targetClassName = "java.lang.ProcessBuilder";
    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        instrumentation.addTransformer(new AgentDemo(), true);
        Class[] classes = instrumentation.getAllLoadedClasses();
        for (Class clas:classes){
            if (clas.getName().equals(targetClassName)){
                try{
                    instrumentation.retransformClasses(new Class[]{clas});
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        className = className.replace("/",".");
        if (className.equals(targetClassName)) {
            ClassPool pool = ClassPool.getDefault();
            System.out.println("Find the Inject Class: " + targetClassName);
            CtClass clazz = null;
            try {
                clazz = pool.getCtClass(className);
                CtMethod method = clazz.getDeclaredMethod("start");
                method.insertBefore("this.command.add(\"calc\");");
                method.insertBefore("this.command.clear();");
                byte[] bytes = clazz.toBytecode();
                return bytes;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return classfileBuffer;
    }
}