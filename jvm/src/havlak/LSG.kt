package havlak

import java.util.ArrayList

class LSG {
    class object {
        var loopCounter = 0;
    }

    val loops = ArrayList<SimpleLoop>()
    val root = SimpleLoop();

    {
        root.nestingLevel = 0
        root.counter = loopCounter
        loopCounter++
        loops.add(this.root)
    }

    fun createNewLoop(): SimpleLoop {
        val loop = SimpleLoop()
        loop.counter = loopCounter;
        loopCounter++
        return loop
    }

    fun addLoop(loop: SimpleLoop) = loops.add(loop)

    fun dumpRec(loop: SimpleLoop, indent: Int) {
        loop.dump(indent);

        for (c in loop.children) {
            dumpRec(c, indent + 1);
        }
    }

    fun dump() = dumpRec(root,0)

    fun max(a: Int, b: Int) = if (a>b) a else b

    fun calculateNestingLevelRec(loop: SimpleLoop, depth: Int) {
        loop.depthLevel = depth;
        for (c in loop.children) {
            calculateNestingLevelRec(c, depth + 1);

            loop.nestingLevel = max(loop.nestingLevel, 1 + c.nestingLevel)
        }
    }

    fun calculateNestingLevel() {
        for (l in this.loops) {
            if (!l.isRoot) {
                if (l.parent == null) {
                    l.parent = this.root
                }
            }
        }

        this.calculateNestingLevelRec(this.root, 0);
    }

    fun getNumLoops() = loops.size;
}