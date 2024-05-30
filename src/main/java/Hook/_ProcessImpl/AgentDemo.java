package Hook._ProcessImpl;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Arrays;

public class AgentDemo implements ClassFileTransformer {
    private static final String[] targetClassNameArray = {"java.lang.ProcessImpl" , "java.lang.UNIXProcess"};

    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        instrumentation.addTransformer(new AgentDemo(), true);
        Class[] classes = instrumentation.getAllLoadedClasses();
        for (Class clas:classes){
            if (Arrays.asList(targetClassNameArray).contains(clas.getName())){
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
        if (Arrays.asList(targetClassNameArray).contains(className)) {
            ClassPool pool = ClassPool.getDefault();
            CtClass clazz = null;
            try {
                clazz = pool.getCtClass(className);
                CtConstructor[] method = clazz.getDeclaredConstructors();
                method[0].insertBefore("{System.out.println(\""+ className +" 类被初始化\");}");
                return clazz.toBytecode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return classfileBuffer;
    }
}