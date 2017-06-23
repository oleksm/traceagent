package tech.oleks.traceagent;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class Transformer implements ClassFileTransformer {
	public byte[] transform(ClassLoader loader, String className,
			Class classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		byte[] byteCode = classfileBuffer;

		// since this transformer will be called when all the classes are
		// loaded by the classloader, we are restricting the instrumentation
		// using if block only for the Lion class
		def conf = Config.prop
		if (conf.debug) {
			println "TRACEAGENT CLASS [${className}]"
		}
		conf.classes.each { c -> 
			if (className.matches(c.crex)) {
				println "TRACEAGENT Instrumenting [${className}]...... "
				try {
					ClassPool classPool = ClassPool.getDefault();
					CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(
							classfileBuffer));
					CtMethod[] methods = ctClass.getDeclaredMethods();
					for (CtMethod method : methods) {
						if (conf.debug)
							println "TRACEAGENT method ${className}.${method.name}"
						def clazz = className.replaceAll("/", ".")
						c.methods.each { m ->
							if (method.name.matches(m.mrex)) {
								m.addLocalVariable.each { v ->
									if (conf.debug)
										println "TRACEAGENT addLocalVariable ${className}.${method.name}:: ${v}"
										def t = v.split()[0]
										def n = v.split()[1]
									method.addLocalVariable(v.split()[1], CtClass[v.split()[0] + 'Type']);
								}
								m.insertBefore.each { l ->
									def line = l.replace("<<class>>", className).replace("<<method>>", method.name).replace("<<clazz>>", clazz)
									if (conf.debug)
										println "TRACEAGENT insertBefore ${className}.${method.name}:: ${line}"
									method.insertBefore(line)
								}
								m.insertAfter.each { l ->
									def line = l.toString().replace("<<class>>", className).replace("<<method>>", method.name).replace("<<clazz>>", clazz)
									if (conf.debug)
										println "TRACEAGENT insertAfter ${className}.${method.name}:: ${line}"
									method.insertAfter(line)
								}
							}
						}
					}
					byteCode = ctClass.toBytecode();
					ctClass.detach();
					System.out.println("TRACEAGENT Instrumentation complete. ${className}");
				} catch (Throwable ex) {
					System.out.println("TRACEAGENT Exception: " + ex);
					ex.printStackTrace();
				}
				
			}
		}
		return byteCode;
	}
}
