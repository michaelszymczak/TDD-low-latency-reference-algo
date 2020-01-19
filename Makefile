run:
	@./gradlew clean test distZip && unzip -q build/distributions/tdd-reference-algo.zip -d build/distributions
	@echo "run with ./build/distributions/tdd-reference-algo/bin/tdd-reference-algo"
	@echo "--------------------------"
	@./build/distributions/tdd-reference-algo/bin/tdd-reference-algo

perfQueue:
	@./gradlew clean distZip && unzip -q build/distributions/tdd-reference-algo.zip -d build/distributions
	@echo "run with ./build/distributions/tdd-reference-algo/bin/tdd-reference-algo perfQueue"
	@echo "--------------------------"
	@./build/distributions/tdd-reference-algo/bin/tdd-reference-algo perfQueue

perfPricer:
	@./gradlew clean distZip && unzip -q build/distributions/tdd-reference-algo.zip -d build/distributions
	@echo "run with ./build/distributions/tdd-reference-algo/bin/tdd-reference-algo perfPricer"
	@echo "--------------------------"
	@./build/distributions/tdd-reference-algo/bin/tdd-reference-algo perfPricer

perfRefPricer:
	@./gradlew clean distZip && unzip -q build/distributions/tdd-reference-algo.zip -d build/distributions
	@echo "run with ./build/distributions/tdd-reference-algo/bin/tdd-reference-algo perfRefPricer"
	@echo "--------------------------"
	@./build/distributions/tdd-reference-algo/bin/tdd-reference-algo perfRefPricer

