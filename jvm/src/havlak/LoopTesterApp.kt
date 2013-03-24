package havlak

open class LoopTesterApp() {
    public open fun buildDiamond(start : Int) : Int {
        var bb0 : Int = start
        BasicBlockEdge(cfg, bb0, bb0 + 1)
        BasicBlockEdge(cfg, bb0, bb0 + 2)
        BasicBlockEdge(cfg, bb0 + 1, bb0 + 3)
        BasicBlockEdge(cfg, bb0 + 2, bb0 + 3)
        return bb0 + 3
    }
    public open fun buildConnect(start : Int, end : Int) : Unit {
        BasicBlockEdge(cfg, start, end)
    }
    public open fun buildStraight(start : Int, n : Int) : Int {
        for (i in 0..n - 1) {
            buildConnect(start + i, start + i + 1)
        }
        return start + n
    }
    public open fun buildBaseLoop(from : Int) : Int {
        var header : Int = buildStraight(from, 1)
        var diamond1 : Int = buildDiamond(header)
        var d11 : Int = buildStraight(diamond1, 1)
        var diamond2 : Int = buildDiamond(d11)
        var footer : Int = buildStraight(diamond2, 1)
        buildConnect(diamond2, d11)
        buildConnect(diamond1, header)
        buildConnect(footer, from)
        footer = buildStraight(footer, 1)
        return footer
    }
    public open fun getMem() : Unit {
        var runtime : Runtime? = Runtime.getRuntime()
        var `val` : Long = (runtime?.totalMemory())!! / 1024
        System.out.println("  Total Memory: " + `val` + " KB")
    }
    public var cfg : CFG? = null
    var lsg : LSG? = null
    private var root : BasicBlock? = null
    {
        cfg = CFG()
        lsg = LSG()
        root = cfg?.createNode(0)
    }
}

fun main(args : Array<String>) {
    System.out.println("Welcome to LoopTesterApp, Java edition")
    System.out.println("Constructing App...")
    var app : LoopTesterApp? = LoopTesterApp()
    app?.getMem()
    System.out.println("Constructing Simple CFG...")
    app?.cfg?.createNode(0)
    app?.buildBaseLoop(0)
    app?.cfg?.createNode(1)
    BasicBlockEdge(app?.cfg, 0, 2)
    System.out.println("15000 dummy loops")
    for (dummyloop in 0..15000 - 1) {
        var finder : HavlakLoopFinder? = HavlakLoopFinder(app?.cfg, app?.lsg)
        finder?.findLoops()
    }
    System.out.println("Constructing CFG...")
    var n : Int = 2
    for (parlooptrees in 0..10 - 1) {
        app?.cfg?.createNode(n + 1)
        app?.buildConnect(2, n + 1)
        n = n + 1
        for (i in 0..100 - 1) {
            var top : Int = n
            n = app!!.buildStraight(n, 1)
            for (j in 0..25 - 1) {
                n = app!!.buildBaseLoop(n)
            }
            var bottom : Int = app?.buildStraight(n, 1)!!
            app?.buildConnect(n, top)
            n = bottom
        }
        app?.buildConnect(n, 1)
    }
    app?.getMem()
    System.out.format("Performing Loop Recognition\n1 Iteration\n")
    var finder : HavlakLoopFinder? = HavlakLoopFinder(app?.cfg, app?.lsg)
    finder?.findLoops()
    app?.getMem()
    System.out.println("Another 50 iterations...")
    for (i in 0..50 - 1) {
        System.out.format(".")
        var finder2 : HavlakLoopFinder? = HavlakLoopFinder(app?.cfg, LSG())
        finder2?.findLoops()
    }
    System.out.println("")
    app?.getMem()
    System.out.println("# of loops: " + (app?.lsg?.getNumLoops())!! + " (including 1 artificial root node)")
    System.out.println("# of BBs  : " + BasicBlock.getNumBasicBlocks())
    System.out.println("# max time: " + (finder?.getMaxMillis())!!)
    System.out.println("# min time: " + (finder?.getMinMillis())!!)
    app?.lsg?.calculateNestingLevel()
}
