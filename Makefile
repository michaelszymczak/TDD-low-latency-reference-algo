run:
	@./gradlew clean test distZip && unzip -q build/distributions/TDD-low-latency-reference-algo.zip -d build/distributions
	@echo "run with ./build/distributions/TDD-low-latency-reference-algo/bin/TDD-low-latency-reference-algo"
	@echo "--------------------------"
	@./build/distributions/TDD-low-latency-reference-algo/bin/TDD-low-latency-reference-algo

perfQueue:
	@./gradlew clean distZip && unzip -q build/distributions/TDD-low-latency-reference-algo.zip -d build/distributions
	@echo "run with ./build/distributions/TDD-low-latency-reference-algo/bin/TDD-low-latency-reference-algo perfQueue"
	@echo "--------------------------"
	@./build/distributions/TDD-low-latency-reference-algo/bin/TDD-low-latency-reference-algo perfQueue

perfPricer:
	@./gradlew clean distZip && unzip -q build/distributions/TDD-low-latency-reference-algo.zip -d build/distributions
	@echo "run with ./build/distributions/TDD-low-latency-reference-algo/bin/TDD-low-latency-reference-algo perfPricer"
	@echo "--------------------------"
	@./build/distributions/TDD-low-latency-reference-algo/bin/TDD-low-latency-reference-algo perfPricer

perfRefPricer:
	@./gradlew clean distZip && unzip -q build/distributions/TDD-low-latency-reference-algo.zip -d build/distributions
	@echo "run with ./build/distributions/TDD-low-latency-reference-algo/bin/TDD-low-latency-reference-algo perfRefPricer"
	@echo "--------------------------"
	@./build/distributions/TDD-low-latency-reference-algo/bin/TDD-low-latency-reference-algo perfRefPricer

