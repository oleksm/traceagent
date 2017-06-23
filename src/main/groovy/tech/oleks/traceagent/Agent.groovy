package tech.oleks.traceagent;

import java.lang.instrument.Instrumentation;

class Agent {
	public static void premain(String agentArgs, Instrumentation inst) {
		println "Executing premain... ${agentArgs}";
		Config.load(agentArgs)
		inst.addTransformer(new Transformer());
	}
}