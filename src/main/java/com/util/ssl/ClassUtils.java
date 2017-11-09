/**
 * ReflectUtils.java
 *
 * Copyright (c) by yinty
 *
 */
package com.util.ssl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

/**
 *  JDK反射工具类
 *
 * @date 2008-6-20
 * @author yinty
 *
 * @版本更新列表
 * <pre>
 * 修改版本: 1.1.0
 * 修改日期：2010-5-4
 * 修改人 : yinty
 * 修改说明：
 *      1. 将本类名由 ReflectUtils, 改为 ClassUtils
 *      2. 增加常量定义, ARRAY_SUFFIX, INTERNAL_ARRAY_PREFIX, PACKAGE_SEPARATOR, INNER_CLASS_SEPARATOR, CGLIB_CLASS_SEPARATOR
 *      3. 增加方法
 * 
 * 修改版本: 1.0.0
 * 修改日期：2008-6-20
 * 修改人 : yinty
 * 修改说明：形成初始版本
 *</pre>
 */
public abstract class ClassUtils {
    private static final Logger logger = LoggerFactory.getLogger( ClassUtils.class );
    
    /** 数组类名的后缀: [] */
    public static final String ARRAY_SUFFIX = "[]";
    /** 多维数组类名的前缀: [L */
    public static final String INTERNAL_ARRAY_PREFIX = "[L";
    /** 包名分隔符:  . */
    public static final char PACKAGE_SEPARATOR = '.';
    /** 内部类分隔符:  $ */
    public static final char INNER_CLASS_SEPARATOR = '$';
    /** CGLIB 代理类的分隔符 : $$ */
    public static final String CGLIB_CLASS_SEPARATOR = "$$";
    
    private static final Map<String, Class> classCache = new WeakHashMap<String, Class>();

    private static final ClassLoader defaultLoader = ClassUtils.class.getClassLoader();

    private static final String[] defaultPackages = {
        "java.lang", "java.util"
    };
    
	private static final Map<String, Class> primitives = new ImmutableMap.Builder<String, Class>()
	        .put("byte",      byte.class)
            .put("char",      char.class)
            .put("double",    double.class)
            .put("float",     float.class)
            .put("int",       int.class)
            .put("long",      long.class)
            .put("short",     short.class)
            .put("boolean",   boolean.class)
	        .build();
	
	private static final Map<String, String> transforms = new ImmutableMap.Builder<String, String>()
	        .put("byte",      "B")
            .put("char",      "C")
            .put("double",    "D")
            .put("float",     "F")
            .put("int",       "I")
            .put("long",      "J")
            .put("short",     "S")
            .put("boolean",   "Z")
	        .build();
	

	/**
	 * 搜索指定类名的类，将其转化为Class对象返回
	 * @param className 指定的类名
	 * @return Class对象
	 */	
	public static Class<?> findClass(String className) {		
		Class cached = classCache.get(className);
		if(cached != null){
			return cached;
		}
		cached = findClass(className, defaultPackages, defaultLoader);
		classCache.put(className, cached);		
		return cached;
	}
	
	/**
	 * 在指定的包中，搜索指定类名的类，将其转化为Class对象返回
	 * @param className
	 * @param packageName
	 * @return
	 */
	public static Class<?> findClass(String className, String packageName) {
		String key = packageName + PACKAGE_SEPARATOR + className;
		Class cached = classCache.get(key);
		if(cached != null){
			return cached;
		}
		cached = findClass(className, new String[]{packageName}, defaultLoader);
		classCache.put(key, cached);		
		return cached;
	}
	
	/**
	 * 在指定的包中，搜索指定类名的类，将其转化为Class对象返回
	 * @param className 指定的类名
	 * @return Class对象
	 */	
	public static Class<?> findClass(String className, String[] packages) {
		if(packages == null){
			return findClass(className, defaultPackages, defaultLoader);
		}		
		return findClass(className, packages, defaultLoader);
	}

