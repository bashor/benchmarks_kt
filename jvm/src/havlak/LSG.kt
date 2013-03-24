package havlak

import java.util.ArrayList

class LSG {
    fun createNewLoop() : SimpleLoop? {
        var loop : SimpleLoop? = SimpleLoop()
        loop!!.counter = loopCounter++
        return loop
    }
    fun addLoop(loop : SimpleLoop?) : Unit {
        loops.add(loop)
    }
    fun dump() : Unit {
        dumpRec(root, 0)
    }
    fun dumpRec(loop : SimpleLoop?, indent : Int) : Unit {
        loop?.dump(indent)
        for (liter : SimpleLoop? in loop!!.children)
            dumpRec(liter, indent + 1)
    }
    fun calculateNestingLevel() : Unit {
        for (liter in loops)
        {
            if (liter != null && liter.isRoot)
            {
                continue
            }

            if (liter?.parent == null)
            {
                liter!!.parent = root
            }

        }
        calculateNestingLevelRec(root, 0)
    }
    fun calculateNestingLevelRec(loop : SimpleLoop, depth : Int) : Unit {
        loop.depthLevel = depth
        for (liter in loop.children)
        {
            calculateNestingLevelRec(liter, depth + 1)
            loop.nestingLevel = Math.max(loop.nestingLevel, 1 + (liter.nestingLevel))
        }
    }
    fun getNumLoops() : Int {
        return loops.size
    }

    val root = SimpleLoop()
    private var loops = ArrayList<SimpleLoop?>()
    private var loopCounter : Int = 0

    {
        root.nestingLevel = 0
        root.counter = loopCounter++
        addLoop(root)
    }
}
