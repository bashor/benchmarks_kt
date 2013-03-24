package havlak

import kotlin.concurrent.thread
import java.util.ArrayList

class SimpleLoop // : Hashable
{

    //    public override fun equals(other: Any?): Boolean {
    //        throw UnsupportedOperationException()
    //    }

    val basicBlocks = ArrayList<BasicBlock>();
    val children = ArrayList<SimpleLoop>()
    var parent: SimpleLoop? = null
        set(p: SimpleLoop?) {
            $parent = p;
            p?.addChildLoop(this);
        }
    var header: BasicBlock? = null
        set(bb: BasicBlock?) {
            basicBlocks.add(bb!!)
            $header = bb
        }

    var isRoot = false
    var isReducible = true
    var counter = 0
    var nestingLevel = 0
        set(level: Int) {
            $nestingLevel = level;
            if (level == 0) {
                isRoot = true;
            }
        }
    var depthLevel = 0

    fun hashCode() = counter

    fun addNode(bb: BasicBlock) = basicBlocks.add(bb)
    fun addChildLoop(loop: SimpleLoop) = children.add(loop)

    fun dump(indent: Int) {
        var res = ""
        for (i in 0..indent - 1) {
            res += "  ";
        }

        print("$res loop-$counter, nest: $nestingLevel, depth $depthLevel");
        if (isReducible == false) {
            print("  irreducible");
        }
    }
}

