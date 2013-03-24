package havlak

import java.util.ArrayList

public open class BasicBlock(val name : Int) {
    public open fun dump() : Unit {
        System.out.format("BB#%03d: ", getName())
        if (inEdges.size > 0)
        {
            System.out.format("in : ")
            for (bb : BasicBlock? in inEdges)
            {
                System.out.format("BB#%03d ", bb?.getName())
            }
        }

        if (outEdges.size > 0)
        {
            System.out.format("out: ")
            for (bb : BasicBlock? in outEdges)
            {
                System.out.format("BB#%03d ", bb?.getName())
            }
        }

        System.out.println()
    }
    public open fun getName() : Int {
        return name
    }
    public open fun getInEdges() : List<BasicBlock?>? {
        return inEdges
    }
    public open fun getOutEdges() : List<BasicBlock?>? {
        return outEdges
    }
    public open fun getNumPred() : Int {
        return inEdges.size
    }
    public open fun getNumSucc() : Int {
        return outEdges.size
    }
    public open fun addOutEdge(to : BasicBlock?) : Unit {
        outEdges.add(to)
    }
    public open fun addInEdge(from : BasicBlock?) : Unit {
        inEdges.add(from)
    }
    private var inEdges = ArrayList<BasicBlock?>(2)
    private var outEdges = ArrayList<BasicBlock?>(2)

    class object {
        var numBasicBlocks : Int = 0
        public open fun getNumBasicBlocks() : Int {
            return numBasicBlocks
        }
    }

    {
        ++numBasicBlocks
    }
}