	/**
	 * 使用指定的ClassLoader、在指定的包下，搜索指定类名的类，将其转化为Class对象
	 * @param className 指定的类名
	 * @param packages 指定的包
	 * @param loader 指定的ClassLoader
	 * @return Class对象
	 */	
	public static Class<?> findClass(String className, String[] packages, ClassLoader loader){		
		String save = className;
		int dimensions = 0;
        int index = 0;
        while ((index = className.indexOf(ARRAY_SUFFIX, index) + 1) > 0) {
            dimensions++;
        }
        StringBuilder brackets = new StringBuilder(className.length() - dimensions);
        for (int i = 0; i < dimensions; i++) {
            brackets.append('[');
        }
        className = className.substring(0, className.length() - 2 * dimensions);

        String prefix = (dimensions > 0) ? brackets + "L" : "";
        String suffix = (dimensions > 0) ? ";" : "";

        try {
            return Class.forName(prefix + className + suffix, false, loader);
        } catch (ClassNotFoundException ignore) { 
            //Noon
        }
        for (int i = 0; i < packages.length; i++) {
            try {
                return Class.forName(prefix + packages[i] + PACKAGE_SEPARATOR + className + suffix, false, loader);
            } catch (ClassNotFoundException ignore) {
                //Noon
            }
        }
        if (dimensions == 0) {
            Class c = primitives.get(className);
            if (c != null) {
                return c;
            }
        } else {
            String transform = transforms.get(className);
            if (transform != null) {
                try {
                    return Class.forName(brackets + transform, false, loader);
                } catch (ClassNotFoundException ignore) {
                    //Noon
                }
            }
        }
        throw new RuntimeException( "className " + save + "  not found !" );
	}

	/**
	 * 将指定的一组类名转化为Class数组
	 * @param classNames 指定的一组类名
	 * @return Class数组
	 */
	public static Class[] findClass(String[] classNames){
		Class[] classArray = new Class[classNames.length];
		for(int i=0, len=classNames.length; i<len; i++){
			classArray[i] = findClass(classNames[i]);
		}
		return classArray;
	}
	
	/**
	 * 获得数组的类型
	 * 如：由 String[] 得到 String, int[] 得到int
	 * @param arrayClass
	 * @return
	 */
    public static Class findArrayElementType(Class arrayClass) {
        if ( !arrayClass.isArray() ) {
            return arrayClass;
        }
        String className = arrayClass.getName();
        int beginIndex = className.lastIndexOf( '[' );
        int endIndex = className.indexOf( ';' );
        if ( beginIndex == -1 ) {
            return arrayClass;
        }
        int bracketsIndex = className.indexOf( '[' );

        //检查是否还有 '[', 如果有，说明是多维数组
        if ( bracketsIndex != beginIndex ) {
            className = className.substring( bracketsIndex + 1 ); //去掉[
        } else {
            if ( endIndex == -1 && className.indexOf( INTERNAL_ARRAY_PREFIX ) == -1 ) { //primitive, 形如 [I
                String transform = className.substring( bracketsIndex + 1 );
                Iterator<Map.Entry<String, String>> iter = transforms.entrySet().iterator();
                while ( iter.hasNext() ) {
                    Map.Entry<String, String> entry = iter.next();
                    if ( entry.getValue().equals( transform ) ) {
                        className = entry.getKey();
                        break;
                    }
                }
            } else { //对象类型, 形如 [Ljava.lang.String;
                className = className.substring( bracketsIndex + 2, endIndex );
            }
        }
        return findClass( className );
    }

    public static Class getTargetClass(Object object) {
        if ( object == null ) {
            return null;
        }
        Class clazz = object.getClass();
        if ( isCglibProxy( object ) ) {
            clazz = clazz.getSuperclass();
        } else if ( isJdkDynamicProxy( object ) ) {
            clazz = getTargetClassFromJdkDynamicAopProxy( object );
        }
        return clazz;
    }
    
    private static final String ADVISED_FIELD_NAME = "advised";  
    private static final String CLASS_JDK_DYNAMIC_AOP_PROXY = "org.springframework.aop.framework.JdkDynamicAopProxy";  
    private static Class getTargetClassFromJdkDynamicAopProxy(Object object) {
        try {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler( object );
            if ( !invocationHandler.getClass().getName().equals( CLASS_JDK_DYNAMIC_AOP_PROXY ) ) {
                return object.getClass();
            }
            org.springframework.aop.framework.AdvisedSupport advised = (org.springframework.aop.framework.AdvisedSupport) 
                    new org.springframework.beans.DirectFieldAccessor( invocationHandler ).getPropertyValue( ADVISED_FIELD_NAME );
            Class targetClass = advised.getTargetClass();
            if ( Proxy.isProxyClass( targetClass ) ) {  // 目标类还是代理，递归  
                Object target = advised.getTargetSource().getTarget();
                return getTargetClassFromJdkDynamicAopProxy( target );
            }
            return targetClass;
        } catch ( Exception e ) {
            logger.error( "get target class from " + CLASS_JDK_DYNAMIC_AOP_PROXY + " error", e );
            return object.getClass();
        }
    }

