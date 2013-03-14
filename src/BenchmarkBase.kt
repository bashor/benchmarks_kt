import java.text.NumberFormat

abstract class BenchmarkBase(val name: String) {
    // The benchmark code.
    // This function is not used, if both [warmup] and [exercise] are overwritten.
    abstract fun run()

    // Runs a short version of the benchmark. By default invokes [run] once.
    fun warmup() {
        run();
    }

    // Exercices the benchmark. By default invokes [run] 10 times.
    fun exercise() {
        10 times {
            run()
        }
    }

    // Measures the score for this benchmark by executing it repeately until
    // time minimum has been reached.
    fun measureFor(timeMinimum: Int, f: () -> Unit): Double {
        var iter = 0

        val timeMinimumNS = timeMinimum * 1000000
        val startTime = System.nanoTime()
        while (true) {
            val elapsed = System.nanoTime() - startTime;
            if (elapsed >= timeMinimumNS)
                return elapsed / 1000.0 / iter.toDouble();
            f();
            iter++;
        }
    }

    // Measures the score for the benchmark and returns it.
    fun measure(): Double {
        // Warmup for at least 100ms. Discard result.
        measureFor(100) { this.warmup(); };

        // Run the benchmark for at least 2000ms.
        val result = measureFor(2000) { this.exercise(); };
        return result;
    }

    fun Double.toString(format: String) = java.lang.String.format(format, this)

    fun report() {
        val score = measure();
//        println("$name: ${NumberFormat.getInstance().format(score)} us.");
//        println("$name: ${score.toLong()} us.");
        println("$name: ${score.toString("%f")} us.");
    }
}
