package havlak

import java.util.ArrayList
import java.util.HashMap

class CFG {
    val basicBlockMap = HashMap<Int, BasicBlock>()
    val edgeList = ArrayList<BasicBlockEdge>()
    var startNode: BasicBlock? = null;

    fun createNode(name: Int): BasicBlock {
        val node: BasicBlock =
                if (basicBlockMap.containsKey(name)) {
                    basicBlockMap[name]!!
                } else {
                    val newNode = BasicBlock(name);
                    basicBlockMap[name] = newNode;
                    newNode
                }

        if (getNumNodes() == 1) {
            startNode = node;
        }
        return node;
    }

    void dump() {
        for (k in basicBlockMap.values()) {
            k.dump();
        }
    }

    fun addEdge(edge: BasicBlockEdge) = edgeList.add(edge)

    fun getNumNodes() = basicBlockMap.size;

    fun getDst(edge: BasicBlockEdge) = edge.to;
    fun getSrc(edge: BasicBlockEdge) = edge.from;
}