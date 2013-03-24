package havlak

import java.util.ArrayList

class BasicBlock(val name: Int) //: Hashable
{
    //    public override fun equals(other: Any?): Boolean {
    //        throw UnsupportedOperationException()
    //    }

    val inEdges = ArrayList<BasicBlock>()
    val outEdges = ArrayList<BasicBlock>()

    fun hashCode() = name

    fun toString() = "BB$name"
    fun getNumPred() = inEdges.size
    fun getNumSucc() = outEdges.size
    fun addInEdge(bb: BasicBlock) = inEdges.add(bb);
    fun addOutEdge(bb: BasicBlock) = outEdges.add(bb);

    fun dump() {
        var res = "  BB#$name"
        if (inEdges.notEmpty()) {
            res += "\tin :";
            for (e in inEdges) {
                res += " " + e.toString();
            }
        }

        if (outEdges.notEmpty()) {
            res += "\tout:";
            for (e in outEdges) {
                res += " " + e.toString();
            }
        }

        println(res);
    }

    class object {
        var numBasicBlocks : Int = 0
    }

    {
        ++numBasicBlocks
    }
}