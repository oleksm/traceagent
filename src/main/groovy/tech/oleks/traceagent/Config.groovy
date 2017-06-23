package tech.oleks.traceagent;

import org.yaml.snakeyaml.Yaml

class Config {

	def static prop

		static def load(def path) {
		def yaml = new Yaml();		
		def conf = yaml.load(new FileInputStream(path));
		println "loaded ${path}: ${conf.size()}"
		prop = conf
	}
}
