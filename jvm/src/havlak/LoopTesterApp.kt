package havlak

open class LoopTesterApp() {
    fun buildDiamond(start : Int) : Int {
        var bb0 : Int = start
        BasicBlockEdge(cfg, bb0, bb0 + 1)
        BasicBlockEdge(cfg, bb0, bb0 + 2)
        BasicBlockEdge(cfg, bb0 + 1, bb0 + 3)
        BasicBlockEdge(cfg, bb0 + 2, bb0 + 3)
        return bb0 + 3
    }
    fun buildConnect(start : Int, end : Int) : Unit {
        BasicBlockEdge(cfg, start, end)
    }
    fun buildStraight(start : Int, n : Int) : Int {
        for (i in 0..n - 1) {
            buildConnect(start + i, start + i + 1)
        }
        return start + n
    }
    fun buildBaseLoop(from : Int) : Int {
        val header = buildStraight(from, 1)
        val diamond1 = buildDiamond(header)
        val d11 = buildStraight(diamond1, 1)
        val diamond2 = buildDiamond(d11)
        val footer = buildStraight(diamond2, 1)
        buildConnect(diamond2, d11)
        buildConnect(diamond1, header)
        buildConnect(footer, from)

        return buildStraight(footer, 1)
    }
    fun getMem() : Unit {
        var runtime : Runtime? = Runtime.getRuntime()
        var `val` : Long = (runtime?.totalMemory())!! / 1024
        println("  Total Memory: " + `val` + " KB")
    }
    val cfg = CFG()
    val lsg = LSG()
    val root =cfg.createNode(0)
}

fun main(args : Array<String>) {
    println("Welcome to LoopTesterApp, Kotlin edition")
    println("Constructing App...")
    var app = LoopTesterApp()
    app.getMem()

    println("Constructing Simple CFG...")
    app.cfg.createNode(0)
    app.buildBaseLoop(0)
    app.cfg.createNode(1)
    BasicBlockEdge(app.cfg, 0, 2)

    println("15000 dummy loops")
    15000 times {
        val finder = HavlakLoopFinder(app.cfg, app.lsg)
        finder.findLoops()
    }

    println("Constructing CFG...")
    var n = 2

    10 times {
        app.cfg.createNode(n + 1)
        app.buildConnect(2, n + 1)
        n = n + 1
        100 times {
            val top : Int = n
            n = app.buildStraight(n, 1)
            25 times {
                n = app.buildBaseLoop(n)
            }
            val bottom = app.buildStraight(n, 1)
            app.buildConnect(n, top)
            n = bottom
        }
        app.buildConnect(n, 1)
    }
    app.getMem()

    println("Performing Loop Recognition\n1 Iteration")
    val finder = HavlakLoopFinder(app.cfg, app.lsg)
    finder.findLoops()
    app.getMem()

    println("Another 50 iterations...")
    50 times {
        print(".")
        val finder2 = HavlakLoopFinder(app.cfg, LSG())
        finder2.findLoops()
    }
    println()

    app.getMem()
    println("# of loops: " + app.lsg.getNumLoops() + " (including 1 artificial root node)")
    println("# of BBs  : " + BasicBlock.numBasicBlocks)
    app.lsg.calculateNestingLevel()
}
