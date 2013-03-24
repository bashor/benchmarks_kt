package havlak

import java.util.ArrayList
import java.util.HashMap

public open class CFG() {
    public open fun createNode(name : Int) : BasicBlock? {
        var node : BasicBlock?
        if (!basicBlockMap.containsKey(name))
        {
            node = BasicBlock(name)
            basicBlockMap.put(name, node)
        }
        else
        {
            node = basicBlockMap.get(name)
        }
        if (getNumNodes() == 1)
        {
            startNode = node
        }

        return node
    }
    public open fun dump() : Unit {
        for (bb : BasicBlock? in basicBlockMap.values())
        {
            bb?.dump()
        }
    }
    public open fun addEdge(edge : BasicBlockEdge?) : Unit {
        edgeList.add(edge)
    }
    public open fun getNumNodes() : Int {
        return basicBlockMap.size()
    }
    public open fun getStartBasicBlock() : BasicBlock? {
        return startNode
    }
    public open fun getDst(edge : BasicBlockEdge?) : BasicBlock? {
        return edge?.getDst()!!
    }
    public open fun getSrc(edge : BasicBlockEdge?) : BasicBlock? {
        return edge?.getSrc()!!
    }
    public open fun getBasicBlocks() : Map<Int, BasicBlock?> {
        return basicBlockMap
    }
    private var basicBlockMap = HashMap<Int, BasicBlock?>()
    private var startNode : BasicBlock? = null
    private var edgeList = ArrayList<BasicBlockEdge?>()
}