    public static boolean isJdkDynamicProxy(Object object) {
        Class clazz = object.getClass();
        return Proxy.isProxyClass( clazz );
    }

    public static boolean isCglibProxy(Object object) {
        Class clazz = object.getClass();
        return clazz.getName().indexOf( CGLIB_CLASS_SEPARATOR ) > 0;
    }
	
	
	/**
	 * 查找指定类的所有父类
	 * @param clazz 指定类
	 * @return
	 */
	public static List<Class> findAllSuperclasses(Class clazz){
		List<Class> list = new ArrayList<Class>();
		for(Class superclass = clazz.getSuperclass(); superclass != null; superclass = clazz.getSuperclass()){
			list.add(superclass);
		}			
		return list;
	}
	
	/**
	 * 查找指定类的所有接口
	 * @param clazz 指定类
	 * @return
	 */
	public static List<Class> findAllInterfaces(Class clazz){
		List<Class> list = new ArrayList<Class>();
		for(Class superclass = clazz; superclass != null; superclass = clazz.getSuperclass()){
			list.addAll(Arrays.asList(superclass.getInterfaces()));
		}
		return list;
	}

	/**
	 * 查找指定类实现的指定名称的接口
	 * @param clazz 指定类
	 * @param interfaceName 接口名称
	 * @return
	 */
	public static Class findImplementedInterface(Class clazz, String interfaceName){
		Class[] implementedInterfaces = clazz.getInterfaces();
		for (int i = 0, len = implementedInterfaces.length; i < len; i++) {
			Class iface = implementedInterfaces[i];
			if (iface.getName().equals(interfaceName)) {
				return iface;
			}
		}
		return null;
	}

	/**
	 * 查找指定类实现的指定名称的接口
	 * @param targetClass 指定类
	 * @param interfaceClass 接口
	 * @return
	 */
	public static Class findImplementedInterface(Class targetClass, Class interfaceClass){
		Class[] implementedInterfaces = targetClass.getInterfaces();
		for (int i = 0, len = implementedInterfaces.length; i < len; i++) {
			Class iface = implementedInterfaces[i];
			if(iface == interfaceClass){
				return iface;
			}
		}
		return null;
	}

	/**
	 * 在指定的类上，查找所有的构造方法
	 * @param targetClass 指定的类
	 * @return 所有的构造方法
	 */
	public static Constructor[] findConstructors(Class targetClass){
		return targetClass.getConstructors();
	}

	/**
	 * 查找指定类上的包括父类除Object类外的所有方法
	 * @param targetClass 指定的类
	 * @return 指定类上的包括父类的所有方法
	 */
	public static Method[] findAllMethod(Class targetClass){
		return targetClass.getMethods();
	}

