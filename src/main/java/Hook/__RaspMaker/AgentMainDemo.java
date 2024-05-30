package Hook.__RaspMaker;

import javassist.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Arrays;

public class AgentMainDemo implements ClassFileTransformer {
    private static final String[] targetClassNameArray = {"java.lang.ProcessImpl", "java.lang.UNIXProcess"};

    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        AgentMainDemo transforme = new AgentMainDemo();
        String nativePrefix = "ko1sh1_";
        instrumentation.addTransformer(transforme, true);
        if (instrumentation.isNativeMethodPrefixSupported()){
            instrumentation.setNativeMethodPrefix(transforme, nativePrefix);
        }else {
            System.out.println("The JVM does not support setting the native method prefix");
        }

        Class[] classes = instrumentation.getAllLoadedClasses();
        for (Class clas : classes) {
            if (Arrays.asList(targetClassNameArray).contains(clas.getName())) {
                try {
                    instrumentation.retransformClasses(clas);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        className = className.replace("/", ".");
        if (Arrays.asList(targetClassNameArray).contains(className)) {
            ClassPool pool = ClassPool.getDefault();
            CtClass clazz = null;
            try {
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    clazz = pool.getCtClass(className);
                    // 添加新的前缀方法
                    CtMethod method = CtNewMethod.make("long ko1sh1_create(String var1, String var2, String var3, long[] var4, boolean var5);", clazz);
                    method.setModifiers(Modifier.PRIVATE | Modifier.STATIC | Modifier.SYNCHRONIZED | Modifier.NATIVE);
                    clazz.addMethod(method);
                    // 移除旧的方法
                    CtMethod method1 = clazz.getDeclaredMethod("create");
                    clazz.removeMethod(method1);

                    // 添加原本的方法，但是不使用 native 修饰
                    CtMethod method2 = CtNewMethod.make("long create(String var1, String var2, String var3, long[] var4, boolean var5) throws java.io.IOException { " +
                            "String a = ($w)$0; " +
                            "String b = ($w)$1; " +
                            "String c = ($w)$2; " +
                            "System.out.println(\"检测到命令执行操作，内容为：\"+a);"+
                            "return ko1sh1_create(a,b,c,new long[]{-1,-1,-1},false); " +
                            "}", clazz);
                    method2.setModifiers(Modifier.PRIVATE | Modifier.STATIC | Modifier.SYNCHRONIZED);
                    clazz.addMethod(method2);

                    byte[] bytes = clazz.toBytecode();
                    return bytes;
                } else {
                    if ("java.lang.UNIXProcess".equals(className)) {
                        clazz = pool.getCtClass(className);
                        CtMethod method = CtNewMethod.make("int ko1sh1_forkAndExec(int var1, byte[] var2, byte[] var3, byte[] var4, int var5, byte[] var6, int var7, byte[] var8, int[] var9, boolean var10);", clazz);
                        method.setModifiers(Modifier.PRIVATE | Modifier.NATIVE);
                        clazz.addMethod(method);

                        CtMethod method1 = clazz.getDeclaredMethod("forkAndExec");
                        clazz.removeMethod(method1);

                        CtMethod method2 = CtNewMethod.make("int forkAndExec(int var1, byte[] var2, byte[] var3, byte[] var4, int var5, byte[] var6, int var7, byte[] var8, int[] var9, boolean var10) throws java.io.IOException { System.out.println(\"检测到系统命令执行，内容为: \" + new java.lang.String(var3)); return this.ko1sh1_forkAndExec(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10); }", clazz);
                        method2.setModifiers(Modifier.PRIVATE);
                        clazz.addMethod(method2);

                        byte[] bytes = clazz.toBytecode();
                        return bytes;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return classfileBuffer;
    }
}