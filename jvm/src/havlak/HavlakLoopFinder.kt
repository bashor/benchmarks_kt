package havlak

import java.util.ArrayList
import java.util.HashMap
import havlak.HavlakLoopFinder.UnionFindNode
import java.util.LinkedList

import kotlin.concurrent.thread

public open class HavlakLoopFinder(cfg : CFG?, lsg : LSG?) {
    public open fun getMaxMillis() : Long {
        return maxMillis
    }
    public open fun getMinMillis() : Long {
        return minMillis.toLong()
    }
    public open class UnionFindNode() {
        public open fun initNode(bb : BasicBlock?, dfsNumber : Int) : Unit {
            this.parent = this
            this.bb = bb
            this.dfsNumber = dfsNumber
            this.loop = null
        }
        public open fun findSet() : UnionFindNode? {
            var nodeList = ArrayList<UnionFindNode?>(2)
            var node : UnionFindNode? = this
            while (node != node?.getParent())
            {
                if (node?.getParent() != node?.getParent()?.getParent())
                {
                    nodeList.add(node)
                }

                node = node?.getParent()
            }
            var len : Int = nodeList.size()
            for (i in 0..len - 1) {
                var iter : UnionFindNode? = nodeList.get(i)!!
                iter?.setParent(node?.getParent())
            }
            return node
        }
        open fun union(basicBlock : UnionFindNode?) : Unit {
            setParent(basicBlock)
        }
        open fun getParent() : UnionFindNode? {
            return parent
        }
        open fun getBb() : BasicBlock? {
            return bb
        }
        open fun getLoop() : SimpleLoop? {
            return loop
        }
        open fun getDfsNumber() : Int {
            return dfsNumber
        }
        open fun setParent(parent : UnionFindNode?) : Unit {
            this.parent = parent
        }
        open fun setLoop(loop : SimpleLoop?) : Unit {
            this.loop = loop
        }
        private var parent : UnionFindNode? = null
        private var bb : BasicBlock? = null
        private var loop : SimpleLoop? = null
        private var dfsNumber : Int = 0


    }
    open fun isAncestor(w : Int, v : Int, last : IntArray?) : Boolean {
        return ((w <= v) && (v <= last!![w]))
    }
    open fun doDFS(currentNode : BasicBlock?, nodes : Array<UnionFindNode?>?, number : HashMap<BasicBlock?, Int>?, last : IntArray?, current : Int) : Int {
        nodes!![current]?.initNode(currentNode, current)
        number?.put(currentNode, current)
        var lastid : Int = current
        var len : Int = currentNode?.getOutEdges()?.size()!!
        for (i in 0..len - 1) {
            var target : BasicBlock? = currentNode?.getOutEdges()?.get(i)!!
            if ((number?.get(target))!! == UNVISITED)
            {
                lastid = doDFS(target, nodes, number, last, lastid + 1)
            }

        }
        last?.set(number!!.get(currentNode), lastid)
        return lastid
    }
    public open fun findLoops() : Unit {
        if ((cfg?.getStartBasicBlock()) == null)
        {
            return
        }

        var startMillis = System.currentTimeMillis()
        var size : Int = cfg?.getNumNodes()!!

        nonBackPreds.clear()
        backPreds.clear()
        number.clear()

        if (size > maxSize)
        {
            header = IntArray(size)
            `type` = arrayOfNulls<BasicBlockClass?>(size)
            last = IntArray(size)
            nodes = arrayOfNulls<UnionFindNode?>(size)
            maxSize = size
        }

        for (i in 0..size - 1) {
            nonBackPreds.add((if (freeListSet.size == 0)
                IntegerSet()
            else
                freeListSet.removeFirst()?.clear()))
            backPreds.add((if (freeListList.size == 0)
                IntegerList()
            else
                freeListList.removeFirst()?.clear()))
            nodes?.set(i, UnionFindNode())
        }
        for (bbIter in cfg!!.getBasicBlocks().values())
        {
            number.put(bbIter, UNVISITED)
        }
        doDFS((cfg?.getStartBasicBlock())!!, nodes, number, last, 0)
        for (w in 0..size - 1) {
            header?.set(w, 0)
            `type`?.set(w, BasicBlockClass.BB_NONHEADER)
            var nodeW : BasicBlock? = nodes!![w]?.getBb()
            if (nodeW == null)
            {
                `type`?.set(w, BasicBlockClass.BB_DEAD)
                continue
            }

            if ((nodeW?.getNumPred())!! > 0)
            {
                var len1 : Int = nodeW?.getInEdges()?.size()!!
                for (i in 0..len1 - 1) {
                    var nodeV : BasicBlock? = nodeW?.getInEdges()?.get(i)!!
                    var v : Int = number.get(nodeV)!!
                    if (v == UNVISITED)
                    {
                        continue
                    }

                    if (isAncestor(w, v, last))
                    {
                        backPreds.get(w)?.add(v)
                    }
                    else
                    {
                        nonBackPreds.get(w)?.add(v)
                    }
                }
            }

        }
        header?.set(0, 0)
        var w : Int = size - 1
        while (w >= 0)
        {

            var nodePool : LinkedList<UnionFindNode?>? = LinkedList<UnionFindNode?>()
            var nodeW : BasicBlock? = nodes!![w]?.getBb()
            if (nodeW == null)
            {
                continue
            }

            var len : Int = backPreds.get(w)?.size()!!
            for (i in 0..len - 1) {
                var v : Int = backPreds.get(w)?.get(i)!!
                if (v != w)
                {
                    nodePool?.add(nodes!![v]?.findSet())
                }
                else
                {
                    `type`?.set(w, BasicBlockClass.BB_SELF)
                }
            }
            var workList : LinkedList<UnionFindNode?>? = LinkedList<UnionFindNode?>()
            for (niter : UnionFindNode? in nodePool!!)
                workList?.add(niter)
            if ((nodePool?.size())!! != 0)
            {
                `type`?.set(w, BasicBlockClass.BB_REDUCIBLE)
            }

            while (!workList.orEmpty().isEmpty())
            {
                var x : UnionFindNode? = workList?.getFirst()!!
                workList?.removeFirst()
                var nonBackSize : Int = nonBackPreds.get(x?.getDfsNumber())?.size()!!
                if (nonBackSize > MAXNONBACKPREDS)
                {
                    return
                }

                var curr : IntegerSet? = nonBackPreds.get(x?.getDfsNumber())!!
                for (i in 0..curr!!.size - 1) {
                    var iter : Int = curr?.arr!![i]
                    var y : UnionFindNode? = nodes?.get(iter)
                    var ydash : UnionFindNode? = y?.findSet()
                    if (!isAncestor(w, (ydash?.getDfsNumber())!!, last))
                    {
                        `type`?.set(w, BasicBlockClass.BB_IRREDUCIBLE)
                        nonBackPreds.get(w)?.add(ydash?.getDfsNumber())
                    }
                    else
                    {
                        if ((ydash?.getDfsNumber())!! != w)
                        {
                            if (!nodePool!!.contains(ydash))
                            {
                                workList?.add(ydash)
                                nodePool?.add(ydash)
                            }

                        }

                    }
                }
            }
            if (((nodePool?.size())!! > 0) || (`type`!![w] == BasicBlockClass.BB_SELF))
            {
                var loop : SimpleLoop? = lsg?.createNewLoop()!!
                loop!!.header = (nodeW!!)
                loop!!.isReducible = (`type`!![w] != BasicBlockClass.BB_IRREDUCIBLE)
                nodes!![w]?.setLoop(loop)
                for (node : UnionFindNode? in nodePool!!)
                {
                    header?.set(node?.getDfsNumber(), w)
                    node?.union(nodes!![w])
                    if (node?.getLoop() != null)
                    {
                        node!!.getLoop()!!.parent = (loop)
                    }
                    else
                    {
                        loop?.addNode(node?.getBb())
                    }
                }
                lsg?.addLoop(loop)
            }

            w--
        }
        var totalMillis = System.currentTimeMillis() - startMillis
        if (totalMillis > maxMillis)
        {
            maxMillis = totalMillis
        }

        if (totalMillis < minMillis)
        {
            minMillis = totalMillis.toInt()
        }

        for (i in 0..size - 1) {
            freeListSet.add(nonBackPreds.get(i))
            freeListList.add(backPreds.get(i))
            nodes?.set(i, UnionFindNode())
        }
    }
    private var cfg : CFG? = null
    private var lsg : LSG? = null
    {
        this.cfg = cfg
        this.lsg = lsg
    }
    class object {
        public enum class BasicBlockClass {
            BB_TOP
            BB_NONHEADER
            BB_REDUCIBLE
            BB_SELF
            BB_IRREDUCIBLE
            BB_DEAD
            BB_LAST
        }
        val UNVISITED : Int = Integer.MAX_VALUE
        val MAXNONBACKPREDS : Int = (32 * 1024)
        open class IntegerSet() : Iterable<Int> {
            var arr = IntArray(2)
            var size : Int = 0
            open fun add(e : Int) : Boolean {
                for (i in 0..size - 1) {
                    if (arr[i] == e)
                    {
                        return false
                    }

                }
                if (size == arr.size)
                {
                    resize++
                    val old = arr
                    arr = IntArray(arr.size shl 1)
                    System.arraycopy(old, 0, arr, 0, old.size)
                }

                arr[size] = e
                size++
                return true
            }
            open fun size() : Int {
                return size
            }
            public override fun iterator() : Iterator<Int> {
                return object : Iterator<Int> {
                    var curr : Int = 0
                    public override fun hasNext() : Boolean {
                        return curr != size
                    }
                    public override fun next() : Int {
                        return arr[curr++]
                    }
                    public open fun remove() : Unit {
                        throw UnsupportedOperationException()
                    }


                }
            }
            open fun clear() : IntegerSet? {
                size = 0
                return this
            }
            {
                total++
            }
            class object {
                var total : Int = 0
                var resize : Int = 0
                {
                    Runtime.getRuntime().addShutdownHook(object : Thread() {
                        public override fun run() : Unit {
                            System.out.println(total.toString() + " = total")
                            System.out.println(resize.toString() + " = resize")
                        }


                    })
                }
            }
        }
        open class IntegerList() : Iterable<Int> {
            var arr = IntArray(2)
            var size : Int = 0
            open fun clear() : IntegerList? {
                size = 0
                return this
            }
            open fun get(i : Int) : Int {
                return arr[i]
            }
            open fun add(e : Int) : Boolean {
                if (size == arr.size)
                {
                    resize++
                    val old = arr
                    arr = IntArray(arr.size shl 1)
                    System.arraycopy(old, 0, arr, 0, old.size)
                }

                arr[size] = e
                size++
                return true
            }
            open fun size() : Int {
                return size
            }
            public override fun iterator() : Iterator<Int> {
                return object : Iterator<Int> {
                    var curr : Int = 0
                    public override fun hasNext() : Boolean {
                        return curr != size
                    }
                    public override fun next() : Int {
                        return arr[curr++]
                    }
                    public open fun remove() : Unit {
                        throw UnsupportedOperationException()
                    }
                }
            }
            {
                total++
            }
            class object {
                var total : Int = 0
                var resize : Int = 0
                {
                    Runtime.getRuntime().addShutdownHook(thread {
                            System.out.println(total.toString() + " = total")
                            System.out.println(resize.toString() + " = resize")
                    })
                }
            }
        }
        var nonBackPreds = ArrayList<IntegerSet?>(2)
        var backPreds = ArrayList<IntegerList?>(2)
        var number = HashMap<BasicBlock?, Int>()
        var maxSize = 0
        var header : IntArray? = null
        var `type` : Array<BasicBlockClass?>? = null
        var last : IntArray? = null
        var nodes : Array<UnionFindNode?>? = null
        var freeListSet = LinkedList<IntegerSet?>()
        var freeListList = LinkedList<IntegerList?>()
        private var maxMillis : Long = 0
        private var minMillis = Integer.MAX_VALUE
    }
}