	/**
	 * 在指定的类上，根据方法名称和参数类型，查找方法
	 * @param targetClass 指定的类
	 * @param methodName 方法名称
	 * @param parameterTypes 参数类型
	 * @return Method 方法对象
	 */
	public static Method findMethod(Class targetClass, String methodName, Class... parameterTypes){
        try {
            return targetClass.getDeclaredMethod( methodName, parameterTypes );
        } catch ( Exception e1 ) {
            try {
                return targetClass.getMethod( methodName, parameterTypes );
            } catch ( Exception e2 ) {}
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append( "can not find method[" ).append( methodName ).append( "(" );
        for ( int i = 0, len = parameterTypes.length; i < len; i++ ) {
            if ( i != 0 ) {
                sb.append( ", " );
            }
            sb.append( parameterTypes[i].getName() );
        }
        sb.append( ")] for class[" ).append( targetClass.getName() ).append( "]" );
        throw new RuntimeException( sb.toString() );
	}

	/**
	 *  由指定的包括类名、方法名、参数列表的字符串，查找方法.<br/>
	 *  <code>fullMethodNameWithParams</code>形如：java.lang.String.substring(int, int)
	 * @param fullMethodNameWithParams 包括类名、方法名、参数列表的字符串
	 * @return 方法对象
	 */
	public static Method findMethod(String fullMethodNameWithParams){
		int lparen = fullMethodNameWithParams.indexOf('(');
		int dot = fullMethodNameWithParams.lastIndexOf('.', lparen);
		int rparen = fullMethodNameWithParams.indexOf(')', lparen);
		String className = fullMethodNameWithParams.substring(0, dot).trim();
		String methodName = fullMethodNameWithParams.substring(dot +1, lparen).trim();

		Class targetClass = findClass(className);
		List<String> paramList = new ArrayList<String>();
		int start = lparen + 1;
        for (;;) {
            int comma = fullMethodNameWithParams.indexOf(',', start);
            if (comma < 0) {
                break;
            }
            paramList.add(fullMethodNameWithParams.substring(start, comma).trim());
            start = comma + 1;
        }
        if (start < rparen) {
        	paramList.add(fullMethodNameWithParams.substring(start, rparen).trim());
        }
        String[] paramArray = paramList.toArray(new String[paramList.size()]);
        Class[] paramTypes = findClass(paramArray);
        return findMethod(targetClass, methodName, paramTypes);
	}
	
	/**
	 * 查询指定类的指定构造函数
	 * @param type
	 * @param parameterTypes
	 * @return
	 */
    public static Constructor getConstructor(Class clazz, Class[] parameterTypes) {
        if ( clazz == null ) {
            throw new IllegalArgumentException( "clazz can not be null" );
        }
        try {
            Constructor constructor = clazz.getDeclaredConstructor( parameterTypes );
            return constructor;
        } catch ( Exception e ) {
            StringBuilder buffer = new StringBuilder();
            buffer.append( "constuctor with parameters: [" );
            for ( int index = 0, len = parameterTypes.length; index < len; index++ ) {
                Class type = parameterTypes[index];
                if ( index != 0 ) {
                    buffer.append( ", " );
                }
                buffer.append( type.getName() );
            }
            buffer.append( "] not found for class: " ).append( clazz.getName() );
            throw new RuntimeException( buffer.toString(), e );
        }
    }

	/**
	 * 调用方法
	 * @param targetRef 对象,如果置null则调用静态方法
	 * @param methodName 方法名, 调用静态方法时应包括类名、方法名、参数列表, 非static方法不加类名
	 * @param args 参数
	 * @return
	 */
	public static Object invokeMethod(Object targetRef, String methodName, Object...args){
		Method method = findMethod(methodName);
		try{
			return method.invoke(targetRef, args);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	/**
	 * 取得方法的参数类型
	 * @param method 指定的方法
	 * @return 参数类型列表
	 */
	public static String[] getParameterTypes(Method method) {
		Class[] parameterClasses = method.getParameterTypes();
		String[] parameterTypes = new String[parameterClasses.length];
		for(int i=0,len=parameterClasses.length; i<len; i++){
			parameterTypes[i] = parameterClasses[i].getName();
		}
		return parameterTypes;
	}

	
	
	/**
	 * 实例化一个指定类的实例
	 * @param clazz 
	 * @return
	 */
	public static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch ( Exception ignore ) {}
        return newInstance( clazz, new Class[0], null );
    }
	
	/**
	 * 实例化一个指定类的实例
	 * @param clazz
	 * @param parameterTypes
	 * @param args
	 * @return
	 */	
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<T> clazz, Class[] parameterTypes, Object[] args) {
	    return (T)newInstance(getConstructor(clazz, parameterTypes), args);
	}
	
	/**
	 * 由构造函数实例化
	 * @param cstruct
	 * @param args
	 * @return
	 */
	public static Object newInstance(final Constructor cstruct, final Object[] args) {
		boolean flag = cstruct.isAccessible();
		cstruct.setAccessible(true);		
		try {
			Object result = cstruct.newInstance(args);
			return result;
		} catch (Exception e) {
			throw new RuntimeException("new Instancece for error!", e);
		}finally{
			cstruct.setAccessible(flag);
		}
	}

}
